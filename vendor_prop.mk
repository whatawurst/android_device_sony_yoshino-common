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

### SENSORS
PRODUCT_PROPERTY_OVERRIDES += \
    persist.camera.gyro.disable=0 \
    persist.vendor.debug.sensors.hal=0 \
    debug.vendor.sns.daemon=0 \
    debug.vendor.sns.hal=0 \
    debug.vendor.sns.libsensor1=0

### AUDIO
PRODUCT_PROPERTY_OVERRIDES += \
    persist.audio.fluence.voicecall=true \
    persist.audio.fluence.voicecomm=true \
    persist.audio.fluence.voicerec=false \
    persist.audio.fluence.speaker=true \
    media.aac_51_output_enabled=true \
    audio.deep_buffer.media=1 \
    ro.config.vc_call_vol_steps=7 \
    ro.config.media_vol_steps=25 \
    ro.config.vc_call_vol_steps=11

### DRM SERVICE
PRODUCT_PROPERTY_OVERRIDES += \
    drm.service.enabled=true

### WFD
# Property to enable user to access Google WFD settings.
PRODUCT_PROPERTY_OVERRIDES += \
    persist.debug.wfd.enable=1

# Property for WfdService.apk to fix WFD for some apps when 
# hdcp is not provisioned (e.g. unlocked bootloader)
PRODUCT_PROPERTY_OVERRIDES += \
    persist.debug.wfd.appmonitoring=1

# Property to choose between virtual/external wfd display
PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.wfd.virtual=0

### RILD
PRODUCT_PROPERTY_OVERRIDES += \
    rild.libpath=/vendor/lib64/libril-qc-qmi-1.so \
    ril.subscription.types=NV,RUIM \
    telephony.lteOnCdmaDevice=1

# Enable netmgr
PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.use_data_netmgrd=true \
    persist.radio.aosp_usr_pref_sel=true \
    persist.data.netmgrd.qos.enable=true \
    persist.vendor.data.mode=concurrent

PRODUCT_PROPERTY_OVERRIDES += \
    persist.rild.nitz_plmn="" \
    persist.rild.nitz_long_ons_0="" \
    persist.rild.nitz_long_ons_1="" \
    persist.rild.nitz_long_ons_2="" \
    persist.rild.nitz_long_ons_3="" \
    persist.rild.nitz_short_ons_0="" \
    persist.rild.nitz_short_ons_1="" \
    persist.rild.nitz_short_ons_2="" \
    persist.rild.nitz_short_ons_3=""

# Enable CNE
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.cne.feature=1

# Enable dpm module
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.dpm.feature=1

# Enable rmnet data
PRODUCT_PROPERTY_OVERRIDES += \
    persist.rmnet.data.enable=true \
    persist.data.wda.enable=true \
    persist.data.df.dl_mode=5 \
    persist.data.df.ul_mode=5 \
    persist.data.df.agg.dl_pkt=10 \
    persist.data.df.agg.dl_size=4096 \
    persist.data.df.mux_count=8 \
    persist.data.df.iwlan_mux=9 \
    persist.data.df.dev_name=rmnet_usb0

# Buffer sizes
PRODUCT_PROPERTY_OVERRIDES += \
    net.tcp.buffersize.default=4096,87380,524288,4096,16384,110208 \
    net.tcp.buffersize.lte=2097152,4194304,8388608,262144,524288,1048576 \
    net.tcp.buffersize.umts=4094,87380,110208,4096,16384,110208 \
    net.tcp.buffersize.hspa=4094,87380,1220608,4096,16384,1220608 \
    net.tcp.buffersize.hsupa=4094,87380,1220608,4096,16384,1220608 \
    net.tcp.buffersize.hsdpa=4094,87380,1220608,4096,16384,1220608 \
    net.tcp.buffersize.hspap=4094,87380,1220608,4096,16384,1220608 \
    net.tcp.buffersize.edge=4093,26280,35040,4096,16384,35040 \
    net.tcp.buffersize.gprs=4092,8760,11680,4096,8760,11680 \
    net.tcp.buffersize.evdo=4094,87380,524288,4096,16384,262144 \
    net.tcp.2g_init_rwnd=10

### NFC
PRODUCT_PROPERTY_OVERRIDES += \
    ro.hardware.nfc_nci=nqx.default \
    ro.nfc.port=I2C \
    ro.nfc.se.sim.enable=true \
    ro.nfc.se.smx.enable=false \
    ro.nfc.on.default=false \
    ro.vendor.nfc.ko=pn553

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

# IMS
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.service.qti.ims.enabled=1

# GPS
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.overlay.izat.optin=rro

# Telephony
PRODUCT_PROPERTY_OVERRIDES += \
    ro.telephony.call_ring.multiple=false
