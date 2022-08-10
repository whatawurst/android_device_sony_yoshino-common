LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
IMS_CAMERA_SYMLINK := $(TARGET_OUT_SYSTEM_EXT)/priv-app/ims/lib/arm64/libimscamera_jni.so
$(IMS_CAMERA_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Create ims camera jni link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /system/system_ext/lib64/libimscamera_jni.so $@

include $(CLEAR_VARS)
IMS_MEDIA_SYMLINK := $(TARGET_OUT_SYSTEM_EXT)/priv-app/ims/lib/arm64/libimsmedia_jni.so
$(IMS_MEDIA_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Create ims media jni link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /system/system_ext/lib64/libimsmedia_jni.so $@

ALL_DEFAULT_INSTALLED_MODULES += \
	$(IMS_CAMERA_SYMLINK) \
	$(IMS_MEDIA_SYMLINK)
