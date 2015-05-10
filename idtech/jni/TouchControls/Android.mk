# Copyright (C) 2009 The Android Open Source Project
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
#

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := touchcontrols

LOCAL_CFLAGS := -Werror -DANDROID_NDK -O0 

LOCAL_C_INCLUDES := $(LOCAL_PATH) $(IDTECH_DIR)  $(IDTECH_DIR)/libpng $(IDTECH_DIR)/TinyXML


LOCAL_SRC_FILES:= \
 	TouchControls.cpp \
 	ControlSuper.cpp \
 	RectF.cpp \
 	Button.cpp \
 	OpenGLUtils.cpp \
 	GLRect.cpp \
 	TouchJoy.cpp \
 	Mouse.cpp \
 	MultitouchMouse.cpp \
 	WheelSelect.cpp \
 	TouchControlsContainer.cpp \
 	GLLines.cpp \
 	JNITouchControlsUtils.cpp \
 	
LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog -lOpenSLES 
LOCAL_STATIC_LIBRARIES := sigc libzip libpng tinyxml 

include $(BUILD_SHARED_LIBRARY)


ifeq ($(BUILD_GLES2_TC),1)

TC_LOCAL_SRC_FILES := $(LOCAL_SRC_FILES)
#Now also build gles2 version

include $(CLEAR_VARS)

LOCAL_MODULE := touchcontrols_gles2

LOCAL_CFLAGS := -Werror -DANDROID_NDK -DUSE_GLES2

LOCAL_C_INCLUDES := $(LOCAL_PATH) $(IDTECH_DIR)  $(IDTECH_DIR)/libpng $(IDTECH_DIR)/TinyXML

LOCAL_SRC_FILES:= $(TC_LOCAL_SRC_FILES)

LOCAL_LDLIBS := -lGLESv2 -ldl -llog -lOpenSLES
LOCAL_STATIC_LIBRARIES := sigc libzip libpng tinyxml 

include $(BUILD_SHARED_LIBRARY)

endif
