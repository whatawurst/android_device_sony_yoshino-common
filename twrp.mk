#
# Copyright (C) 2017 The LineAgeOS Project
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

RECOVERY_VARIANT := twrp
TARGET_RECOVERY_FSTAB := $(PLATFORM_PATH)/recovery/twrp.fstab

BOARD_NEEDS_VENDORIMAGE_SYMLINK := false
TARGET_COPY_OUT_VENDOR := vendor

### INIT
# Use rootdir/init.recovery.usb.rc
TW_EXCLUDE_DEFAULT_USB_INIT := true
TARGET_RECOVERY_DEVICE_MODULES := init.recovery.usb.rc

### KERNEL
BOARD_KERNEL_CMDLINE += androidboot.selinux=permissive

### QCOM
TW_TARGET_USES_QCOM_BSP := true
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
# ext4 file based crypto
TW_INCLUDE_CRYPTO_FBE := true
TARGET_CRYPTFS_HW_PATH := vendor/qcom/opensource/cryptfs_hw

# Dependencies of libsecureui.so
TARGET_RECOVERY_DEVICE_MODULES      += libEGL
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libEGL.so
TARGET_RECOVERY_DEVICE_MODULES      += libGLESv2
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libGLESv2.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.graphics.allocator@2.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hardware.graphics.allocator@2.0.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.graphics.common@1.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hardware.graphics.common@1.0.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.graphics.mapper@2.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hardware.graphics.mapper@2.0.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.configstore@1.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hardware.configstore@1.0.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.configstore-utils
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hardware.configstore-utils.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.graphics.bufferqueue@1.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hardware.graphics.bufferqueue@1.0.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.media@1.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hardware.media@1.0.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hidl.base@1.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hidl.base@1.0.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hidl.token@1.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hidl.token@1.0.so
TARGET_RECOVERY_DEVICE_MODULES      += android.hidl.token@1.0-utils
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hidl.token@1.0-utils.so
TARGET_RECOVERY_DEVICE_MODULES      += libbinder
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libbinder.so
TARGET_RECOVERY_DEVICE_MODULES      += libgui
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libgui.so
TARGET_RECOVERY_DEVICE_MODULES      += libnativebridge
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libnativebridge.so
TARGET_RECOVERY_DEVICE_MODULES      += libnativehelper
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libnativehelper.so
TARGET_RECOVERY_DEVICE_MODULES      += libnativeloader
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libnativeloader.so
TARGET_RECOVERY_DEVICE_MODULES      += libnativewindow
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libnativewindow.so
TARGET_RECOVERY_DEVICE_MODULES      += libprotobuf-cpp-lite
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libprotobuf-cpp-lite.so
TARGET_RECOVERY_DEVICE_MODULES      += libsync
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libsync.so
TARGET_RECOVERY_DEVICE_MODULES      += libui
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libui.so

# Required for keymaster
TARGET_RECOVERY_DEVICE_MODULES      += hwservicemanager
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/bin/hwservicemanager
TARGET_RECOVERY_DEVICE_MODULES      += libhidl-gen-utils
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libhidl-gen-utils.so
TARGET_RECOVERY_DEVICE_MODULES      += libtinyxml2
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libtinyxml2.so
TARGET_RECOVERY_DEVICE_MODULES      += libvintf
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libvintf.so

# Required by android.hardware.keymaster@3.0-service
TARGET_RECOVERY_DEVICE_MODULES      += libhardware_legacy
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/libhardware_legacy.so

# Required by android.hardware.gatekeeper-1.0-service
TARGET_RECOVERY_DEVICE_MODULES      += android.hardware.gatekeeper@1.0
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/lib64/android.hardware.gatekeeper@1.0.so

# Add strace
TARGET_RECOVERY_DEVICE_MODULES      += strace
TW_RECOVERY_ADDITIONAL_RELINK_FILES += $(OUT)/system/xbin/strace

### F2FS SUPPORT
CM_PLATFORM_SDK_VERSION := $(LINEAGE_PLATFORM_SDK_VERSION)
TARGET_USERIMAGES_USE_F2FS := true

### TWRP FEATURES
TW_NO_EXFAT_FUSE := true
TW_EXCLUDE_SUPERSU := true

TW_THEME := portrait_hdpi
