### SYSTEM LIBS
PRODUCT_PACKAGES += \
    libjson \
    libion \
    libxml2

### POWER
PRODUCT_PACKAGES += \
    librqbalance

### AUDIO
# For audio.primary
PRODUCT_PACKAGES += \
    libtinyalsa \
    tinymix

# Audio effects
PRODUCT_PACKAGES += \
    libqcomvisualizer \
    libqcomvoiceprocessing \
    libqcomvoiceprocessingdescriptors \
    libqcompostprocbundle \
    libvolumelistener

### OMX
PRODUCT_PACKAGES += \
    libc2dcolorconvert \
    libstagefrighthw \
    libOmxCore \
    libmm-omxcore \
    libOmxVdec \
    libOmxVdecHevc \
    libOmxVenc

### GPS
PRODUCT_PACKAGES += \
    libloc_api_v02 \
    libloc_core \
    libloc_ds_api \
    libloc_eng \
    libloc_pla \
    libloc_stub \
    libgps.utils

### BLUETOOTH
PRODUCT_PACKAGES += \
    libbt-vendor

### RIL
PRODUCT_PACKAGES += \
    libprotobuf-cpp-full
