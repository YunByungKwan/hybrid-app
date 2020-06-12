LOCAL_PATH := $(call my-dir)

# --------------- crypto ---------------
include $(CLEAR_VARS)
LOCAL_MODULE := crypto
LOCAL_MODULE_FILENAME := crypto
LOCAL_SRC_FILES := curl/$(TARGET_ARCH_ABI)/libcrypto.a
LOCAL_EXPORT_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)
# --------------- ssl ---------------
include $(CLEAR_VARS)
LOCAL_MODULE := ssl
LOCAL_MODULE_FILENAME := ssl
LOCAL_SRC_FILES := curl/$(TARGET_ARCH_ABI)/libssl.a
include $(PREBUILT_STATIC_LIBRARY)
# --------------- curl ---------------
include $(CLEAR_VARS)
LOCAL_MODULE := curl
LOCAL_MODULE_FILENAME := curl
LOCAL_SRC_FILES := curl/$(TARGET_ARCH_ABI)/libcurl.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/curl/include
LOCAL_STATIC_LIBRARIES += ssl
LOCAL_STATIC_LIBRARIES += crypto
include $(PREBUILT_STATIC_LIBRARY)
# -----------

include $(CLEAR_VARS)
LOCAL_MODULE        := main
LOCAL_CPPFLAGS += -std=c++14
LOCAL_SRC_FILES     := main.cpp
LOCAL_STATIC_LIBRARIES += android_native_app_glue
LOCAL_STATIC_LIBRARIES  += curl

include $(BUILD_SHARED_LIBRARY)

$(call import-module,android/native_app_glue)

# LOCAL_STATIC_LIBRARIES += android_native_app_glue
# $(call import-module,android/native_app_glue)

