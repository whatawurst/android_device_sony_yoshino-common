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

### AUDIO
# For audio.primary
PRODUCT_PACKAGES += \
    libaudio-resampler \
    libaudioroute \
    libaudioutils \
    libtinyalsa \
    libtinycompress \
    tinymix

# Audio effects
PRODUCT_PACKAGES += \
    libqcomvisualizer \
    libqcomvoiceprocessing \
    libqcomvoiceprocessingdescriptors \
    libqcompostprocbundle \
    libvolumelistener

### HIDL
PRODUCT_PACKAGES += \
    android.hidl.allocator@1.0.vendor \
    android.hidl.base@1.0 \
    android.hidl.base@1.0.vendor \
    android.hidl.memory@1.0.vendor \
    android.hidl.manager@1.0 \
    android.hidl.manager-V1.0-java \
    libhidltransport \
    libhidltransport.vendor \
    libhwbinder \
    libhwbinder.vendor

### IMS
PRODUCT_PACKAGES += \
    ims-ext-common \
    ims_ext_common.xml \
    libgui_shim

### MEDIA
PRODUCT_PACKAGES += \
    libavservices_minijail.vendor \
    libqdMetaData.system

### NETMGR
PRODUCT_PACKAGES += \
    librmnetctl

### OMX
PRODUCT_PACKAGES += \
    libOmxAacEnc \
    libOmxAmrEnc \
    libOmxCore \
    libOmxEvrcEnc \
    libOmxG711Enc \
    libOmxQcelp13Enc \
    libOmxVdec \
    libOmxVenc \
    libc2dcolorconvert \
    libmm-omxcore \
    libstagefrighthw

# PROTOBUF
PRODUCT_PACKAGES += \
    libprotobuf-cpp-full-vendorcompat \
    libprotobuf-cpp-lite-vendorcompat \
    libprotobuf-cpp-lite-3.9.1-vendorcompat

### RIL
PRODUCT_PACKAGES += \
    libprotobuf-cpp-full

### SYSTEM LIBS
PRODUCT_PACKAGES += \
    libavservices_minijail \
    libjson \
    libion \
    libminijail \
    libstdc++_vendor \
    libtinyxml \
    libxml2

### TELEPHONY
PRODUCT_PACKAGES += \
    telephony-ext \
    qti-telephony-hidl-wrapper \
    qti_telephony_hidl_wrapper.xml \
    qti-telephony-utils \
    qti_telephony_utils.xml

PRODUCT_BOOT_JARS += \
    telephony-ext

### VNDK
PRODUCT_PACKAGES += \
    vndk_package

# VNDFWK_DETECT
PRODUCT_PACKAGES += \
    libqti_vndfwk_detect \
    libqti_vndfwk_detect.vendor \
    libvndfwk_detect_jni.qti \
    libvndfwk_detect_jni.qti.vendor

# libhidlbase-v32
PRODUCT_COPY_FILES += \
    prebuilts/vndk/v32/arm64/arch-arm-armv8-a/shared/vndk-sp/libhidlbase.so:$(TARGET_COPY_OUT_SYSTEM_EXT)/lib/libhidlbase-v32.so \
    prebuilts/vndk/v32/arm64/arch-arm64-armv8-a/shared/vndk-sp/libhidlbase.so:$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64/libhidlbase-v32.so \
    prebuilts/vndk/v32/arm64/arch-arm-armv8-a/shared/vndk-sp/libhidlbase.so:$(TARGET_COPY_OUT_VENDOR)/lib/libhidlbase-v32.so \
    prebuilts/vndk/v32/arm64/arch-arm64-armv8-a/shared/vndk-sp/libhidlbase.so:$(TARGET_COPY_OUT_VENDOR)/lib64/libhidlbase-v32.so
