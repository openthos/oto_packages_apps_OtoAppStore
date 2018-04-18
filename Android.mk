#
# Copyright (C) 2013 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)
#
# Build app code.
#
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-recyclerview
LOCAL_STATIC_JAVA_LIBRARIES += design
LOCAL_STATIC_JAVA_LIBRARIES += image-loader

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
    $(call all-renderscript-files-under, src) \
    $(call all-proto-files-under, protos) \
    $(call all-subdir-Java-files) \
    ../OtoCloudService/src/org/openthos/seafile/ISeafileService.aidl

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_PROTOC_OPTIMIZE_TYPE := nano

LOCAL_PROTOC_FLAGS := --proto_path=$(LOCAL_PATH)/protos/

LOCAL_SDK_VERSION := current

LOCAL_PACKAGE_NAME := OtoAppStore
#LOCAL_CERTIFICATE := shared
#LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := design:libs/android-support-design.jar \
                                        image-loader:libs/universal-image-loader-1.9.3.jar

include $(BUILD_MULTI_PREBUILT)

include $(CLEAR_VARS)
