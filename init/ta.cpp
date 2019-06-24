/*
 * Copyright (c) 2018, Andreas Schneider <asn@cryptomilk.org>
 * Copyright (c) 2018, The LineageOS Project
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *  * Neither the name of The Linux Foundation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <sys/un.h>
#include <sys/socket.h>
#include <unistd.h>
#include <string>

#define MIN(a,b) ((a) < (b) ? (a) : (b))

#define TAD_SOCKET "/dev/socket/tad"

using std::string;

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

static int tad_open(void)
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
        return -1;
    }

    rc = connect(s, (struct sockaddr *)&tad_un, sunlen);
    if (rc != 0) {
        close(s);
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
        tad_close(s);
        return -1;
    }

    /* Send 0 byte */
    bwritten = write(s, &null_byte, sizeof(null_byte));
    if (bwritten != sizeof(null_byte)) {
        tad_close(s);
        return -1;
    }

    put_u32(cmd_buf, 0x0001);
    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        tad_close(s);
        return -1;
    }

    put_u32(cmd_buf, 0x0001);
    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        tad_close(s);
        return -1;
    }

    /* Send request for unit */
    put_u32(cmd_buf, unit);

    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        tad_close(s);
        return -1;
    }

    nread = read(s, size_buf, sizeof(size_buf));
    if (nread != 4) {
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

static int tad_read(uint32_t unit, uint8_t *buf, uint32_t size)
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
        tad_close(s);
        return -1;
    }

    /* Send 0 byte */
    bwritten = write(s, &null_byte, sizeof(null_byte));
    if (bwritten != sizeof(null_byte)) {
        tad_close(s);
        return -1;
    }

    put_u32(cmd_buf, 0x0001);
    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        tad_close(s);
        return -1;
    }

    put_u32(cmd_buf, 0x0000);
    bwritten = write(s, cmd_buf, sizeof(cmd_buf));
    if (bwritten != sizeof(cmd_buf)) {
        tad_close(s);
        return -1;
    }

    /* Request unit data */
    put_u32(cmd_size_buf, unit);
    put_u32(cmd_size_buf + 4, size);

    bwritten = write(s, cmd_size_buf, sizeof(cmd_size_buf));
    if (bwritten != (ssize_t)sizeof(cmd_size_buf)) {
        tad_close(s);
        return -1;
    }

    /* Get the size of the returned buffer */
    nread = read(s, size_buf, sizeof(size_buf));
    if (nread != 4) {
        tad_close(s);
        return -1;
    }

    len = get_u32(size_buf);

    if (len > 0) {
        uint8_t unit_buf[len];

        /* Read the buffer */
        nread = read(s, unit_buf, len);
        if (nread != (ssize_t)len) {
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

string ta_get_cust_active(void)
{
    uint32_t size = 0;
    int rc;

    rc = tad_get_size(0x8A4, &size);
    if (rc != 0 || size == 0) {
        return "";
    }

    uint8_t buf[size];
    char str[size + 1];

    memset(buf, '\0', size);

    rc = tad_read(0x8A4, buf, size);
    if (rc != 0) {
        return "";
    }

    memcpy(str, buf, size);
    /* Make it a string */
    str[size] = '\0';

    std::string ta_cust_active(str);

    return ta_cust_active;
}
