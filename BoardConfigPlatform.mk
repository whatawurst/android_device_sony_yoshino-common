#
# Copyright (C) 2017 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

PLATFORM_PATH := device/sony/yoshino-common

### BOARD
BOARD_USES_QCOM_HARDWARE := true
BOARD_VENDOR := sony
TARGET_BOARD_PLATFORM := msm8998
TARGET_BOARD_PLATFORM_GPU := qcom-adreno540

### PROCESSOR
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-a
TARGET_CPU_ABI := arm64-v8a
TARGET_CPU_ABI2 :=
TARGET_CPU_VARIANT := generic
TARGET_CPU_VARIANT_RUNTIME := cortex-a73

TARGET_2ND_ARCH := arm
TARGET_2ND_ARCH_VARIANT := armv8-a
TARGET_2ND_CPU_ABI := armeabi-v7a
TARGET_2ND_CPU_ABI2 := armeabi
TARGET_2ND_CPU_VARIANT := generic
TARGET_2ND_CPU_VARIANT_RUNTIME := cortex-a73

### KERNEL
BOARD_KERNEL_CMDLINE += user_debug=31
BOARD_KERNEL_CMDLINE += ehci-hcd.park=3
BOARD_KERNEL_CMDLINE += lpm_levels.sleep_disabled=1
BOARD_KERNEL_CMDLINE += sched_enable_hmp=1
BOARD_KERNEL_CMDLINE += sched_enable_power_aware=1
BOARD_KERNEL_CMDLINE += service_locator.enable=1
BOARD_KERNEL_CMDLINE += swiotlb=2048
BOARD_KERNEL_CMDLINE += androidboot.configfs=true
BOARD_KERNEL_CMDLINE += androidboot.usbcontroller=a800000.dwc3
BOARD_KERNEL_CMDLINE += loop.max_part=7
BOARD_KERNEL_CMDLINE += zram.backend=z3fold
BOARD_KERNEL_CMDLINE += androidboot.selinux=permissive

BOARD_KERNEL_BASE        := 0x00000000
BOARD_KERNEL_PAGESIZE    := 4096

### PARTITIONS
# See also /proc/partitions on the devices
BOARD_BOOTIMAGE_PARTITION_SIZE := 67108864
BOARD_CACHEIMAGE_PARTITION_SIZE := 398458880
BOARD_FLASH_BLOCK_SIZE := 131072 # (BOARD_KERNEL_PAGESIZE * 64)
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 67108864

BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE := ext4
PRODUCT_USE_DYNAMIC_PARTITION_SIZE := true
TARGET_USERIMAGES_USE_EXT4 := true

### TREBLE DISABLED
PRODUCT_FULL_TREBLE_OVERRIDE := false

### TWRP

## CPU TEMP PATH
TW_CUSTOM_CPU_TEMP_PATH := /sys/class/thermal/thermal_zone4/temp

## /DATA/MEDIA SUPPORT
RECOVERY_SDCARD_ON_DATA := true

## ENCRYPTION
PLATFORM_SECURITY_PATCH := 2025-12-31
PLATFORM_VERSION := 16.1.0
TARGET_CRYPTFS_HW_PATH := vendor/qcom/opensource/commonsys/cryptfs_hw
TARGET_HW_DISK_ENCRYPTION := true
TW_INCLUDE_CRYPTO := true

# QSEECOMD DEPENDENCIES
TARGET_RECOVERY_DEVICE_MODULES      += libxml2.so
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(PRODUCT_OUT)/system/lib64/libxml2.so

TARGET_RECOVERY_DEVICE_MODULES      += libicuuc.so
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(PRODUCT_OUT)/system/lib64/libicuuc.so

# KEYMASTER DEPENDENCIES
TARGET_RECOVERY_DEVICE_MODULES      += libion.so
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(PRODUCT_OUT)/system/lib64/libion.so

TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.weaver@1.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(PRODUCT_OUT)/system/lib64/android.hardware.weaver@1.0.so

## F2FS SUPPORT
CM_PLATFORM_SDK_VERSION := 3
TARGET_USERIMAGES_USE_F2FS := true

## INIT
TW_EXCLUDE_DEFAULT_USB_INIT := true
TARGET_RECOVERY_DEVICE_MODULES := init.recovery.usb.rc

## LOGCAT SUPPORT
TWRP_INCLUDE_LOGCAT := true
TARGET_USES_LOGD := true

## QCOM
TARGET_RECOVERY_QCOM_RTC_FIX := true
TW_NEW_ION_HEAP := true

## SCREEN BRIGHTNESS
TW_BRIGHTNESS_PATH := /sys/class/leds/wled/brightness
TW_MAX_BRIGHTNESS := 4095
TW_DEFAULT_BRIGHTNESS := 1600

## SDCARD PARTITIONING DISABLED
BOARD_HAS_NO_REAL_SDCARD := true

## TOOLBOX
TW_USE_TOOLBOX := true

## TWRP FEATURES
TW_EXCLUDE_SUPERSU := true
TW_EXCLUDE_TWRPAPP := true
TW_EXTRA_LANGUAGES := true
TW_THEME := portrait_hdpi

### VENDOR SECURITY PATCH LEVEL
VENDOR_SECURITY_PATCH := 2019-09-01
