LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Name of the library without prefix "lib" and file extension
LOCAL_MODULE := Voicesmith

# Optimization flags
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -Wall -O3 -ffast-math -funroll-loops -fomit-frame-pointer

# LogCat support
# LOCAL_LDLIBS := -llog

# Debugging flag
# LOCAL_CFLAGS += -g

# Include all .c/.cpp files to build
LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); \
	find . -type f -name '*.c'; \
	find . -type f -name '*.cpp')

include $(BUILD_SHARED_LIBRARY)