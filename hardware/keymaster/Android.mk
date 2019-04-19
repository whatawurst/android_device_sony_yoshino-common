LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
KEYMASTER_SYMLINK := $(TARGET_OUT_VENDOR)/lib/android.hardware.keymaster@3.0-impl-qti.so
$(KEYMASTER_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Create keymaster link: $@"
	$(hide) ln -sf hw/android.hardware.keymaster@3.0-impl-qti.so $@

include $(CLEAR_VARS)
KEYMASTER64_SYMLINK := $(TARGET_OUT_VENDOR)/lib64/android.hardware.keymaster@3.0-impl-qti.so
$(KEYMASTER64_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Create keymaster64 link: $@"
	$(hide) ln -sf hw/android.hardware.keymaster@3.0-impl-qti.so $@

ALL_DEFAULT_INSTALLED_MODULES += \
	$(KEYMASTER_SYMLINK) \
	$(KEYMASTER64_SYMLINK)
