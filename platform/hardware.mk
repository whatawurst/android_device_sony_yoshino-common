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

### AUDIO
PRODUCT_PACKAGES += \
    audio.a2dp.default \
    audio.r_submix.default \
    audio.usb.default \
    libvolumelistener

# BLUETOOTH
PRODUCT_PACKAGES += \
    audio.bluetooth.default

### CHARGER
PRODUCT_PACKAGES += \
    charger_res_images

### COVER
PRODUCT_PACKAGES += \
    FlipFlap

### DISPLAY
PRODUCT_PACKAGES += \
    vendor.display.config@1.3

### DPM
PRODUCT_PACKAGES += \
    libshim_dpmframework

### GRAPHICS
PRODUCT_PACKAGES += \
    copybit.msm8998 \
    gralloc.msm8998 \
    hwcomposer.msm8998 \
    memtrack.msm8998 \
    libdisplayconfig \
    liboverlay

### IPACM
PRODUCT_PACKAGES += \
    ipacm \
    IPACM_cfg.xml \
    libipanat \
    liboffloadhal

### NFC
PRODUCT_PACKAGES += \
    com.android.nfc_extras \
    NfcNci \
    SecureElement \
    Tag

### OPENCUSTOMIZATIONSELECTOR
PRODUCT_PACKAGES += \
    CustomizationSelector

### REMOVE UNWANTED PACKAGES
PRODUCT_PACKAGES += \
    RemovePackages

# PERFD (DUMMY)
PRODUCT_PACKAGES += \
    libqti-perfd-client

### SENSORS
PRODUCT_PACKAGES += \
    libsensorndkbridge

### SERVICES
# AUDIO
PRODUCT_PACKAGES += \
    android.hardware.audio@4.0-impl \
    android.hardware.audio@2.0-service \
    android.hardware.audio.effect@4.0-impl \
    android.hardware.audio.effect@2.0-service \
    android.hardware.soundtrigger@2.2-impl \
    android.hardware.soundtrigger@2.0-service

# BLUETOOTH
PRODUCT_PACKAGES += \
    android.hardware.bluetooth.audio@2.0-impl

# CAMERA
PRODUCT_PACKAGES += \
    android.hardware.camera.common@1.0-impl \
    android.hardware.camera.provider@2.4-impl \
    camera.device@3.2-impl

# CONFIGSTORE
PRODUCT_PACKAGES += \
    android.hardware.configstore@1.1-service

# DISPLAY
PRODUCT_PACKAGES += \
    vendor.lineage.livedisplay@2.0-service-sdm

# DRM
PRODUCT_PACKAGES += \
    android.hardware.drm@1.0-impl \
    android.hardware.drm@1.0-service \
    android.hardware.drm@1.2-service.clearkey

# FINGERPRINT
PRODUCT_PACKAGES += \
    android.hardware.biometrics.fingerprint@2.1

# GNSS
PRODUCT_PACKAGES += \
    android.hardware.gnss@2.0

# GRAPHICS
PRODUCT_PACKAGES += \
    android.hardware.graphics.allocator@2.0-impl \
    android.hardware.graphics.allocator@2.0-service \
    android.hardware.graphics.composer@2.1-impl \
    android.hardware.graphics.composer@2.1-service \
    android.hardware.graphics.mapper@2.0-impl-2.1

# HEALTH
PRODUCT_PACKAGES += \
    android.hardware.health@2.0-impl \
    android.hardware.health@2.0-service

# LIGHT
ifneq ($(TARGET_USE_YOSHINO_LIGHT_SERVICE),true)
    PRODUCT_PACKAGES += \
        android.hardware.light@2.0-impl \
        android.hardware.light@2.0-service
else
    TARGET_PROVIDES_LIBLIGHT := true
    PRODUCT_PACKAGES += android.hardware.light@2.0-service.yoshino
endif

# MEMTRACK
PRODUCT_PACKAGES += \
    android.hardware.memtrack@1.0-impl \
    android.hardware.memtrack@1.0-service

# POWER
PRODUCT_PACKAGES += \
    android.hardware.power@1.3-service.yoshino \
    android.hardware.power.stats@1.0-service.mock

# RADIO
PRODUCT_PACKAGES += \
    android.hardware.broadcastradio@1.0-impl \
    android.hardware.radio@1.1 \
    android.hardware.radio.config@1.0 \
    android.hardware.radio.deprecated@1.0

# RENDERSCRIPT
PRODUCT_PACKAGES += \
    android.hardware.renderscript@1.0-impl

# SENSORS
PRODUCT_PACKAGES += \
    android.hardware.sensors@1.0-impl \
    android.hardware.sensors@1.0-service

# THERMAL
PRODUCT_PACKAGES += \
    android.hardware.thermal@1.0-impl \
    android.hardware.thermal@1.1-impl \
    android.hardware.thermal@1.0-service

# USB
PRODUCT_PACKAGES += \
    android.hardware.usb@1.0-service.basic

# USB TRUST HAL
PRODUCT_PACKAGES += \
    vendor.lineage.trust@1.0-service

# VIBRATOR
PRODUCT_PACKAGES += \
    android.hardware.vibrator@1.0-impl \
    android.hardware.vibrator@1.0-service

# VR
PRODUCT_PACKAGES += \
    android.hardware.vr@1.0-impl \
    android.hardware.vr@1.0-service

# WEAVER
PRODUCT_PACKAGES += \
    android.hardware.weaver@1.0

# WIFI
PRODUCT_PACKAGES += \
    android.hardware.wifi@1.0-impl \
    android.hardware.wifi@1.1-impl \
    android.hardware.wifi@1.0-service \
    android.hardware.wifi.offload@1.0

### THERMAL
PRODUCT_PACKAGES += \
    thermal.msm8998

### VR
PRODUCT_PACKAGES += \
    vr.msm8998

### WIFI
PRODUCT_PACKAGES += \
    hostapd \
    libwpa_client \
    p2p_supplicant.conf \
    wificond \
    wpa_supplicant \
    wpa_supplicant.conf

### XPERIAPARTS
PRODUCT_PACKAGES += \
    XperiaParts
