.PHONY: libs

default: help

help:
	@echo "make libs: Download required third-party libraries"

libs:
	mkdir -p ./libs
	wget -q -O ./libs/mockito-all-1.9.5.jar    https://mockito.googlecode.com/files/mockito-all-1.9.5.jar
	wget -q -O ./libs/dexmaker-1.0.jar         http://dexmaker.googlecode.com/files/dexmaker-1.0.jar
	wget -q -O ./libs/dexmaker-mockito-1.0.jar http://dexmaker.googlecode.com/files/dexmaker-mockito-1.0.jar