.PHONY: help build lint clean dev con log

ADB = ~/Library/Android/sdk/platform-tools/adb

HOST  = 192.168.178.32
PORT ?= $(shell bash -c 'read -p "> " PORT; echo $$PORT')

help:
	@echo build
	@echo lint
	@echo clean
	@echo dev
	@echo con
	@echo log

build:
	@./gradlew build

lint:
	@./gradlew lint

clean:
	@./gradlew clean

dev:
	@$(ADB) devices

con:
	@$(ADB) connect $(HOST):$(PORT)

log:
	@$(ADB) logcat -v color voicesmith.java:D voicesmith.cpp:D *:S
