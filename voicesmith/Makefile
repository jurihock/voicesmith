.PHONY: jni

default: help

help:
	@echo "make jni:             Generate JNI headers"
	@echo "make build[-verbose]: Build JNI sources"
	@echo "make release:         Make release APK"
	@echo "make install:         Install released APK"
	@echo "make uninstall:       Uninstall released APK"
	@echo "make clean:           Clean build/release files"

jni:
	jni/GenerateHeaders.sh \
		"Math" \
		"de.jurihock.voicesmith.dsp" \
		"de/jurihock/voicesmith/dsp" \
		"Math"
	jni/GenerateHeaders.sh \
		"KissFFT" \
		"de.jurihock.voicesmith.dsp" \
		"de/jurihock/voicesmith/dsp" \
		"KissFFT"
	jni/GenerateHeaders.sh \
		"NativeResampleProcessor" \
		"de.jurihock.voicesmith.dsp.processors" \
		"de/jurihock/voicesmith/dsp/processors" \
		"Processors"
	jni/GenerateHeaders.sh \
		"NativeTimescaleProcessor" \
		"de.jurihock.voicesmith.dsp.processors" \
		"de/jurihock/voicesmith/dsp/processors" \
		"Processors"

build:
	ndk-build

build-verbose:
	ndk-build V=1

release:
	ant release

install:
	adb installr

uninstall:
	adb uninstall de.jurihock.voicesmith

clean:
	ndk-build clean && rm -rfv libs obj
	ant clean
