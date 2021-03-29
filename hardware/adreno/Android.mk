LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LIBEGL_SYMLINK := $(TARGET_OUT_VENDOR)/lib/libGLESv2_adreno.so
$(LIBEGL_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating lib/libGLESv2_adreno.so symlink: $@"
	@mkdir -p $(dir $@)
	$(hide) ln -sf egl/$(notdir $@) $@

LIBEGL64_SYMLINK := $(TARGET_OUT_VENDOR)/lib64/libGLESv2_adreno.so
$(LIBEGL64_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating lib64/libGLESv2_adreno.so symlink: $@"
	@mkdir -p $(dir $@)
	$(hide) ln -sf egl/$(notdir $@) $@

LIBQ3DTOOLS_SYMLINK := $(TARGET_OUT_VENDOR)/lib/libq3dtools_adreno.so
$(LIBQ3DTOOLS_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating lib/libq3dtools_adreno.so symlink: $@"
	@mkdir -p $(dir $@)
	$(hide) ln -sf egl/$(notdir $@) $@

LIBQ3DTOOLS64_SYMLINK := $(TARGET_OUT_VENDOR)/lib64/libq3dtools_adreno.so
$(LIBQ3DTOOLS64_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating lib64/libq3dtools_adreno.so symlink: $@"
	@mkdir -p $(dir $@)
	$(hide) ln -sf egl/$(notdir $@) $@

VULKAN_LIB_PATH := hw/vulkan.msm8998.so

VULKAN_SYMLINK := $(TARGET_OUT_VENDOR)/lib/vulkan.msm8998.so
$(VULKAN_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating lib/vulkan.msm8998.so symlink: $@"
	@mkdir -p $(dir $@)
	$(hide) ln -sf $(VULKAN_LIB_PATH) $@

VULKAN64_SYMLINK := $(TARGET_OUT_VENDOR)/lib64/vulkan.msm8998.so
$(VULKAN64_SYMLINK): $(LOCAL_INSTALLED_MODULE)
	@echo "Creating lib64/vulkan.msm8998.so symlink: $@"
	@mkdir -p $(dir $@)
	$(hide) ln -sf $(VULKAN_LIB_PATH) $@

ALL_DEFAULT_INSTALLED_MODULES += \
	$(LIBEGL_SYMLINK) \
	$(LIBEGL64_SYMLINK) \
	$(LIBQ3DTOOLS_SYMLINK) \
	$(LIBQ3DTOOLS64_SYMLINK) \
	$(VULKAN_SYMLINK) \
	$(VULKAN64_SYMLINK)
