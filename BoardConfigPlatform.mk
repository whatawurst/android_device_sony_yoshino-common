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
TARGET_KERNEL_CLANG_COMPILE := true
TARGET_KERNEL_CLANG_VERSION := r383902b
TARGET_KERNEL_SOURCE  := kernel/sony/msm8998
TARGET_KERNEL_VERSION := 4.4

# Taken from unpacked stock boot.img / README_Xperia in Kernel source
BOARD_KERNEL_CMDLINE += ehci-hcd.park=3
BOARD_KERNEL_CMDLINE += lpm_levels.sleep_disabled=1
BOARD_KERNEL_CMDLINE += sched_enable_hmp=0
BOARD_KERNEL_CMDLINE += sched_enable_power_aware=1
BOARD_KERNEL_CMDLINE += service_locator.enable=1
BOARD_KERNEL_CMDLINE += swiotlb=2048
BOARD_KERNEL_CMDLINE += androidboot.configfs=true
BOARD_KERNEL_CMDLINE += androidboot.usbcontroller=a800000.dwc3
BOARD_KERNEL_CMDLINE += loop.max_part=7

# See README_Xperia in Kernel Source
BOARD_KERNEL_BASE        := 0x00000000
BOARD_KERNEL_PAGESIZE    := 4096

BOARD_KERNEL_IMAGE_NAME := Image.gz-dtb

### PARTITIONS
# See also /proc/partitions on the devices
BOARD_BOOTIMAGE_PARTITION_SIZE := 67108864
BOARD_CACHEIMAGE_PARTITION_SIZE := 398458880
BOARD_FLASH_BLOCK_SIZE := 131072 # (BOARD_KERNEL_PAGESIZE * 64)
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 67108864

BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE := ext4
BOARD_ROOT_EXTRA_FOLDERS := ocm
TARGET_USERIMAGES_USE_EXT4 := true

### DEXPREOPT
# Enable dexpreopt for everything to speed boot time
ifeq ($(HOST_OS),linux)
  ifneq ($(TARGET_BUILD_VARIANT),eng)
      WITH_DEXPREOPT_BOOT_IMG_AND_SYSTEM_SERVER_ONLY := false
      WITH_DEXPREOPT := true
  endif
endif

### BLUETOOTH
BOARD_BLUETOOTH_BDROID_BUILDCFG_INCLUDE_DIR := $(PLATFORM_PATH)/bluetooth
BOARD_HAVE_BLUETOOTH_QCOM := true

### BUILD_COPY_HEADERS ALLOWED
BUILD_BROKEN_USES_BUILD_COPY_HEADERS := true

### CAMERA
BOARD_QTI_CAMERA_32BIT_ONLY := true
MALLOC_SVELTE_FOR_LIBC32 := true
TARGET_USES_QTI_CAMERA_DEVICE := true

### DISPLAY
TARGET_USES_HWC2 := true
TARGET_USES_GRALLOC1 := true

### DRM
TARGET_ENABLE_MEDIADRM_64 := true

### ENCRYPTION
TARGET_HW_DISK_ENCRYPTION := true

### FILESYSTEM
TARGET_FS_CONFIG_GEN := \
    $(PLATFORM_PATH)/fs/config.aid \
    $(PLATFORM_PATH)/fs/config.fs

### GRAPHICS
TARGET_USES_ION := true

### HIDL
DEVICE_MANIFEST_FILE := $(PLATFORM_PATH)/manifest.xml
DEVICE_MATRIX_FILE := $(PLATFORM_PATH)/compatibility_matrix.xml

### INIT
TARGET_INIT_VENDOR_LIB := //$(PLATFORM_PATH):libinit_yoshino
TARGET_RECOVERY_DEVICE_MODULES := libinit_yoshino

### PROPS
TARGET_ODM_PROP += $(PLATFORM_PATH)/odm.prop
# This is a reset, add more in devices if needed
TARGET_SYSTEM_PROP := $(PLATFORM_PATH)/system.prop
TARGET_VENDOR_PROP := $(PLATFORM_PATH)/vendor.prop

### RECOVERY
ifneq ($(filter maple maple_dsds, $(TARGET_DEVICE)),)
TARGET_RECOVERY_FSTAB := $(PLATFORM_PATH)/recovery/fstab_maple.recovery
else
TARGET_RECOVERY_FSTAB := $(PLATFORM_PATH)/recovery/fstab.recovery
endif

### RENDERSCRIPT
OVERRIDE_RS_DRIVER := libRSDriver_adreno.so

### RIL
DISABLE_RILD_OEM_HOOK := true
TARGET_PER_MGR_ENABLED := true
TARGET_PROVIDES_QTI_TELEPHONY_JAR := true
TARGET_USES_OLD_MNC_FORMAT := true

### SEPOLICY
include device/qcom/sepolicy-legacy-um/SEPolicy.mk
BOARD_VENDOR_SEPOLICY_DIRS += device/sony/yoshino-common/sepolicy/vendor
BOARD_PLAT_PRIVATE_SEPOLICY_DIR += device/sony/yoshino-common/sepolicy/private

### VENDOR FILE OVERRIDE
BUILD_BROKEN_DUP_RULES := true

### VENDOR SECURITY PATCH LEVEL
VENDOR_SECURITY_PATCH := 2019-09-01

### WIFI
BOARD_HAS_QCOM_WLAN := true
BOARD_HOSTAPD_DRIVER := NL80211
BOARD_HOSTAPD_PRIVATE_LIB := lib_driver_cmd_qcwcn
BOARD_WLAN_DEVICE := qcwcn
BOARD_WPA_SUPPLICANT_DRIVER := NL80211
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_qcwcn
HOSTAPD_VERSION := VER_0_8_X
WIFI_DRIVER_FW_PATH_AP  := "ap"
WIFI_DRIVER_FW_PATH_P2P := "p2p"
WIFI_DRIVER_FW_PATH_STA := "sta"
WIFI_HIDL_FEATURE_DUAL_INTERFACE := true
WIFI_HIDL_UNIFIED_SUPPLICANT_SERVICE_RC_ENTRY := true
WPA_SUPPLICANT_VERSION := VER_0_8_X
