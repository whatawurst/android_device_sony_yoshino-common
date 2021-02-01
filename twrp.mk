#
# Copyright (C) 2017 The LineageOS Project
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

###########################################################
### TWRP RECOVERY
###########################################################

# Do not go full treble for recovery
PRODUCT_FULL_TREBLE_OVERRIDE := false

RECOVERY_VARIANT := twrp

ifneq ($(filter maple maple_dsds, $(TARGET_DEVICE)),)
    TARGET_RECOVERY_FSTAB := $(PLATFORM_PATH)/recovery/twrp_maple.fstab
    TARGET_COPY_OUT_VENDOR := system/vendor
else
    TARGET_RECOVERY_FSTAB := $(PLATFORM_PATH)/recovery/twrp.fstab
    TARGET_COPY_OUT_VENDOR := vendor
endif

BOARD_NEEDS_VENDORIMAGE_SYMLINK := false

### INIT
# Use rootdir/init.recovery.usb.rc
TW_EXCLUDE_DEFAULT_USB_INIT := true
TARGET_RECOVERY_DEVICE_MODULES := init.recovery.usb.rc

### KERNEL
BOARD_KERNEL_CMDLINE += androidboot.selinux=permissive

# Install kernel modules to root directory
NEED_KERNEL_MODULE_ROOT := true

### QCOM
TARGET_RECOVERY_QCOM_RTC_FIX := true
#TW_TARGET_USES_QCOM_BSP := true
TW_NEW_ION_HEAP := true

### SCREEN BRIGHTNESS
TW_BRIGHTNESS_PATH := /sys/class/leds/wled/brightness
TW_MAX_BRIGHTNESS := 4095
TW_DEFAULT_BRIGHTNESS := 1600

TW_CUSTOM_CPU_TEMP_PATH := /sys/class/thermal/thermal_zone4/temp

# Add logcat support
TWRP_INCLUDE_LOGCAT := true
TARGET_USES_LOGD := true

# Use toolbox instead of busybox
TW_USE_TOOLBOX := true

# Does not allow to partition the sdcard
BOARD_HAS_NO_REAL_SDCARD := true
# Media on data partition
RECOVERY_SDCARD_ON_DATA := true

### ENCRYPTED FILESYSTEMS
TW_INCLUDE_CRYPTO := true
ifneq ($(filter lilac poplar poplar_canada poplar_dsds poplar_kddi, $(TARGET_DEVICE)),)
    # ext4 file based crypto
    TW_INCLUDE_CRYPTO_FBE := true
endif

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

# Add strace
TARGET_RECOVERY_DEVICE_MODULES      += strace
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(PRODUCT_OUT)/system/bin/strace

### F2FS SUPPORT
CM_PLATFORM_SDK_VERSION := 3
TARGET_USERIMAGES_USE_F2FS := true

### TWRP FEATURES
TW_EXCLUDE_SUPERSU := true
TW_EXTRA_LANGUAGES := true

TW_THEME := portrait_hdpi

# Use a future date for the security patchlevel.  As TWRP doesn't do key
# upgrades it should just be fine using it for decryption.
PLATFORM_SECURITY_PATCH := 2025-12-31
