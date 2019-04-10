# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

ifeq ($(BOARD_VNDK_VERSION),)
$(warning ************* BOARD VNDK is not enabled - compiling vndk-sp ***************************)

LOCAL_PATH := $(call my-dir)

ifndef BOARD_VNDK_VERSION
# The libs with "vndk: {enabled: true, support_system_process: true}" will be
# added VNDK_SP_LIBRARIES automatically. And the core variants of the VNDK-SP
# libs will be copied to vndk-sp directory.
# However, some of those libs need FWK-ONLY libs, which must be listed here
# manually.
VNDK_SP_LIBRARIES := \
    libdexfile_support

install_in_hw_dir := \
   android.hidl.memory@1.0-impl

vndk_sp_dir := vndk-sp-$(PLATFORM_VNDK_VERSION)

define define-vndk-sp-lib
include $$(CLEAR_VARS)
LOCAL_MODULE := $1.vndk-sp-gen
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_PREBUILT_MODULE_FILE := $$(call intermediates-dir-for,SHARED_LIBRARIES,$1,,,,)/$1.so
LOCAL_STRIP_MODULE := false
LOCAL_MULTILIB := first
LOCAL_MODULE_TAGS := optional
LOCAL_INSTALLED_MODULE_STEM := $1.so
LOCAL_MODULE_SUFFIX := .so
LOCAL_MODULE_RELATIVE_PATH := $(vndk_sp_dir)$(if $(filter $1,$(install_in_hw_dir)),/hw)
include $$(BUILD_PREBUILT)

ifneq ($$(TARGET_2ND_ARCH),)
ifneq ($$(TARGET_TRANSLATE_2ND_ARCH),true)
include $$(CLEAR_VARS)
LOCAL_MODULE := $1.vndk-sp-gen
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_PREBUILT_MODULE_FILE := $$(call intermediates-dir-for,SHARED_LIBRARIES,$1,,,$$(TARGET_2ND_ARCH_VAR_PREFIX),)/$1.so
LOCAL_STRIP_MODULE := false
LOCAL_MULTILIB := 32
LOCAL_MODULE_TAGS := optional
LOCAL_INSTALLED_MODULE_STEM := $1.so
LOCAL_MODULE_SUFFIX := .so
LOCAL_MODULE_RELATIVE_PATH := $(vndk_sp_dir)$(if $(filter $1,$(install_in_hw_dir)),/hw)
include $$(BUILD_PREBUILT)
endif # TARGET_TRANSLATE_2ND_ARCH is not true
endif # TARGET_2ND_ARCH is not empty
endef

# Add VNDK-SP libs to the list if they are missing
$(foreach lib,$(VNDK_SAMEPROCESS_LIBRARIES),\
    $(if $(filter $(lib),$(VNDK_SP_LIBRARIES)),,\
    $(eval VNDK_SP_LIBRARIES += $(lib))))

# Remove libz from the VNDK-SP list (b/73296261)
VNDK_SP_LIBRARIES := $(filter-out libz,$(VNDK_SP_LIBRARIES))

$(foreach lib,$(VNDK_SP_LIBRARIES),\
    $(eval $(call define-vndk-sp-lib,$(lib))))

include $(CLEAR_VARS)
LOCAL_MODULE := vndk-sp
LOCAL_MODULE_TAGS := optional
LOCAL_REQUIRED_MODULES := $(addsuffix .vndk-sp-gen,$(VNDK_SP_LIBRARIES))
include $(BUILD_PHONY_PACKAGE)

install_in_hw_dir :=
vndk_sp_dir :=

endif
endif
