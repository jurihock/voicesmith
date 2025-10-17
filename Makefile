.PHONY: help build clean dev pair log

ADB = ~/Library/Android/sdk/platform-tools/adb

HOST ?= $(shell bash -c 'read -p "HOST> " HOST; echo 192.168.178.$$HOST')
PORT ?= $(shell bash -c 'read -p "PORT> " PORT; echo $$PORT')
CODE ?= $(shell bash -c 'read -p "CODE> " CODE; echo $$CODE')

help:
	@echo build
	@echo clean
	@echo apk
	@echo dev
	@echo pair
	@echo log

build:
	@./gradlew build

clean:
	@./gradlew clean

apk:
	@./gradlew assembleRelease
	@ls ./voicesmith/build/outputs/apk/release/*.apk

dev:
	@$(ADB) devices

pair:
	@$(ADB) pair $(HOST):$(PORT) $(CODE)

log:
	@$(ADB) logcat -c
	@$(ADB) logcat -v color voicesmith.java:D voicesmith.cpp:D *:S
