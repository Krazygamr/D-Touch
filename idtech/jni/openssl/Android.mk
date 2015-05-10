LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := openssl
LOCAL_SRC_FILES := libs/libssl.so 

include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := opencrypto
LOCAL_SRC_FILES := libs/libcrypto.so

include $(PREBUILT_SHARED_LIBRARY)