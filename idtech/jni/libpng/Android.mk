LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libpng
LOCAL_SRC_FILES := lib/libpng.a
LOCAL_EXPORT_LDLIBS := -lz 
include $(PREBUILT_STATIC_LIBRARY)
