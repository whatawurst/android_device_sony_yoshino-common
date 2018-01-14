# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

COMMON_PATH := device/sony/common-treble

### BOARD
BOARD_USES_QCOM_HARDWARE := true
BOARD_VENDOR := sony

### KERNEL
BOARD_KERNEL_CMDLINE += console=ttyMSM0,115200,n8 androidboot.console=ttyMSM0
BOARD_KERNEL_CMDLINE += msm_rtb.filter=0x3F ehci-hcd.park=3
BOARD_KERNEL_CMDLINE += coherent_pool=8M
BOARD_KERNEL_CMDLINE += sched_enable_power_aware=1 user_debug=31

### GRAPHICS
USE_OPENGL_RENDERER := true
BOARD_USES_ADRENO := true
TARGET_USES_ION := true

MAX_EGL_CACHE_KEY_SIZE := 12*1024
MAX_EGL_CACHE_SIZE := 2048*1024
TARGET_FORCE_HWC_FOR_VIRTUAL_DISPLAYS := true

### DISPLAY
# qcom/display-caf/msm8998/common.mk
TARGET_USES_COLOR_METADATA := true
TARGET_HAS_HDR_DISPLAY := true
TARGET_USES_HWC2 := true
TARGET_USES_GRALLOC1 := true

# RENDERSCRIPT
OVERRIDE_RS_DRIVER := libRSDriver_adreno.so

### PARTITIONS
BOARD_CACHEIMAGE_FILE_SYSTEM_TYPE := ext4
# Build ext4 tools - system/vold
TARGET_USERIMAGES_USE_EXT4 := true

### FILESYSTEM
TARGET_FS_CONFIG_GEN := \
    $(COMMON_PATH)/fs/config.aid \
    $(COMMON_PATH)/fs/config.fs

### CHARGER
WITH_LINEAGE_CHARGER := true
# system/core/healthd/Android.mk
BOARD_CHARGER_DISABLE_INIT_BLANK := true
BOARD_CHARGER_ENABLE_SUSPEND := true

# device/sony/treble/core/healthd
BOARD_HAL_STATIC_LIBRARIES += libhealthd.$(TARGET_DEVICE)

### AUDIO
BOARD_USES_ALSA_AUDIO := true
AUDIO_FEATURE_ENABLED_MULTI_VOICE_SESSIONS := true
USE_XML_AUDIO_POLICY_CONF := 1

### VENDOR MANIFEST AND MATRIX
ifeq ($(WITH_VENDOR_IMAGE),true)
DEVICE_MANIFEST_FILE := $(COMMON_PATH)/vendor/manifest.xml
DEVICE_MATRIX_FILE   := $(COMMON_PATH)/vendor/compatibility_matrix.xml
endif

### SEPOLICY
include device/qcom/sepolicy/sepolicy.mk
BOARD_SEPOLICY_DIRS += \
    device/sony/common-treble/sepolicy
