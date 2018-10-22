/*
 * Copyright (C) 2007, The Android Open Source Project
 * Copyright (c) 2016, The CyanogenMod Project
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

#include <android-base/file.h>
#include <android-base/logging.h>
#include <android-base/properties.h>
#include <sstream>

#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>

#include "vendor_init.h"
#include "property_service.h"

#include "ta.h"

using android::base::GetProperty;
using android::base::WaitForProperty;
using android::base::ReadFileToString;
using android::init::property_set;

using namespace std::chrono_literals;

static void load_properties_from_file(const char *, const char *);

static void load_properties(char *data, const char *filter)
{
    char *key, *value, *eol, *sol, *tmp, *fn;
    size_t flen = 0;
    if (filter) {
        flen = strlen(filter);
    }
    sol = data;
    while ((eol = strchr(sol, '\n'))) {
        key = sol;
        *eol++ = 0;
        sol = eol;
        while (isspace(*key)) key++;
        if (*key == '#') continue;
        tmp = eol - 2;
        while ((tmp > key) && isspace(*tmp)) *tmp-- = 0;
        if (!strncmp(key, "import ", 7) && flen == 0) {
            fn = key + 7;
            while (isspace(*fn)) fn++;
            key = strchr(fn, ' ');
            if (key) {
                *key++ = 0;
                while (isspace(*key)) key++;
            }
            load_properties_from_file(fn, key);
        } else {
            value = strchr(key, '=');
            if (!value) continue;
            *value++ = 0;
            tmp = value - 2;
            while ((tmp > key) && isspace(*tmp)) *tmp-- = 0;
            while (isspace(*value)) value++;
            if (flen > 0) {
                if (filter[flen - 1] == '*') {
                    if (strncmp(key, filter, flen - 1)) continue;
                } else {
                    if (strcmp(key, filter)) continue;
                }
            }
            property_set(key, value);
        }
    }
}

static void load_properties_from_file(const char* filename, const char* filter) {
    std::string data;

    if (!ReadFileToString(filename, &data)) {
        PLOG(WARNING) << "Couldn't load property file";
        return;
    }
    data.push_back('\n');
    load_properties(&data[0], filter);
    LOG(INFO) << "Loaded properties from " << filename << ".";
    return;
}

void vendor_load_properties() {

    // Wait for up to 2 seconds for /oem to be ready before we proceed (it should take much less...)
    WaitForProperty("ro.boot.oem.ready", "true", 2s);

    LOG(INFO) << "Loading region- and carrier-specific properties from /oem.";

    // Load the carrier-independent props
    LOG(INFO) << "Loading properties from /oem/system-properties/cust.prop";
    load_properties_from_file("/oem/system-properties/cust.prop", NULL);

    // Get the active customization id from miscTA
    std::string cust_id = ta_get_cust_active();

    // If no customization is set, load the basic set of config props.
    if (cust_id.empty()) {
        LOG(INFO) << "No active customization detected.";
        LOG(INFO) << "Loading properties from /oem/system-properties/config.prop";
        load_properties_from_file("/oem/system-properties/config.prop", NULL);
    } else {
    // Otherwise, load the carrier-specific ones (these also contain the basic ones).
        LOG(INFO) << "Active customization detected: " << cust_id;

        std::stringstream ss;
        ss << "/oem/system-properties/" << cust_id << "/config.prop";    
        std::string cust_path = ss.str();
        LOG(INFO) << "Loading properties from " << cust_path;
        load_properties_from_file(cust_path.c_str(), NULL);
    }
}
