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

#################### SYSTEM ######################

# SENSORS
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/sensors/sensors_settings:system/etc/sensors/sensors_settings

# NFC
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/nfc/nfcee_access.xml:system/etc/nfcee_access.xml

# GPS
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/gps/gps.conf:system/etc/gps.conf

#################### VENDOR ######################

# ISRC
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/irsc/sec_config:$(TARGET_COPY_OUT_VENDOR)/etc/sec_config

# STAGEFRIGHT
PRODUCT_COPY_FILES += \
    frameworks/av/media/libstagefright/data/media_codecs_google_audio.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_google_audio.xml \
    frameworks/av/media/libstagefright/data/media_codecs_google_telephony.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_google_telephony.xml \
    frameworks/av/media/libstagefright/data/media_codecs_google_video.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_google_video.xml
