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
    init.yoshino.usb \
    init.yoshino.pwr \
    init.yoshino.idd.rc \
    init.yoshino.qns.rc \
    idd.fstab \
    qns.fstab

DEVICE_PACKAGE_OVERLAYS += \
    $(PLATFORM_PATH)/overlay

### RECOVERY
ifeq ($(WITH_TWRP),true)
# Add Timezone database
PRODUCT_COPY_FILES += \
    system/timezone/output_data/iana/tzdata:recovery/root/system/usr/share/zoneinfo/tzdata

# Add manifest for hwservicemanager
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/recovery/vendor/manifest.xml:recovery/root/vendor/manifest.xml

else # WITH_TWRP
### VERITY
ifeq ($(WITH_VERITY),true)
PRODUCT_SYSTEM_VERITY_PARTITION := /dev/block/platform/soc/1da4000.ufshc/by-name/system
PRODUCT_VENDOR_VERITY_PARTITION := /dev/block/platform/soc/1da4000.ufshc/by-name/vendor
$(call inherit-product, build/target/product/verity.mk)
endif # WITH_VERITY

include $(PLATFORM_PATH)/platform/*.mk
include $(PLATFORM_PATH)/vendor_prop.mk
endif # WITH_TWRP
