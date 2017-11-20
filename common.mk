COMMON_PATH := device/sony/common-treble

### INIT
PRODUCT_COPY_FILES += \
    $(COMMON_PATH)/ramdisk/init.common.rc:root/init.common.rc \
    $(COMMON_PATH)/ramdisk/init.common.srv.rc:root/init.common.srv.rc

include $(COMMON_PATH)/common/*.mk

$(call inherit-product, device/sony/common-treble/treble.mk)
