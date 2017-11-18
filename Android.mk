LOCAL_PATH := $(call my-dir)

ifneq ($(filter yoshino,$(PRODUCT_PLATFORM)),)
include $(call all-subdir-makefiles,$(LOCAL_PATH))
endif
