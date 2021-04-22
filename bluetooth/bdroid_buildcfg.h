/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef _BDROID_BUILDCFG_H
#define _BDROID_BUILDCFG_H

#pragma push_macro("PROPERTY_VALUE_MAX")

#if !defined(OS_GENERIC)
#include <cutils/properties.h>
#include <string.h>

static inline const char* getBTDefaultName()
{
    char device[PROPERTY_VALUE_MAX];
    property_get("ro.boot.hardware", device, "");

    if (!strcmp("maple", device)) {
        return "Xperia XZ Premium";
    }

    if (!strcmp("maple_dsds", device)) {
        return "Xperia XZ Premium Dual";
    }

    if (!strcmp("poplar", device)) {
        return "Xperia XZ1";
    }

    if (!strcmp("poplar_canada", device)) {
        return "Xperia XZ1";
    }

    if (!strcmp("poplar_dsds", device)) {
        return "Xperia XZ1 Dual";
    }

    if (!strcmp("poplar_kddi", device)) {
        return "Xperia XZ1";
    }

    if (!strcmp("lilac", device)) {
        return "Xperia XZ1 Compact";
    }

    return "Xperia";
}

#define BTM_DEF_LOCAL_NAME getBTDefaultName()
#endif // OS_GENERIC

// Disables read remote device feature
#define MAX_ACL_CONNECTIONS   16
#define MAX_L2CAP_CHANNELS    16

// Vendor extensions
#define BLE_VND_INCLUDED TRUE

#pragma pop_macro("PROPERTY_VALUE_MAX")
#endif
