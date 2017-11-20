### SENSORS
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/sensors/sensors_settings:system/etc/sensors/sensors_settings

### QMI
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/qmi/dsi_config.xml:system/etc/data/dsi_config.xml \
    $(COMMON_PATH)/config/qmi/netmgr_config.xml:system/etc/data/netmgr_config.xml \
    $(COMMON_PATH)/config/qmi/qmi_config.xml:system/etc/data/qmi_config.xml

### ISRC
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/config/irsc/sec_config:system/etc/sec_config
