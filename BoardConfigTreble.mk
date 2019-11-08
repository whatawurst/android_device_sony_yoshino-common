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
WITH_LINEAGE_CHARGER := false
# system/core/healthd/Android.mk
BOARD_CHARGER_DISABLE_INIT_BLANK := true
BOARD_CHARGER_ENABLE_SUSPEND := true

# device/sony/treble/core/healthd
# FIXME Linking issues with libminui, it is not a dep to libhealthd
#BOARD_HAL_STATIC_LIBRARIES += libhealthd.$(TARGET_DEVICE)

### AUDIO
BOARD_USES_ALSA_AUDIO := true
AUDIO_FEATURE_ENABLED_MULTI_VOICE_SESSIONS := true
USE_XML_AUDIO_POLICY_CONF := 1

### DRM
TARGET_ENABLE_MEDIADRM_64 := true

### NFC
TARGET_USES_NQ_NFC := true

### SEPOLICY
include device/qcom/sepolicy-legacy-um/sepolicy.mk
BOARD_SEPOLICY_DIRS += device/sony/common-treble/sepolicy/vendor

### SYSTEM PROPS
# This is a reset, add more in platform and device if needed
TARGET_SYSTEM_PROP := $(COMMON_PATH)/system.prop
