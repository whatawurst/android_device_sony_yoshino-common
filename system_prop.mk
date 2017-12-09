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
    rild.libpath=/vendor/lib64/libril-qc-qmi-1.so \
    ril.subscription.types=NV,RUIM

# Default to LTE/GSM/WCDMA.
PRODUCT_PROPERTY_OVERRIDES += \
    ro.telephony.default_network=9

### NETMGR
PRODUCT_PROPERTY_OVERRIDES += \
    ro.use_data_netmgrd=true \
    persist.data.netmgrd.qos.enable=true \
    ro.data.large_tcp_window_size=true

### NFC
PRODUCT_PROPERTY_OVERRIDES += \
    ro.nfc.port=I2C

### WIFI
PRODUCT_PROPERTY_OVERRIDES += \
    wifi.interface=wlan0 \
    net.tcp.buffersize.wifi=524288,2097152,4194304,262144,524288,1048576

### DEX2OAT
# Limit dex2oat threads to improve thermals
PRODUCT_PROPERTY_OVERRIDES += \
    dalvik.vm.dex2oat-threads=2 \
    dalvik.vm.image-dex2oat-threads=4

### DATA
# Platform specific default properties
PRODUCT_PROPERTY_OVERRIDES += \
    persist.data.qmi.adb_logmask=0

### HW COMPOSER AND UI
# Hardware composer
PRODUCT_PROPERTY_OVERRIDES += \
    persist.hwc.mdpcomp.enable=true

# Hardware User Interface parameters
PRODUCT_PROPERTY_OVERRIDES += \
    ro.hwui.texture_cache_size=48 \
    ro.hwui.layer_cache_size=32 \
    ro.hwui.r_buffer_cache_size=4 \
    ro.hwui.path_cache_size=24 \
    ro.hwui.gradient_cache_size=1 \
    ro.hwui.drop_shadow_cache_size=5 \
    ro.hwui.texture_cache_flushrate=0.5 \
    ro.hwui.text_small_cache_width=1024 \
    ro.hwui.text_small_cache_height=1024 \
    ro.hwui.text_large_cache_width=2048 \
    ro.hwui.text_large_cache_height=1024
