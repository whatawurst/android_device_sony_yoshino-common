#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>

/*
 * [] Hooking!
 *
 * miscta_get_unit_size:
 * [] WRITE(fd=3, count=4):
 * [0000] 00 00 00 0d                                       ....
 * This is probably a write_uint32
 *
 * [] WRITE(fd=3, count=1):
 * [0000] 00                                                .
 * This is probably a write_uint8
 *
 * [] WRITE(fd=3, count=4):
 * [0000] 00 00 00 01                                       ....
 * [] WRITE(fd=3, count=4):
 * [0000] 00 00 00 01                                       ....
 * [] WRITE(fd=3, count=4):
 * [0000] 00 00 08 a4                                       ....
 *
 * unit len: 14
 *
 * mscta_read_unit:
 * [] WRITE(fd=3, count=4):
 * [0000] 00 00 00 11                                       ....
 * [] WRITE(fd=3, count=1):
 * [0000] 00                                                .
 * [] WRITE(fd=3, count=4):
 * [0000] 00 00 00 01                                       ....
 * [] WRITE(fd=3, count=4):
 * [0000] 00 00 00 00                                       ....
 * [] WRITE(fd=3, count=8):
 * [0000] 00 00 08 a4 00 00 00 0e                            ........
 * [0000] 00 00 00 00 00 00 00 00   00 00 00 00 00 00        ........ ......
 */

#define MIN(a,b) ((a) < (b) ? (a) : (b))

#define TAD_SOCKET "/dev/socket/tad"

static int tad_sock = -1;

static void print_asc(const uint8_t *buf, int len)
{
    int i;
    for (i=0; i<len; i++) {
        printf("%c", isprint(buf[i]) ? buf[i] : '.');
    }
}

static void dump_data(const uint8_t *buf, int len)
{
    int i=0;
    static const uint8_t empty[16] = { 0, };

    if (len<=0) return;

    for (i=0; i<len;) {

        if (i%16 == 0) {
            if ((i > 0) &&
                    (len > i+16) &&
                    (memcmp(&buf[i], &empty, 16) == 0))
            {
                i +=16;
                continue;
            }

            if (i<len)  {
                printf("[%04X] ",i);
            }
        }

        printf("%02x ", buf[i]);
        i++;

        if (i%8 == 0) printf("  ");
        if (i%16 == 0) {
            print_asc(&buf[i-16],8); printf(" ");
            print_asc(&buf[i-8],8); printf("\n");
        }
    }

    if (i%16) {
        int n;
        n = 16 - (i%16);
        printf(" ");
        if (n>8) printf(" ");
        while (n--) printf("   ");
        n = MIN(8,i%16);
        print_asc(&buf[i-(i%16)],n); printf( " " );
        n = (i%16) - n;
        if (n>0) print_asc(&buf[i-n],n);
        printf("\n");
    }
}

static uint32_t get_u32(const void *vp)
{
  const uint8_t *p = (const uint8_t *)vp;
  uint32_t v;

  v  = (uint32_t)p[0] << 24;
  v |= (uint32_t)p[1] << 16;
  v |= (uint32_t)p[2] << 8;
  v |= (uint32_t)p[3];

  return v;
}

static void put_u32(void *vp, uint32_t v)
{
  uint8_t *p = (uint8_t *)vp;

  p[0] = (uint8_t)(v >> 24) & 0xff;
  p[1] = (uint8_t)(v >> 16) & 0xff;
  p[2] = (uint8_t)(v >> 8) & 0xff;
  p[3] = (uint8_t)v & 0xff;
}

int tad_open(void)
{
    struct sockaddr_un tad_un = {
        .sun_family = AF_UNIX,
        .sun_path = TAD_SOCKET,
    };
    size_t sunlen = sizeof(struct sockaddr_un);
    int rc;
    int s;

    s = socket(AF_UNIX, SOCK_STREAM, 0);
    if (s == -1) {
        perror("Failed to create socket");
        return -1;
    }

    rc = connect(s, (struct sockaddr *)&tad_un, sunlen);
    if (rc != 0) {
        close(s);
        perror("Failed to connect to tad");
        return rc;
    }

    return s;
}

static int tad_close(int sock)
{
    if (sock != -1) {
        return close(sock);
    }

    return 0;
}

static int tad_get_size(uint32_t unit, uint32_t *size)
{
    uint8_t cmd_buf[4] = {0};
    uint8_t size_buf[4] = {0};
    uint8_t null_byte = 0;
    ssize_t bwritten;
    ssize_t nread;
    uint32_t len;
    int s;

    s = tad_open();
    if (s == -1) {
        return -1;
    }

    /* Send CMD */
    put_u32(cmd_buf, 0x000d);

    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    /* Send 0 byte */
    bwritten = write(s, &null_byte, sizeof(null_byte));
    if (bwritten != sizeof(null_byte)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    put_u32(cmd_buf, 0x0001);
    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    put_u32(cmd_buf, 0x0001);
    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    /* Send request for unit */
    put_u32(cmd_buf, unit);

    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    nread = read(s, size_buf, sizeof(size_buf));
    if (nread != 4) {
        perror("read failed");
        tad_close(s);
        return -1;
    }

    len = get_u32(size_buf);

    if (len > 0) {
        uint8_t buf[len];
        uint32_t offset = 4;
        uint32_t x;

        nread = read(s, buf, len);
        if (nread != (ssize_t)len) {
            perror("read failed");
            tad_close(s);
            return -1;
        }

        x = get_u32(buf);
        while (offset < len) {
            x += get_u32(buf + offset);
            offset += 4;
        }

        *size = x;
    }

    tad_close(s);

    return 0;
}

int tad_read(uint32_t unit, uint8_t *buf, uint32_t size)
{
    uint8_t cmd_size_buf[8] = {0};
    uint8_t cmd_buf[4] = {0};
    uint8_t size_buf[4] = {0};
    uint8_t null_byte = 0;
    ssize_t bwritten;
    ssize_t nread;
    uint32_t len;
    int s;

    s = tad_open();
    if (s == -1) {
        return -1;
    }

    /* Send CMD */
    put_u32(cmd_buf, 0x0011);

    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    /* Send 0 byte */
    bwritten = write(s, &null_byte, sizeof(null_byte));
    if (bwritten != sizeof(null_byte)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    put_u32(cmd_buf, 0x0001);
    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    put_u32(cmd_buf, 0x0000);
    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    /* Request unit data */
    put_u32(cmd_size_buf, unit);
    put_u32(cmd_size_buf + 4, size);

    bwritten = write(s, cmd_size_buf, sizeof(cmd_size_buf));
    if (bwritten != (ssize_t)sizeof(cmd_size_buf)) {
        perror("write failed");
        tad_close(s);
        return -1;
    }

    /* Get the size of the returned buffer */
    nread = read(s, size_buf, sizeof(size_buf));
    if (nread != 4) {
        perror("read failed");
        tad_close(s);
        return -1;
    }

    len = get_u32(size_buf);

    if (len > 0) {
        uint8_t unit_buf[len];

        /* Read the buffer */
        nread = read(s, unit_buf, len);
        if (nread != (ssize_t)len) {
            perror("read failed");
            tad_close(s);
            return -1;
        }

        if (len > size) {
            memcpy(buf, unit_buf + (len - size), size);
        }
    }

    tad_close(s);
    return 0;
}

int main(void)
{
    int rc;
    uint32_t size = 0;

    rc = tad_get_size(0x8A4, &size);
    if (rc != 0) {
        return 1;
    }

    printf("tad_get_size: %u\n", size);

    if (size > 0) {
        uint8_t buf[size];
        char str[size + 1];

        memset(buf, '\0', size);
        printf("tad_read:\n");

        rc = tad_read(0x8A4, buf, size);
        if (rc != 0) {
            return 1;
        }

        printf("DUMP:\n");
        dump_data(buf, size);
        printf("\n");

        memcpy(str, buf, size);
        /* Make it a string */
        str[size] = '\0';

        printf("STRING: %s\n", str);
    }

    return 0;
}
