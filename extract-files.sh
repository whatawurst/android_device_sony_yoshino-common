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
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/lib/libseemore.so
"${PATCHELF}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/lib64/libseemore.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_alfortlp.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_alfortlpserv.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_alfortrsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_bordeauxrsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_buttercakersc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_canelersc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_cheesesconersc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_dars.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_darsrsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_marblersc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_melonpanrsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_mugichocorsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_pretzchocorsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_raisinrsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_shortcakersc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_spicarsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_sumomolpserv.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_sumomorsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_topporsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsomc_yummyrsc.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsony_fooddetect.so
"${PATCHELF_0_17_2}" --replace-needed "libstdc++.so" "libstdc++_vendor.so" "${DEVICE_COMMON_ROOT}"/vendor/lib/libsony_naruto.so

# Use libhidlbase-v32 for select Android P blobs
"${PATCHELF_0_17_2}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/bin/sony-modem-switcher
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/com.qualcomm.qti.ant@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/com.qualcomm.qti.bluetooth_audio@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/libMiscTaWrapper.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.qti.hardware.qteeconnector@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.qti.hardware.tui_comm@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.qti.hardware.vpp@1.1.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.semc.hardware.light@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.semc.system.idd@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.somc.hardware.camera.cacao@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.somc.hardware.camera.cacao@2.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.somc.hardware.camera.cacao@3.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.somc.hardware.camera.cacao@3.1.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.somc.hardware.camera.device@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib/vendor.somc.hardware.camera.provider@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/com.qualcomm.qti.ant@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/com.qualcomm.qti.bluetooth_audio@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/libMiscTaWrapper.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.display.color@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.display.color@1.1.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.display.color@1.2.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.display.postproc@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.qti.esepowermanager@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.qti.hardware.qdutils_disp@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.qti.hardware.qteeconnector@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.qti.hardware.tui_comm@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.qti.hardware.vpp@1.1.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.semc.hardware.light@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.semc.system.idd@1.0.so
"${PATCHELF}" --replace-needed "libhidlbase.so" "libhidlbase-v32.so" "${DEVICE_COMMON_ROOT}"/lib64/vendor.somc.hardware.security.secd@1.0.so

#
# Blobs fixup end
#

"${MY_DIR}"/setup-makefiles.sh
