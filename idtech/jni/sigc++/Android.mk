LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := sigc
LOCAL_SRC_FILES := lib/libsigc.a

include $(PREBUILT_STATIC_LIBRARY)
