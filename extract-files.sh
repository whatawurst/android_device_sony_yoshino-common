#!/bin/bash
#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017-2020 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

set -e

DEVICE_COMMON=yoshino-common
VENDOR=sony

# Load extract_utils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "${MY_DIR}" ]]; then MY_DIR="${PWD}"; fi

ANDROID_ROOT="${MY_DIR}/../../.."

HELPER="${ANDROID_ROOT}/tools/extract-utils/extract_utils.sh"
if [ ! -f "${HELPER}" ]; then
    echo "Unable to find helper script at ${HELPER}"
    exit 1
fi
source "${HELPER}"

# Default to sanitizing the vendor folder before extraction
CLEAN_VENDOR=true

KANG=
SECTION=

while [ "${#}" -gt 0 ]; do
    case "${1}" in
        -n | --no-cleanup )
                CLEAN_VENDOR=false
                ;;
        -k | --kang )
                KANG="--kang"
                ;;
        -s | --section )
                SECTION="${2}"; shift
                CLEAN_VENDOR=false
                ;;
        * )
                SRC="${1}"
                ;;
    esac
    shift
done

if [ -z "${SRC}" ]; then
    SRC="adb"
fi


# Initialize the helper
setup_vendor "${DEVICE_COMMON}" "${VENDOR}" "${ANDROID_ROOT}" true "${CLEAN_VENDOR}"

extract "${MY_DIR}/proprietary-files.txt" "${SRC}" "${KANG}" --section "${SECTION}"
extract "${MY_DIR}/proprietary-files-vendor.txt" "${SRC}" "${KANG}" --section "${SECTION}"

#
# Blobs fixup start
#

DEVICE_COMMON_ROOT="${ANDROID_ROOT}"/vendor/"${VENDOR}"/"${DEVICE_COMMON}"/proprietary

# Let ffu load ufs firmare files from /etc
sed -i 's/\/lib\/firmware\/ufs/\/etc\/firmware\/ufs/g' "${DEVICE_COMMON_ROOT}"/vendor/bin/ffu

# Change xml version from 2.0 to 1.0
sed -i 's/version\=\"2\.0\"/version\=\"1\.0\"/g' "${DEVICE_COMMON_ROOT}"/product/etc/permissions/vendor.qti.hardware.data.connection-V1.0-java.xml
sed -i 's/version\=\"2\.0\"/version\=\"1\.0\"/g' "${DEVICE_COMMON_ROOT}"/product/etc/permissions/vendor.qti.hardware.data.connection-V1.1-java.xml

# Add a restorecon for /persist/wlan to taimport_vendor.rc
sed -i '4 a\    restorecon /persist/wlan' "${DEVICE_COMMON_ROOT}"/vendor/etc/init/taimport_vendor.rc

# Patch lib-imsvideocodec.so to load libgui_shim.so
grep -q "libgui_shim.so" "${DEVICE_COMMON_ROOT}"/system_ext/lib64/lib-imsvideocodec.so || "${PATCHELF}" --add-needed "libgui_shim.so" "${DEVICE_COMMON_ROOT}"/system_ext/lib64/lib-imsvideocodec.so

# Replace libstdc++.so with libstdc++_vendor.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/lib/libjni_imageutil.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/lib/libjni_snapcammosaic.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/lib/libjni_snapcamtinyplanet.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/lib/libseemore.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/lib64/libseemore.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_alfortlp.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_alfortlpserv.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_alfortrsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_bordeauxrsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_buttercakersc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_canelersc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_cheesesconersc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_dars.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_darsrsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_marblersc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_melonpanrsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_mugichocorsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_pretzchocorsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_raisinrsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_shortcakersc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_spicarsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_sumomolpserv.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_sumomorsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_topporsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_yummyrsc.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsony_fooddetect.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsony_naruto.so

#
# Blobs fixup end
#

"${MY_DIR}"/setup-makefiles.sh
