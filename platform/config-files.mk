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
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/audio_policy/audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/r_submix_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/r_submix_audio_policy_configuration.xml

### BLUETOOTH
PRODUCT_COPY_FILES += \
    frameworks/av/services/audiopolicy/config/a2dp_in_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/a2dp_in_audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/bluetooth_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/bluetooth_audio_policy_configuration.xml

### CLEARPAD_IDC
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/idc/clearpad.idc:$(TARGET_COPY_OUT_VENDOR)/usr/idc/clearpad.idc

### COMPONENT OVERRIDES
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/component-overrides/component-overrides.xml:$(TARGET_COPY_OUT_VENDOR)/etc/sysconfig/component-overrides.xml

### GPS
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/gps/gps.conf:$(TARGET_COPY_OUT_VENDOR)/etc/gps.conf

### ISRC
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/irsc/sec_config:$(TARGET_COPY_OUT_VENDOR)/etc/sec_config

### KEYLAYOUT
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/keylayout/gpio-keys.kl:$(TARGET_COPY_OUT_SYSTEM)/usr/keylayout/gpio-keys.kl

### MEDIA
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/media/media_codecs.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs.xml \
    $(PLATFORM_PATH)/config/media/media_codecs_performance.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_performance.xml \
    $(PLATFORM_PATH)/config/media/media_profiles_vendor.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_profiles_vendor.xml

### MSM_IRQ_BALANCER
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/irqbalance/msm_irqbalance.conf:$(TARGET_COPY_OUT_VENDOR)/etc/msm_irqbalance.conf

### POWER
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/power/powerhint.json:$(TARGET_COPY_OUT_VENDOR)/etc/powerhint.json

### QTI_LOW_POWER_WHITELIST
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/power/qti_whitelist.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/sysconfig/qti_whitelist.xml

### STAGEFRIGHT
PRODUCT_COPY_FILES += \
    frameworks/av/media/libstagefright/data/media_codecs_google_audio.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_google_audio.xml \
    frameworks/av/media/libstagefright/data/media_codecs_google_telephony.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_google_telephony.xml \
    frameworks/av/media/libstagefright/data/media_codecs_google_video.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_google_video.xml

### TELEPHONY
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/telephony/apns-conf.xml:$(TARGET_COPY_OUT_PRODUCT)/etc/apns-conf.xml

### WIFI
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/wifi/WCNSS_qcom_cfg.ini:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/WCNSS_qcom_cfg.ini \
    $(PLATFORM_PATH)/config/wifi/wpa_supplicant_overlay.conf:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/wpa_supplicant_overlay.conf

### WIFIDISPLAY
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/wfd/wfdconfig.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/wfdconfig.xml
