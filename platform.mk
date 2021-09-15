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

PLATFORM_PATH := device/sony/yoshino-common

PRODUCT_SOONG_NAMESPACES += \
    $(PLATFORM_PATH) \
    hardware/google/pixel

QCOM_SOONG_NAMESPACE := $(PLATFORM_PATH)/hardware/qcom-caf

# For android_filesystem_config.h permissions
PRODUCT_PACKAGES += \
    fs_config_files \
    fs_config_dirs

# Enable dynamic partition size
PRODUCT_USE_DYNAMIC_PARTITION_SIZE := true

DEVICE_PACKAGE_OVERLAYS += \
    $(PLATFORM_PATH)/overlay

PRODUCT_ENFORCE_RRO_TARGETS += *
PRODUCT_ENFORCE_RRO_EXCLUDED_OVERLAYS += \
    $(PLATFORM_PATH)/overlay/packages/apps/FlipFlap

### Additional native libraries
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/public.libraries.txt:$(TARGET_COPY_OUT_VENDOR)/etc/public.libraries.txt

### POWER
TARGET_USE_CUSTOM_POWERHINT ?= false

### RECOVERY
include $(PLATFORM_PATH)/platform/*.mk

### VERITY
ifeq ($(WITH_VERITY),true)
PRODUCT_SYSTEM_VERITY_PARTITION := /dev/block/platform/soc/1da4000.ufshc/by-name/system
PRODUCT_VENDOR_VERITY_PARTITION := /dev/block/platform/soc/1da4000.ufshc/by-name/vendor
$(call inherit-product, build/target/product/verity.mk)
endif
