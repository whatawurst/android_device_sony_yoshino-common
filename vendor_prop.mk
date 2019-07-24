#
# Copyright (C) 2017 The LineAgeOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

### AUDIO
# ALAC software decoders enable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.use.sw.alac.decoder=true

# APE software decoders enable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.use.sw.ape.decoder=true

# Audio buffer size for low latency in frames
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio_hal.period_size=192

# DS2, Hardbypass feature for Dolby disable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.dolby.ds2.enabled=false \
    vendor.audio.dolby.ds2.hardbypass=false

# Flac sw decoder 24 bit decode capability
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.flac.sw.decoder.24bit=true

# Fluence
# fluencetype can be "fluence" or "fluencepro" or "none"
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.audio.fluence.speaker=true \
    persist.vendor.audio.fluence.voicecall=true \
    persist.vendor.audio.fluence.voicerec=false \
    ro.vendor.audio.sdk.fluencetype=none

# Hw aac encoder enable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.hw.aac.encoder=true

# Noisy audio intent broadcast delay
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.noisy.broadcast.delay=600

# Offload audio track disable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.offload.track.enable=false

# Offload buffer size in kbytes
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.offload.buffer.size.kb=32

# Offload gapless mode enable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.offload.gapless.enabled=true

# Offload multi channel aac enable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.offload.multiaac.enable=true

# Offload multiple session enable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.offload.multiple.enabled=true

# Offload passthrough disable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.offload.passthrough=false

# Offload pause timeout duration to 3 secs to inline with other outputs
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.offload.pstimeout.secs=3

# Parser input buffer size (256kb) in byte stream mode
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.parser.ip.buffer.size=262144

# Pbe effects enable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.safx.pbe.enabled=true

# PCM VoIP voice path enable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.voice.path.for.pcm.voip=true

# RAS Feature disable
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.audio.ras.enabled=false

# Sony effect props
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.sony.effect.custom.caplus_hs=0x298 \
    vendor.audio.sony.effect.custom.caplus_sp=0x2B8 \
    vendor.audio.sony.effect.custom.sp_bundle=0x122

# Speaker protection enable
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.audio.speaker.prot.enable=true

# Surround sound recording disable
PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.audio.sdk.ssr=false

# Tunnel encoding disable
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.audio.tunnel.encode=false

# VPP omx component for decoder disable
# (enabled on stock, causes issues with e.g. 4k and 60fps playback)
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.media.vpp.enable=false

### BLUETOOTH
PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.bt.bdaddr_path="/data/vendor/etc/bluetooth_bdaddr" \
    ro.vendor.btstack.hfp.ver=1.7 \
    persist.vendor.bt.enable.splita2dp=false \
    persist.vendor.service.bdroid.ssrlvl=3 \
    vendor.qcom.bluetooth.soc=cherokee

### CAMERA
PRODUCT_PROPERTY_OVERRIDES += \
    camera.disable_zsl_mode=1

### DISPLAY
# CABL
PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.display.cabl=2

# Display Properties as per treble compliance
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.gralloc.disable_ubwc=0 \
    vendor.gralloc.enable_fb_ubwc=1

# HDR
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.display.disable_hdr_lut_gen=1

# OpenGLES version
PRODUCT_PROPERTY_OVERRIDES += \
    ro.opengles.version=196610

# SurfaceFlinger
PRODUCT_PROPERTY_OVERRIDES += \
    debug.sf.early_phase_offset_ns=1500000 \
    debug.sf.early_app_phase_offset_ns=1500000 \
    debug.sf.early_gl_phase_offset_ns=3000000 \
    debug.sf.early_gl_app_phase_offset_ns=15000000 \
    debug.sf.enable_gl_backpressure=1 \
    ro.surface_flinger.force_hwc_copy_for_virtual_displays=true \
    ro.surface_flinger.protected_contents=true \
    ro.surface_flinger.vsync_event_phase_offset_ns=2000000 \
    ro.surface_flinger.vsync_sf_event_phase_offset_ns=6000000

# Touch
PRODUCT_PROPERTY_OVERRIDES += \
    vendor.backlight_on=1

# Virtual keys
PRODUCT_PROPERTY_OVERRIDES += \
    qemu.hw.mainkeys=0

### MEDIA
PRODUCT_PROPERTY_OVERRIDES += \
    debug.stagefright.omx_default_rank.sw-audio=1 \
    debug.stagefright.omx_default_rank=0

### MISC
# Factory reset protection path
PRODUCT_PROPERTY_OVERRIDES += \
    ro.frp.pst=/dev/block/bootdevice/by-name/frp

# Misc path
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.mmi.misc_dev_path=/dev/block/sda54

# OEM unlock reporting
PRODUCT_DEFAULT_PROPERTY_OVERRIDES += \
    ro.oem_unlock_supported=1

### PERFORMANCE
# Power balancing
PRODUCT_PROPERTY_OVERRIDES += \
    ro.vendor.extension_library=libqti-perfd-client.so

### RADIO
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.data.iwlan.enable=true \
    persist.vendor.radio.custom_ecc=1 \
    persist.vendor.radio.mt_sms_ack=19 \
    persist.vendor.radio.oem_socket=true \
    persist.vendor.radio.rat_on=combine \
    persist.vendor.radio.report_codec=1 \
    persist.vendor.radio.sib16_support=1 \
    persist.vendor.radio.wait_for_pbm

# IMS
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.ims.vcel_rtcp_report=5 \
    persist.vendor.radio.ims_call_transfer=true

# IMS availability overrides
#PRODUCT_PROPERTY_OVERRIDES += \
#    persist.dbg.volte_avail_ovr=1 \
#    persist.dbg.vt_avail_ovr=1 \
#    persist.dbg.wfc_avail_ovr=1

# Power down sim in airplane mode disable
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.radio.apm_sim_not_pwdn=1

# Power save functionality for modem
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.radio.add_power_save=1

### THERMAL
PRODUCT_PROPERTY_OVERRIDES += \
    ro.hardware.thermal=somc

### USB
PRODUCT_PROPERTY_OVERRIDES += \
    persist.vendor.usb.config.extra=none \
    vendor.usb.rndis.func.name=gsi
