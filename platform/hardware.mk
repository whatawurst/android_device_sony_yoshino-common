### GRAPHICS
ifeq ($(WITH_VENDOR_IMAGE),true)
PRODUCT_PACKAGES += \
    copybit.msm8998 \
    gralloc.msm8998 \
    hwcomposer.msm8998 \
    memtrack.msm8998

### AUDIO
PRODUCT_PACKAGES += \
    audio.primary.msm8998 \
    libvolumelistener

### CAMERA
PRODUCT_PACKAGES += \
    camera.msm8998
endif

### CAMERA APP
PRODUCT_PACKAGES += \
    Snap
