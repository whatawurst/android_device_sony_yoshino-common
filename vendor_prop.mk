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
PRODUCT_PROPERTY_OVERRIDES += \
    audio.deep_buffer.media=true \
    media.aac_51_output_enabled=true \
    ro.config.media_vol_steps=30 \
    ro.config.vc_call_vol_steps=8

### DRM
PRODUCT_PROPERTY_OVERRIDES += \
    drm.service.enabled=true

### GPS
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.overlay.izat.optin=rro

### IMS
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.service.qti.ims.enabled=1

### NFC
PRODUCT_PROPERTY_OVERRIDES += \
    ro.hardware.nfc_nci=nqx.default \
    ro.nfc.on.default=false \
    ro.nfc.port=I2C \
    ro.nfc.se.sim.enable=true \
    ro.nfc.se.smx.enable=false

### RADIO
# CNE
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.cne.feature=1

# DPM module
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.dpm.feature=1

# Netmgr
PRODUCT_PROPERTY_OVERRIDES += \
    persist.data.netmgrd.qos.enable=true \
    persist.vendor.data.mode=concurrent \
    persist.vendor.radio.aosp_usr_pref_sel=true \
    ro.vendor.use_data_netmgrd=true

# Rild
PRODUCT_PROPERTY_OVERRIDES += \
    ril.subscription.types=NV,RUIM \
    rild.libpath=/system/vendor/lib64/libril-qc-qmi-1.so

# Rild - NITZ
PRODUCT_PROPERTY_OVERRIDES += \
    persist.rild.nitz_long_ons_0="" \
    persist.rild.nitz_long_ons_1="" \
    persist.rild.nitz_long_ons_2="" \
    persist.rild.nitz_long_ons_3="" \
    persist.rild.nitz_plmn="" \
    persist.rild.nitz_short_ons_0="" \
    persist.rild.nitz_short_ons_1="" \
    persist.rild.nitz_short_ons_2="" \
    persist.rild.nitz_short_ons_3=""

# Rmnet data
PRODUCT_PROPERTY_OVERRIDES += \
    persist.data.df.agg.dl_pkt=10 \
    persist.data.df.agg.dl_size=4096 \
    persist.data.df.dl_mode=5 \
    persist.data.df.dev_name=rmnet_usb0 \
    persist.data.df.iwlan_mux=9 \
    persist.data.df.mux_count=8 \
    persist.data.df.ul_mode=5 \
    persist.data.wda.enable=true \
    persist.rmnet.data.enable=true

# TCP Buffer sizes
PRODUCT_PROPERTY_OVERRIDES += \
    net.tcp.2g_init_rwnd=10 \
    net.tcp.buffersize.default=4096,87380,524288,4096,16384,110208 \
    net.tcp.buffersize.edge=4093,26280,35040,4096,16384,35040 \
    net.tcp.buffersize.evdo=4094,87380,524288,4096,16384,262144 \
    net.tcp.buffersize.gprs=4092,8760,11680,4096,8760,11680 \
    net.tcp.buffersize.hsdpa=4094,87380,1220608,4096,16384,1220608 \
    net.tcp.buffersize.hspa=4094,87380,1220608,4096,16384,1220608 \
    net.tcp.buffersize.hspap=4094,87380,1220608,4096,16384,1220608 \
    net.tcp.buffersize.hsupa=4094,87380,1220608,4096,16384,1220608 \
    net.tcp.buffersize.lte=2097152,4194304,8388608,262144,524288,1048576 \
    net.tcp.buffersize.umts=4094,87380,110208,4096,16384,110208

# Telephony
PRODUCT_PROPERTY_OVERRIDES += \
    ro.telephony.call_ring.multiple=false \
    telephony.lteOnCdmaDevice=1

### SENSORS
PRODUCT_PROPERTY_OVERRIDES += \
    debug.vendor.sns.daemon=0 \
    debug.vendor.sns.libsensor1=0 \
    persist.camera.gyro.disable=0 \
    persist.vendor.debug.sensors.hal=0

### WFD
# Property for WfdService.apk to fix WFD for some apps when 
# hdcp is not provisioned (e.g. unlocked bootloader)
PRODUCT_PROPERTY_OVERRIDES += \
    persist.debug.wfd.appmonitoring=1

# Property to enable user to access Google WFD settings.
PRODUCT_PROPERTY_OVERRIDES += \
    persist.debug.wfd.enable=1

# Property to choose between virtual/external wfd display
PRODUCT_PROPERTY_OVERRIDES += \
    persist.sys.wfd.virtual=0

### WIFI
PRODUCT_PROPERTY_OVERRIDES += \
    net.tcp.buffersize.wifi=524288,2097152,4194304,262144,524288,1048576 \
    wifi.interface=wlan0
