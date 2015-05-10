
TOP_DIR := $(call my-dir)

IDTECH_DIR =  $(TOP_DIR)

LOCAL_PATH := $(call my-dir)


ifeq ($(G),WOLF)
#Wolf 3d
include $(TOP_DIR)/ecwolf-1.3-src/Android.mk
BUILD_SDL := 1
else ifeq ($(G),NOAH)
#NOAH
BUILD_SDL := 1
include $(TOP_DIR)/ecwolf-super-3d-noahs-ark-1be3e5f65f77/Android.mk
else ifeq ($(G),RTCW)
#RTCW
include $(TOP_DIR)/rtcw/Android.mk
BUILD_SERIAL := 1
else ifeq ($(G),Q3)
#Q3
include $(TOP_DIR)/ioq3/Android.mk
include $(TOP_DIR)/openal/Android.mk
BUILD_SDL := 1
else ifeq ($(G),DOOM)
#PRBOOM
include $(TOP_DIR)/GL/Android.mk
include $(TOP_DIR)/prboom/MUSIC/Android.mk
include $(TOP_DIR)/prboom/SDL/Android.mk
include $(TOP_DIR)/prboom/TEXTSCREEN/Android.mk
include $(TOP_DIR)/prboom/PCSOUND/Android.mk
include $(TOP_DIR)/prboom/Android.mk
include $(TOP_DIR)/fluidsynth/Android.mk
#include $(TOP_DIR)/prboom/Android_server.mk
#include $(TOP_DIR)/prboom2/Android.mk
include $(TOP_DIR)/crispy-doom-crispy-doom-1.0/Android.mk

include $(TOP_DIR)/gzdoom-g1.8.6/Android.mk
include $(TOP_DIR)/FMOD_studio/Android.mk
#include $(TOP_DIR)/glshim/Android.mk
include $(TOP_DIR)/openal/Android.mk
#include $(TOP_DIR)/fluidsynth/Android.mk
#include   $(TOP_DIR)/TiMidity++-2.13.0/Android.mk
#BUILD_SERIAL := 1
BUILD_SDL := 1
else ifeq ($(G),DOOM3)
include $(TOP_DIR)/doom3/neo/Android.mk
include $(TOP_DIR)/doom3/neo/Android_game.mk
include $(TOP_DIR)/jpeg8d/Android.mk
include $(TOP_DIR)/curl/Android.mk
include $(TOP_DIR)/openal/Android.mk
include $(TOP_DIR)/liboggvorbis/Android.mk
BUILD_GLES2_TC := 1
else ifeq ($(G),JK2)
#JK2
include $(TOP_DIR)/openal/Android.mk
include $(TOP_DIR)/jk2/Android.mk
include $(TOP_DIR)/jk2/Android_game.mk
BUILD_SERIAL := 1
else ifeq ($(G),JK3)
#JK3
include $(TOP_DIR)/openal/Android.mk
include $(TOP_DIR)/jk3/Android.mk
include $(TOP_DIR)/jk3/Android_game.mk

#JK3 MP
include $(TOP_DIR)/OpenJK/Android.mk
include $(TOP_DIR)/SDL2/Android.mk
include $(TOP_DIR)/jpeg8d/Android.mk
BUILD_SERIAL := 1
else ifeq ($(G),Q2)
#Quake 2
include $(TOP_DIR)/quake2/Android.mk
include $(TOP_DIR)/quake2/Android_ctf_dll.mk
include $(TOP_DIR)/quake2/Android_xsrc_dll.mk
include $(TOP_DIR)/quake2/Android_rsrc_dll.mk
include $(TOP_DIR)/quake2/Android_dll.mk
BUILD_SERIAL := 1
else ifeq ($(G),Q1)
#Quake 1
include $(TOP_DIR)/WinQuake/Android.mk
include $(TOP_DIR)/darkplaces/Android.mk
include $(TOP_DIR)/fteqw/Android.mk
BUILD_SERIAL := 1
BUILD_GLES2_TC := 1
else ifeq ($(G),HEX2)
#Hexen 2
include $(TOP_DIR)/fteqw2/Android.mk
else ifeq ($(G),BS)
#Blake Stone
include $(TOP_DIR)/bstone/Android.mk
include $(TOP_DIR)/SDL2_net/Android.mk
include $(TOP_DIR)/SDL2_mixer/Android.mk
include $(TOP_DIR)/SDL2_image/Android.mk
include $(TOP_DIR)/SDL2/Android.mk
BUILD_GLES2_TC := 1
#BUILD_SDL := 1
else ifeq ($(G),CHOC_HEX)
#Chocolate doom
include $(TOP_DIR)/crispy-doom-crispy-doom-1.0/Android.mk
BUILD_SDL := 1
else ifeq ($(G),CHOC_HERETIC)
#Chocolate doom
include $(TOP_DIR)/crispy-doom-crispy-doom-1.0/Android.mk
BUILD_SDL := 1
else ifeq ($(G),CHOC_STRIFE)
#Chocolate doom
include $(TOP_DIR)/crispy-doom-crispy-doom-1.0/Android.mk
BUILD_SDL := 1
else ifeq ($(G),CHOC_DOOM)
#Chocolate doom
include $(TOP_DIR)/crispy-doom-crispy-doom-1.0/Android.mk
BUILD_SDL := 1
else ifeq ($(G),AVP)
#AVP
include $(TOP_DIR)/openal/Android.mk
#include $(TOP_DIR)/OpenAL-Soft/Android.mk
include $(TOP_DIR)/avpmp/Android.mk
BUILD_SDL := 1
else ifeq ($(G),ALEPH)
#AVP
include $(TOP_DIR)/AlephOne-20140104/Android.mk
BUILD_SDL := 1
else ifeq ($(G),SW)
#Shadow warrior
include $(TOP_DIR)/GL/Android.mk
include $(TOP_DIR)/sw/Android.mk
BUILD_SDL := 1
else ifeq ($(G),DESCENT)
#Descent
include $(TOP_DIR)/descent/Android.mk
BUILD_SDL := 1
else ifeq ($(G),GISH)
#Gish
include $(TOP_DIR)/GishGLES/Android.mk
include $(TOP_DIR)/openal/Android.mk
#include $(TOP_DIR)/OpenAL-Soft/Android.mk
include $(TOP_DIR)/liboggvorbis/Android.mk
BUILD_SDL := 1
else ifeq ($(G),HW)
#Gish
include $(TOP_DIR)/homeworld/src/Android.mk
BUILD_SDL := 1
else
error Choose build
endif

#VAVOOM
#include $(TOP_DIR)/jpeg8d/Android.mk
#include $(TOP_DIR)/vavoom/utils/glvis/Android.mk
#include $(TOP_DIR)/vavoom/utils/glbsp/Android.mk
#include $(TOP_DIR)/vavoom/libs/core/Android.mk
#include $(TOP_DIR)/vavoom/source/timidity/Android.mk
#include $(TOP_DIR)/vavoom/source/Android.mk


#GZDOOM

ifeq ($(BUILD_SDL),1)
#Common SDL

include $(TOP_DIR)/SDL/Android.mk
include $(TOP_DIR)/SDL_net/Android.mk
include $(TOP_DIR)/SDL_mixer/Android.mk
include $(TOP_DIR)/SDL_image/Android.mk
include $(TOP_DIR)/jpeg8d/Android.mk
endif

#Always build
include $(TOP_DIR)/TinyXML/Android.mk
include $(TOP_DIR)/libzip/Android.mk
include $(TOP_DIR)/libpng/Android.mk
include $(TOP_DIR)/sigc++/Android.mk
include $(TOP_DIR)/lvl/Android.mk
include $(TOP_DIR)/TouchControls/Android.mk


ifeq ($(BUILD_SERIAL),1)
include $(TOP_DIR)/../../Serial/jni/Android.mk
include $(TOP_DIR)/curl/Android.mk
endif




