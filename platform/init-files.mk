# fstab
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/init/idd.fstab:$(TARGET_COPY_OUT_VENDOR)/etc/idd.fstab \
    $(PLATFORM_PATH)/config/init/qns.fstab:$(TARGET_COPY_OUT_VENDOR)/etc/qns.fstab

# init rc
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/init/init.common.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.common.rc \
    $(PLATFORM_PATH)/config/init/init.common.qcom.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.common.qcom.rc \
    $(PLATFORM_PATH)/config/init/init.common.srv.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.common.srv.rc \
    $(PLATFORM_PATH)/config/init/init.common.ims.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.common.ims.rc \
    $(PLATFORM_PATH)/config/init/init.yoshino.usb.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.yoshino.usb.rc \
    $(PLATFORM_PATH)/config/init/init.yoshino.pwr.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.yoshino.pwr.rc

# init.qcom.early_boot.sh (modified)
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/init/init.qcom.early_boot.sh:$(TARGET_COPY_OUT_VENDOR)/bin/init.qcom.early_boot.sh

# ueventd
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/init/ueventd.yoshino.rc:$(TARGET_COPY_OUT_VENDOR)/ueventd.rc
