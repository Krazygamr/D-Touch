LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := tinyxml
LOCAL_SRC_FILES := lib/libtinyxml.a

include $(PREBUILT_STATIC_LIBRARY)
