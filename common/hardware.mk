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
    libaudio-resampler \
    tinymix

### NFC
PRODUCT_PACKAGES += \
    com.nxp.nfc.nq \
    com.nxp.nfc.nq.xml \
    libnqnfc-nci \
    libnqp61-jcop-kit \
    com.android.nfc_extras \
    NQNfcNci \
    nqnfcee_access.xml \
    nqnfcse_access.xml \
    Tag \
    vendor.nxp.hardware.nfc@1.1-service

### WIFI
PRODUCT_PACKAGES += \
    p2p_supplicant.conf \
    hostapd \
    libwpa_client \
    wificond \
    wpa_supplicant \
    wpa_supplicant.conf

### CHARGER
PRODUCT_PACKAGES += \
    charger_res_images

### CAMERA
PRODUCT_PACKAGES += \
    Snap
