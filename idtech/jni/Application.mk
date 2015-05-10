#APP_ABI :=  armeabi-v7a

ifeq ($(G),Q2)

else
APP_ABI      := armeabi-v7a
NDK_TOOLCHAIN_VERSION=4.8
endif

APP_STL := gnustl_static
#APP_STL  := stlport_static
#APP_MODULES := libsigc++  
