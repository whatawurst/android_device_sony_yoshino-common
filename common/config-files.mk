### SENSORS
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/sensors/sensors_settings:system/etc/sensors/sensors_settings

### AUDIO
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/audio/audio_effects.conf:system/etc/audio_effects.conf

PRODUCT_COPY_FILES += \
    frameworks/av/services/audiopolicy/config/audio_policy_configuration_generic.xml:system/etc/audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/primary_audio_policy_configuration.xml:system/etc/primary_audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/r_submix_audio_policy_configuration.xml:system/etc/r_submix_audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/audio_policy_volumes.xml:system/etc/audio_policy_volumes.xml \
    frameworks/av/services/audiopolicy/config/default_volume_tables.xml:system/etc/default_volume_tables.xml \

### QMI
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/qmi/dsi_config.xml:system/etc/data/dsi_config.xml \
    $(COMMON_PATH)/config/qmi/netmgr_config.xml:system/etc/data/netmgr_config.xml \
    $(COMMON_PATH)/config/qmi/qmi_config.xml:system/etc/data/qmi_config.xml

### ISRC
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/irsc/sec_config:system/etc/sec_config

### STAGEFRIGHT
PRODUCT_COPY_FILES += \
    frameworks/av/media/libstagefright/data/media_codecs_google_audio.xml:system/etc/media_codecs_google_audio.xml \
    frameworks/av/media/libstagefright/data/media_codecs_google_telephony.xml:system/etc/media_codecs_google_telephony.xml \
    frameworks/av/media/libstagefright/data/media_codecs_google_video.xml:system/etc/media_codecs_google_video.xml

### GPS
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/gps/gps.conf:system/etc/gps.conf
