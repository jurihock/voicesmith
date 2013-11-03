#!/bin/sh

# ATTENTION: Run this script only from the project root directory!

CLASSNAME=$1
PACKAGE=$2
INPUTDIR=$3
OUTPUTDIR=$4

CLASSPATH=bin/classes:$ANDROID_SDK_HOME/platforms/android-8/android.jar

# Compile the Java source file
javac -classpath $CLASSPATH -d bin/classes src/$INPUTDIR/$CLASSNAME.java

# Make the JNI header file
javah -verbose -jni -classpath $CLASSPATH -o jni/$OUTPUTDIR/$CLASSNAME.h $PACKAGE.$CLASSNAME

# Only if using rather SDK/JDK version than Eclipse --
# delete the compiled Java class file
# rm bin/classes/$INPUTDIR/$CLASSNAME.class