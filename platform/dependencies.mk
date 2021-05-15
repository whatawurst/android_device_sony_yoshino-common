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
    libtinycompress_vendor \
    tinymix

# Audio effects
PRODUCT_PACKAGES += \
    libqcomvisualizer \
    libqcomvoiceprocessing \
    libqcomvoiceprocessingdescriptors \
    libqcompostprocbundle \
    libvolumelistener

### CAMERA
PRODUCT_PACKAGES += \
    libmmcamera_interface \
    libmmjpeg_interface \
    libmmlib2d_interface \
    libmm-qcamera \
    libqomx_core

### HIDL
PRODUCT_PACKAGES += \
    android.hidl.base@1.0 \
    android.hidl.base@1.0.vendor \
    android.hidl.manager@1.0 \
    android.hidl.manager-V1.0-java \
    libhidltransport \
    libhidltransport.vendor \
    libhwbinder \
    libhwbinder.vendor

### IMS
PRODUCT_PACKAGES += \
    ims-ext-common \
    ims-ext-common_system \
    ims_ext_common.xml

PRODUCT_BOOT_JARS += \
    ims-ext-common_system

### IPV6
PRODUCT_PACKAGES += \
    ebtables \
    ethertypes

### MEDIA
PRODUCT_PACKAGES += \
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
    libOmxVdecHevc \
    libOmxVenc \
    libc2dcolorconvert \
    libmm-omxcore \
    libstagefrighthw

# PROTOBUF
PRODUCT_PACKAGES += \
    libprotobuf-cpp-full-vendorcompat \
    libprotobuf-cpp-lite-vendorcompat

### RCS
PRODUCT_PACKAGES += \
    rcs_service_aidl \
    rcs_service_aidl.xml \
    rcs_service_api \
    rcs_service_api.xml

### RIL
PRODUCT_PACKAGES += \
    libprotobuf-cpp-full

### SYSTEM LIBS
PRODUCT_PACKAGES += \
    libavservices_minijail \
    libjson \
    libion \
    libminijail \
    libstdc++.vendor \
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
    vndk_package \
    vndk-sp

# VNDFWK_DETECT
PRODUCT_PACKAGES += \
    libqti_vndfwk_detect \
    libqti_vndfwk_detect.vendor

### WIFI DISPLAY
PRODUCT_PACKAGES += \
    libaacwrapper \
    libnl \
    libmediaextractorservice

PRODUCT_BOOT_JARS += \
    WfdCommon
