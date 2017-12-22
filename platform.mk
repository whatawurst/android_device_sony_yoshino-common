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

$(call inherit-product, device/sony/common-treble/common.mk)

PLATFORM_PATH := device/sony/yoshino

### PLATFORM INIT
PRODUCT_PACKAGES += \
    fstab.yoshino \
    init.yoshino.usb \
    init.yoshino.pwr \
    init.qcom.early_boot.sh

### RECOVERY
# Add Timezone database
PRODUCT_COPY_FILES += \
    bionic/libc/zoneinfo/tzdata:recovery/root/system/usr/share/zoneinfo/tzdata

ifeq ($(WITH_TWRP),true)
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/recovery/vendor/manifest.xml:recovery/root/vendor/manifest.xml
endif

DEVICE_PACKAGE_OVERLAYS += \
    $(PLATFORM_PATH)/overlay

### VERITY
# We can't make system a verity partition for now
# The issue is that else we can't install the su-addon or opengapps
#PRODUCT_SYSTEM_VERITY_PARTITION := /dev/block/bootdevice/by-name/system
#PRODUCT_VENDOR_VERITY_PARTITION := /dev/block/bootdevice/by-name/vendor
#$(call inherit-product, build/target/product/verity.mk)

include $(PLATFORM_PATH)/platform/*.mk
include $(PLATFORM_PATH)/system_prop.mk
