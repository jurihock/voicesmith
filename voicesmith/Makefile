# This makefile handles only the JNI stuff

.PHONY: jni

default: build

build:
	ndk-build

verbose:
	ndk-build V=1

clean:
	ndk-build clean

jni:
	./jni/JNI.sh
