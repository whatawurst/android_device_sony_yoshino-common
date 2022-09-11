# init rc
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/init/init.yoshino.ims.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.yoshino.ims.rc \
    $(PLATFORM_PATH)/config/init/init.yoshino.pwr.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.yoshino.pwr.rc \
    $(PLATFORM_PATH)/config/init/init.yoshino.qcom.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.yoshino.qcom.rc \
    $(PLATFORM_PATH)/config/init/init.yoshino.srv.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.yoshino.srv.rc \
    $(PLATFORM_PATH)/config/init/init.yoshino.usb.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.yoshino.usb.rc

# init.qcom.early_boot.sh (modified)
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/init/init.qcom.early_boot.sh:$(TARGET_COPY_OUT_VENDOR)/bin/init.qcom.early_boot.sh \
    $(PLATFORM_PATH)/config/init/init.qcom.radio.sh:$(TARGET_COPY_OUT_VENDOR)/bin/init.qcom.radio.sh

# ueventd
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/init/ueventd.rc:$(TARGET_COPY_OUT_VENDOR)/ueventd.rc
