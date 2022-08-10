LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
CNE_SYMLINK := $(TARGET_OUT_VENDOR)/app/CneApp/lib/arm64/libvndfwk_detect_jni.qti.so
$(CNE_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Create ims media jni link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /vendor/lib64/libvndfwk_detect_jni.qti.so $@

ALL_DEFAULT_INSTALLED_MODULES += \
	$(CNE_SYMLINK)
