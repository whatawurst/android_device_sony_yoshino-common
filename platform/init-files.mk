# Modified init.qcom.early_boot.sh
PRODUCT_COPY_FILES += \
    $(PLATFORM_PATH)/config/init/init.common.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.common.rc \
    $(PLATFORM_PATH)/config/init/init.common.qcom.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.common.qcom.rc \
    $(PLATFORM_PATH)/config/init/init.common.srv.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.common.srv.rc \
    $(PLATFORM_PATH)/config/init/init.common.ims.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/hw/init.common.ims.rc \
    $(PLATFORM_PATH)/config/init/init.qcom.early_boot.sh:$(TARGET_COPY_OUT_VENDOR)/bin/init.qcom.early_boot.sh
