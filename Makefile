.PHONY: help build clean dev pair log key

KEYDNA = CN=Juergen Hock, O=Voicesmith
KEYARG = -keyalg RSA -keysize 2048 -validity 12345

ADB = ~/Library/Android/sdk/platform-tools/adb

HOST ?= $(shell bash -c 'read -p "HOST> " HOST; echo 192.168.178.$$HOST')
PORT ?= $(shell bash -c 'read -p "PORT> " PORT; echo $$PORT')
CODE ?= $(shell bash -c 'read -p "CODE> " CODE; echo $$CODE')

help:
	@echo build
	@echo clean
	@echo dev
	@echo pair
	@echo log
	@echo key

build:
	@./gradlew assembleRelease lintRelease

clean:
	@./gradlew clean

dev:
	@$(ADB) devices

pair:
	@$(ADB) pair $(HOST):$(PORT) $(CODE)

log:
	@$(ADB) logcat -c
	@$(ADB) logcat -v color voicesmith.java:D voicesmith.cpp:D *:S

key:
	@keytool -genkeypair -keystore local.keystore -alias github-jurihock-voicesmith -dname "$(KEYDNA)" $(KEYARG)

key-base64:
	@base64 -i local.keystore
