### GRAPHICS
PRODUCT_PACKAGES += \
    copybit.msm8998 \
    gralloc.msm8998 \
    hwcomposer.msm8998 \
    memtrack.msm8998 \
    libdisplayconfig \
    liboverlay

### AUDIO
PRODUCT_PACKAGES += \
    audio.primary.msm8998 \
    libvolumelistener \
    bthost_if

# IPACM
PRODUCT_PACKAGES += \
    ipacm \
    IPACM_cfg.xml \
    libipanat \
    liboffloadhal

# GPS
PRODUCT_PACKAGES += \
    android.hardware.gnss@1.0-impl-qti \
    libgnss \
    libgnsspps
