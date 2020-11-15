#
# Copyright (c) 2020, Shashank Verma (shank03) <shashank.verma2002@gmail.com>
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

### TODO: Whether to add jar dependency from vendor by adding java import module in .bp file
###       or directly compile with app like below
LOCAL_SRC_FILES += $(call all-java-files-under, src-misc-ta)

LOCAL_PACKAGE_NAME := CustomizationSelector
LOCAL_CERTIFICATE := platform
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_PRIVILEGED_MODULE := true

LOCAL_USE_AAPT2 := true

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.core_core \
    androidx.preference_preference \
    androidx.appcompat_appcompat

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res

### Include device specific config xml file
ifeq ($(TARGET_DEVICE),lilac)
    LOCAL_RESOURCE_DIR += \
        $(LOCAL_PATH)/res-lilac
endif

ifeq ($(TARGET_DEVICE),poplar)
    LOCAL_RESOURCE_DIR += \
        $(LOCAL_PATH)/res-poplar
endif

ifeq ($(TARGET_DEVICE),poplar_canada)
    LOCAL_RESOURCE_DIR += \
        $(LOCAL_PATH)/res-poplar-canada
endif

ifeq ($(TARGET_DEVICE),poplar_dsds)
    LOCAL_RESOURCE_DIR += \
        $(LOCAL_PATH)/res-poplar-dsds
endif

ifeq ($(TARGET_DEVICE),maple)
    LOCAL_RESOURCE_DIR += \
        $(LOCAL_PATH)/res-poplar
endif

ifeq ($(TARGET_DEVICE),maple_dsds)
    LOCAL_RESOURCE_DIR += \
        $(LOCAL_PATH)/res-poplar-dsds
endif

include $(BUILD_PACKAGE)
