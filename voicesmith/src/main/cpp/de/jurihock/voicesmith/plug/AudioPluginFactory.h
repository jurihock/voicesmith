#pragma once

#include <voicesmith/Header.h>

#include <voicesmith/etc/JNA.h>

jna bool voicesmith_plugin_open(const char* name, jna_callback* callback, jna_pointer* pointer, jna_result* result);
jna bool voicesmith_plugin_setup(int input, int output, int samplerate, int blocksize, int channels, jna_pointer* pointer, jna_result* result);
jna bool voicesmith_plugin_set(const char* param, const char* value, jna_pointer* pointer, jna_result* result);
jna bool voicesmith_plugin_start(jna_pointer* pointer, jna_result* result);
jna bool voicesmith_plugin_stop(jna_pointer* pointer, jna_result* result);
jna bool voicesmith_plugin_close(jna_pointer* pointer, jna_result* result);
