LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libzip
LOCAL_SRC_FILES := lib/libzip.a

include $(PREBUILT_STATIC_LIBRARY)
