### SENSORS
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.debug.sensors.hal=0 \
    debug.vendor.sns.daemon=0 \
    debug.vendor.sns.hal=0 \
    debug.vendor.sns.libsensor1=0

### POWER
PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.extension_library=/vendor/lib64/librqbalance.so

### AUDIO
PRODUCT_PROPERTY_OVERRIDES += \
    persist.audio.fluence.voicecall=true \
    persist.audio.fluence.voicecomm=true \
    persist.audio.fluence.voicerec=false \
    persist.audio.fluence.speaker=true \
    media.aac_51_output_enabled=true \
    audio.deep_buffer.media=1

### DRM SERVICE
PRODUCT_PROPERTY_OVERRIDES += \
    drm.service.enabled=true

### WFD
# Property to enable user to access Google WFD settings.
PRODUCT_PROPERTY_OVERRIDES += \
    persist.debug.wfd.enable=1

# Property to choose between virtual/external wfd display
PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.wfd.virtual=0

### BLUETOOTH ADDRESS
PRODUCT_PROPERTY_OVERRIDES += \
    ro.bt.bdaddr_path=/data/misc/bluetooth/bluetooth_bdaddr

### RILD
PRODUCT_PROPERTY_OVERRIDES += \
    rild.libpath=/odm/lib64/libril-qc-qmi-1.so \
    ril.subscription.types=NV,RUIM

### NETMGR
PRODUCT_PROPERTY_OVERRIDES += \
    ro.use_data_netmgrd=true \
    persist.data.netmgrd.qos.enable=true \
    ro.data.large_tcp_window_size=true

### NFC
PRODUCT_PROPERTY_OVERRIDES += \
    ro.nfc.port=I2C
