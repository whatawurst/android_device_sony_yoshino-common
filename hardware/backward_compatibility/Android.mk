LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
# Create mount points and symlinks for backward compatibility 
FIRMWARE_MOUNT_POINT := $(TARGET_OUT_VENDOR)/firmware_mnt
BT_FIRMWARE_MOUNT_POINT := $(TARGET_OUT_VENDOR)/bt_firmware
DSP_MOUNT_POINT := $(TARGET_OUT_VENDOR)/dsp
PERSIST_MOUNT_POINT := $(TARGET_ROOT_OUT)/persist

ALL_DEFAULT_INSTALLED_MODULES += $(FIRMWARE_MOUNT_POINT) \
                                 $(BT_FIRMWARE_MOUNT_POINT) \
                                 $(DSP_MOUNT_POINT) \
                                 $(PERSIST_MOUNT_POINT)

$(FIRMWARE_MOUNT_POINT):
	@echo "Creating $(FIRMWARE_MOUNT_POINT)"
	@mkdir -p $(TARGET_OUT_VENDOR)/firmware_mnt
	@ln -sf /vendor/firmware_mnt $(TARGET_ROOT_OUT)/firmware
	
$(BT_FIRMWARE_MOUNT_POINT):
	@echo "Creating $(BT_FIRMWARE_MOUNT_POINT)"
	@mkdir -p $(TARGET_OUT_VENDOR)/bt_firmware
	@ln -sf /vendor/bt_firmware $(TARGET_ROOT_OUT)/bt_firmware
	
$(DSP_MOUNT_POINT):
	@echo "Creating $(DSP_MOUNT_POINT)"
	@mkdir -p $(TARGET_OUT_VENDOR)/dsp
	@ln -sf /vendor/dsp $(TARGET_ROOT_OUT)/dsp
	
$(PERSIST_MOUNT_POINT):
	@echo "Creating $(PERSIST_MOUNT_POINT)"
	@ln -sf /mnt/vendor/persist $(TARGET_ROOT_OUT)/persist
