LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

	# Name of the library without prefix "lib" and file extension
	LOCAL_MODULE := Voicesmith

	# Instruction set "thumb" or "arm"
	LOCAL_ARM_MODE := arm

	# Debugging flag
	# LOCAL_CFLAGS := -g

	# LogCat library
	# LOCAL_LDLIBS := -llog

	# Include all .c/.cpp files to build
	LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); find . -type f -name '*.c'; find . -type f -name '*.cpp')

include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY)