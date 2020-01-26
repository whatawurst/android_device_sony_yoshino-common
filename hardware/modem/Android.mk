ifeq ($(BOARD_MODEM_CUSTOMIZATIONS),true)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
CUSTOMIZATION_MODEM_SYMLINKS := $(TARGET_OUT)/etc/customization/modem
$(CUSTOMIZATION_MODEM_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "Create customization modem links: $@"
	@mkdir -p $@
	$(hide) ln -sf amss_fs_empty.mbn $@/reset_modemst1
	$(hide) ln -sf amss_fs_empty.mbn $@/reset_modemst2
ifeq ($(TARGET_DEVICE),lilac)
	$(hide) ln -sf amss_fsg_lilac_tar.mbn $@/default
endif
ifeq ($(TARGET_DEVICE),poplar)
	$(hide) ln -sf amss_fsg_poplar_tar.mbn $@/default
endif
ifeq ($(TARGET_DEVICE),poplar_canada)
	$(hide) ln -sf amss_fsg_poplar_tar.mbn $@/default
endif
ifeq ($(TARGET_DEVICE),poplar_dsds)
	$(hide) ln -sf amss_fsg_poplar_dsds_tar.mbn $@/default
endif
ifeq ($(TARGET_DEVICE),maple)
	$(hide) ln -sf amss_fsg_maple_tar.mbn $@/default
endif
ifeq ($(TARGET_DEVICE),maple_dsds)
	$(hide) ln -sf amss_fsg_maple_dsds_tar.mbn $@/default
endif

ALL_DEFAULT_INSTALLED_MODULES += \
	$(CUSTOMIZATION_MODEM_SYMLINKS)

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

endif
