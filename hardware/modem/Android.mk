ifeq ($(BOARD_MODEM_CUSTOMIZATIONS),true)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
CUSTOMIZATION_MODEM_SYMLINKS := $(TARGET_OUT)/etc/customization/modem
$(CUSTOMIZATION_MODEM_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "Create customization modem links: $@"
	@mkdir -p $@
	$(hide) ln -sf amss_fsg_lilac_tar.mbn $@/default
	$(hide) ln -sf amss_fs_empty.mbn $@/reset_modemst1
	$(hide) ln -sf amss_fs_empty.mbn $@/reset_modemst2

ALL_DEFAULT_INSTALLED_MODULES += \
	$(CUSTOMIZATION_MODEM_SYMLINKS)

include $(CLEAR_VARS)
LOCAL_MODULE := init.sony.modem.sh
LOCAL_SRC_FILES := init.sony.modem.sh
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_STEM := init.sony.modem.sh
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR_EXECUTABLES)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE := dump_miscta
LOCAL_SRC_FILES := dump_miscta.c
LOCAL_SHARED_LIBRARIES := libmiscta
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR_EXECUTABLES)
include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)
LOCAL_MODULE := ta_cust_version
LOCAL_SRC_FILES := ta_cust_version.c
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR_EXECUTABLES)
include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)
# Create mount points for oem configs
OEM_MOUNT_POINTS := $(TARGET_ROOT_OUT)/oem

ALL_DEFAULT_INSTALLED_MODULES += $(OEM_MOUNT_POINTS)

$(OEM_MOUNT_POINTS):
	@echo "Creating $(OEM_MOUNT_POINTS) subdirs"
	@mkdir -p $(TARGET_ROOT_OUT)/oem/modem-config
	@mkdir -p $(TARGET_ROOT_OUT)/oem/system-properties

endif
