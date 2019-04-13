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

### build/make/target/product/treble_common.mk

# Enable dynamic partition size
PRODUCT_USE_DYNAMIC_PARTITION_SIZE := true

# Split build properties
BOARD_PROPERTY_OVERRIDES_SPLIT_ENABLED := true

# Split selinux policy
PRODUCT_FULL_TREBLE_OVERRIDE := true

# The Messaging app:
#   Needed for android.telecom.cts.ExtendedInCallServiceTest#testOnCannedTextResponsesLoaded
PRODUCT_PACKAGES += \
    messaging

# NFC:
#   Provide default libnfc-nci.conf file for devices that does not have one in
#   vendor/etc
PRODUCT_COPY_FILES += \
    device/generic/common/nfc/libnfc-nci.conf:system/etc/libnfc-nci.conf

# Support for the O-MR1 devices
PRODUCT_COPY_FILES += \
    build/make/target/product/vndk/init.gsi.rc:system/etc/init/init.gsi.rc \
    build/make/target/product/vndk/init.vndk-27.rc:system/etc/init/gsi/init.vndk-27.rc

# Name space configuration file for non-enforcing VNDK
PRODUCT_PACKAGES += \
    ld.config.vndk_lite.txt

# Support addtional O-MR1 vendor interface
PRODUCT_EXTRA_VNDK_VERSIONS := 27

### build/make/target/product/treble_common_64.mk

# For now this will allow 64-bit apps, but still compile all apps with JNI
# for 32-bit only.

# Copy different zygote settings for vendor.img to select by setting property
# ro.zygote=zygote64_32 or ro.zygote=zygote32_64:
#   1. 64-bit primary, 32-bit secondary OR
#   2. 32-bit primary, 64-bit secondary
#   3. 64-bit only is currently forbidden (b/64280459#comment6)
PRODUCT_COPY_FILES += \
    system/core/rootdir/init.zygote64_32.rc:root/init.zygote64_32.rc \
    system/core/rootdir/init.zygote32_64.rc:root/init.zygote32_64.rc

TARGET_SUPPORTS_32_BIT_APPS := true
TARGET_SUPPORTS_64_BIT_APPS := true

### DEPENDENCIES
PRODUCT_PACKAGES += \
    libaudioroute \
    libaudioutils \
    libavservices_minijail \
    libminijail

PRODUCT_PACKAGES += \
    android.hardware.biometrics.fingerprint@2.1 \
    android.hardware.usb@1.0 \
    android.hardware.usb@1.1 \

### SERVICES
# RENDERSCRIPT
PRODUCT_PACKAGES += \
    android.hardware.renderscript@1.0-impl

# GRAPHICS
PRODUCT_PACKAGES += \
    android.hardware.graphics.allocator@2.0-impl \
    android.hardware.graphics.allocator@2.0-service \
    android.hardware.graphics.composer@2.1-impl \
    android.hardware.graphics.composer@2.1-service \
    android.hardware.graphics.mapper@2.0-impl

# MEMTRACK
PRODUCT_PACKAGES += \
    android.hardware.memtrack@1.0-impl \
    android.hardware.memtrack@1.0-service

# CONFIGSTORE
PRODUCT_PACKAGES += \
    android.hardware.configstore@1.0-service

# AUDIO
PRODUCT_PACKAGES += \
    android.hardware.audio@4.0-impl \
    android.hardware.audio@2.0-service \
    android.hardware.audio.effect@4.0-impl \
    android.hardware.audio.effect@2.0-service \
    android.hardware.soundtrigger@2.0-impl \
    android.hardware.soundtrigger@2.0-service

# CAMERA
PRODUCT_PACKAGES += \
    android.hardware.camera.common@1.0-impl \
    android.hardware.camera.provider@2.4-impl \
    camera.device@3.2-impl

# WIFI
PRODUCT_PACKAGES += \
    android.hardware.wifi@1.0-impl \
    android.hardware.wifi@1.1-impl \
    android.hardware.wifi@1.0-service

# BLUETOOTH
PRODUCT_PACKAGES += \
    android.hardware.bluetooth@1.0-impl

# NFC
PRODUCT_PACKAGES += \
    android.hardware.nfc@1.0-impl \
    android.hardware.nfc@1.0-service

# GNSS
PRODUCT_PACKAGES += \
    android.hardware.gnss@1.0-impl-qti \
    android.hardware.gnss@1.0-service-qti

# LIGHT
PRODUCT_PACKAGES += \
    android.hardware.light@2.0-impl \
    android.hardware.light@2.0-service

# SENSORS
PRODUCT_PACKAGES += \
    android.hardware.sensors@1.0-impl \
    android.hardware.sensors@1.0-service

# DRM
PRODUCT_PACKAGES += \
    android.hardware.drm@1.0-impl \
    android.hardware.drm@1.0-service

# USB
PRODUCT_PACKAGES += \
    android.hardware.usb@1.0-service

# THERMAL
PRODUCT_PACKAGES += \
    android.hardware.thermal@1.0-impl \
    android.hardware.thermal@1.1-impl \
    android.hardware.thermal@1.0-service

# POWER
PRODUCT_PACKAGES += \
    android.hardware.power@1.1-service-qti

# HEALTH
PRODUCT_PACKAGES += \
    android.hardware.health@1.0-impl \
    android.hardware.health@1.0-service

# VIBRATOR
PRODUCT_PACKAGES += \
    android.hardware.vibrator@1.0-impl \
    android.hardware.vibrator@1.0-service

# VR
PRODUCT_PACKAGES += \
    android.hardware.vr@1.0-impl \
    android.hardware.vr@1.0-service

# RADIO
PRODUCT_PACKAGES += \
    android.hardware.broadcastradio@1.0-impl

# SECURE ELEMENT
PRODUCT_PACKAGES += \
    android.hardware.secure_element@1.0-service \
    SecureElement
