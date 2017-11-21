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

include device/sony/common-treble/BoardConfigTreble.mk

PLATFORM_PATH := device/sony/yoshino

### BOARD
BOARD_VENDOR := sony

TARGET_BOARD_PLATFORM := msm8998
TARGET_BOARD_PLATFORM_GPU := qcom-adreno540

BOARD_USES_QCOM_HARDWARE := true

### PROCESSOR
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-a
TARGET_CPU_ABI := arm64-v8a
TARGET_CPU_ABI2 :=
#TARGET_CPU_VARIANT := kryo
TARGET_CPU_VARIANT := generic

TARGET_2ND_ARCH := arm
TARGET_2ND_ARCH_VARIANT := armv7-a-neon
TARGET_2ND_CPU_ABI := armeabi-v7a
TARGET_2ND_CPU_ABI2 := armeabi
#TARGET_2ND_CPU_VARIANT := kryo
TARGET_2ND_CPU_VARIANT := cortex-a9

TARGET_USES_64_BIT_BINDER := true

ENABLE_CPUSETS := true

### KERNEL
TARGET_KERNEL_VERSION := 4.4
TARGET_KERNEL_SOURCE  := kernel/sony/msm8998

BOARD_KERNEL_CMDLINE += androidboot.bootdevice=1da4000.ufshc
BOARD_KERNEL_CMDLINE += display_status=on
BOARD_KERNEL_CMDLINE += lpm_levels.sleep_disabled=1

# See README_Xperia in Kernel source
BOARD_KERNEL_CMDLINE += user_debug=31
BOARD_KERNEL_CMDLINE += msm_rtb.filter=0x37
BOARD_KERNEL_CMDLINE += ehci-hcd.park=3
BOARD_KERNEL_CMDLINE += sched_enable_hmp=1
BOARD_KERNEL_CMDLINE += sched_enable_power_aware=1
BOARD_KERNEL_CMDLINE += service_locator.enable=1
BOARD_KERNEL_CMDLINE += zram.backend=z3fold
BOARD_KERNEL_CMDLINE += buildvariant=userdebug

# See README_Xperia in Kernel Source
BOARD_KERNEL_BASE        := 0x00000000
BOARD_KERNEL_PAGESIZE    := 4096
BOARD_RAMDISK_OFFSET     := 0x01000000

TARGET_KERNEL_ARCH := arm64
TARGET_KERNEL_HEADER_ARCH := arm64
TARGET_KERNEL_CROSS_COMPILE_PREFIX := aarch64-linux-android-
BOARD_KERNEL_IMAGE_NAME := Image.gz-dtb

### PARTITIONS
BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE := ext4

# Use stock vendor.img
BOARD_NEEDS_VENDORIMAGE_SYMLINK := true
TARGET_COPY_OUT_VENDOR := system

# Build ext4 tools - system/vold
TARGET_USERIMAGES_USE_EXT4 := true

### GRAPHICS
TARGET_HAS_HDR_DISPLAY := true
TARGET_USES_HWC2 := true
TARGET_USES_GRALLOC1 := true

### WIFI
BOARD_HAS_QCOM_WLAN := true
BOARD_HOSTAPD_DRIVER := NL80211
BOARD_HOSTAPD_PRIVATE_LIB := lib_driver_cmd_qcwcn
BOARD_WLAN_DEVICE := qcwcn
BOARD_WPA_SUPPLICANT_DRIVER := NL80211
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_qcwcn
HOSTAPD_VERSION := VER_0_8_X
WIFI_DRIVER_MODULE_PATH := "/system/lib/modules/wlan.ko"
WIFI_DRIVER_MODULE_NAME := "wlan"
WIFI_DRIVER_FW_PATH_AP  := "ap"
WIFI_DRIVER_FW_PATH_P2P := "p2p"
WIFI_DRIVER_FW_PATH_STA := "sta"
WPA_SUPPLICANT_VERSION := VER_0_8_X

### BLUETOOTH
BOARD_BLUETOOTH_BDROID_BUILDCFG_INCLUDE_DIR := $(PLATFORM_PATH)/bluetooth
BOARD_HAVE_BLUETOOTH := true
BOARD_HAVE_BLUETOOTH_QCOM := true
QCOM_BT_USE_BTNV := true
BOARD_HAS_QCA_BT_SIBS := true
WCNSS_FILTER_USES_SIBS := true

# vendor/qcom/opensource/cryptfs_hw
TARGET_HW_DISK_ENCRYPTION := true
TARGET_CRYPTFS_HW_PATH := vendor/qcom/opensource/cryptfs_hw

### SEPOLICY
BOARD_SEPOLICY_DIRS += \
    device/sony/yoshino/sepolicy

### RECOVERY
TARGET_RECOVERY_FSTAB := $(PLATFORM_PATH)/ramdisk/fstab.yoshino

ifeq ($(WITH_TWRP),true)
-include $(PLATFORM_PATH)/twrp.mk
endif
