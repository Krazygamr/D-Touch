LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := nkd_lvl
LOCAL_SRC_FILES := lib/libndk_lvl.so

include $(PREBUILT_SHARED_LIBRARY)
