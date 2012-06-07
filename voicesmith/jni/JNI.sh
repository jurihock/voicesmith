#!/bin/bash

# Run this script only from project root directory!

CLASSPATH=bin/classes:$ANDROID_SDK_HOME/platforms/android-8/android.jar

function GenerateJNI()
{
	CLASSNAME=$1
	PACKAGE=$2
	INPUTDIR=$3
	OUTPUTDIR=$4

	# Compile the Java source file
	javac -classpath $CLASSPATH -d bin/classes src/$INPUTDIR/$CLASSNAME.java

	# Make the JNI header file
	javah -verbose -jni -classpath $CLASSPATH -o jni/$OUTPUTDIR/$CLASSNAME.h $PACKAGE.$CLASSNAME

	# Only if using rather (S/J)DK version than Eclipse --
	# delete the compiled Java class file
	# rm bin/classes/$INPUTDIR/$CLASSNAME.class
}

GenerateJNI "Math" \
	"de.jurihock.voicesmith.dsp" \
	"de/jurihock/voicesmith/dsp" \
	"Math"

GenerateJNI "KissFFT" \
	"de.jurihock.voicesmith.dsp" \
	"de/jurihock/voicesmith/dsp" \
	"KissFFT"

GenerateJNI "NativeResampleProcessor" \
	"de.jurihock.voicesmith.dsp.dafx" \
	"de/jurihock/voicesmith/dsp/dafx" \
	"DAFX"

GenerateJNI "NativeTimescaleProcessor" \
	"de.jurihock.voicesmith.dsp.dafx" \
	"de/jurihock/voicesmith/dsp/dafx" \
	"DAFX"