### SENSORS
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.debug.sensors.hal=0 \
    debug.vendor.sns.daemon=0 \
    debug.vendor.sns.hal=0 \
    debug.vendor.sns.libsensor1=0

### POWER
PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.extension_library=/vendor/lib64/librqbalance.so
