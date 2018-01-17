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

### SYSTEM LIBS
PRODUCT_PACKAGES += \
    libjson \
    libion \
    libxml2

### AUDIO
# For audio.primary
PRODUCT_PACKAGES += \
    libtinyalsa \
    libtinycompress_vendor \
    tinymix

# Audio effects
PRODUCT_PACKAGES += \
    libqcomvisualizer \
    libqcomvoiceprocessing \
    libqcomvoiceprocessingdescriptors \
    libqcompostprocbundle \
    libvolumelistener

### OMX
PRODUCT_PACKAGES += \
    libc2dcolorconvert \
    libstagefrighthw \
    libOmxCore \
    libmm-omxcore \
    libOmxVdec \
    libOmxVdecHevc \
    libOmxVenc

### GPS
PRODUCT_PACKAGES += \
    libloc_api_v02 \
    libloc_core \
    libloc_ds_api \
    libloc_eng \
    libloc_pla \
    libloc_stub \
    libgps.utils

### GNS
PRODUCT_PACKAGES += \
    libgnss \
    libgnsspps

### BLUETOOTH
PRODUCT_PACKAGES += \
    libbt-vendor

### RIL
PRODUCT_PACKAGES += \
    libprotobuf-cpp-full

### NETMGR
PRODUCT_PACKAGES += \
    librmnetctl

# IMS
PRODUCT_PACKAGES += \
    ims-ext-common

### TELEPHONY
PRODUCT_PACKAGES += \
    telephony-ext

### RCS
PRODUCT_PACKAGES += \
    rcs_service_aidl \
    rcs_service_aidl.xml \
    rcs_service_api \
    rcs_service_api.xml

### VNDK
PRODUCT_PACKAGES += \
    vndk-sp
