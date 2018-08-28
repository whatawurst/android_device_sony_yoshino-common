#include <stdio.h>
#include <ctype.h>
#include <string.h>

#define MIN(a,b) ((a)<(b)?(a):(b))

int miscta_get_unit_size(int unit, int *len);
int miscta_read_unit(int unit, void *buf, int *len);

static void print_asc(const uint8_t *buf, int len)
{
    int i;
    for (i=0; i<len; i++) {
        printf("%c", isprint(buf[i])?buf[i]:'.');
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


int main(void)
{
    int ac_version = 0;
    int cust_version = 0;
    int rc;
    int len = 0;

    printf("dump unit:\n");

    rc = miscta_get_unit_size(0x8A4, &len);
    if (rc != 0) {
        return 1;
    }

    if (len > 0) {
        uint8_t buf[len];

        printf("unit len: %d\n", len);

        rc = miscta_read_unit(0x8A4, buf, &len);
        if (rc != 0) {
            return 1;
        }

        dump_data(buf, len);
    }

    return 0;
}
