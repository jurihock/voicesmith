#include "Math.h"
#include "../MATLAB.h"
#include <stdlib.h>

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_pow
  (JNIEnv *, jclass, jfloat base, jfloat exponent)
{
	return powf(base, exponent);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_min
  (JNIEnv *, jclass, jfloat a, jfloat b)
{
	return fmin(a, b);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_max
  (JNIEnv *, jclass, jfloat a, jfloat b)
{
	return fmax(a, b);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_floor
  (JNIEnv *, jclass, jfloat value)
{
	return floorf(value);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_ceil
  (JNIEnv *, jclass, jfloat value)
{
	return ceilf(value);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_sin
  (JNIEnv *, jclass, jfloat angle)
{
	return sinf(angle);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_cos
  (JNIEnv *, jclass, jfloat angle)
{
	return cosf(angle);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_sqrt
  (JNIEnv *, jclass, jfloat value)
{
	return sqrtf(value);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_atan2
  (JNIEnv *, jclass, jfloat y, jfloat x)
{
	return atan2f(y, x);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_abs
  (JNIEnv *, jclass, jfloat real, jfloat imag)
{
	return sqrtf(real * real + imag * imag);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_arg
  (JNIEnv *, jclass, jfloat real, jfloat imag)
{
	return atan2f(imag, real);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_real
  (JNIEnv *, jclass, jfloat abs, jfloat arg)
{
	return abs * cosf(arg);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_imag
  (JNIEnv *, jclass, jfloat abs, jfloat arg)
{
	return abs * sinf(arg);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_random
  (JNIEnv *, jclass, jfloat min, jfloat max)
{
	// TODO: min < result <= max

	float next = rand() / (float)RAND_MAX;

	// min <= result < max
	return min + next * (max - min);
}

JNIEXPORT jfloat JNICALL Java_de_jurihock_voicesmith_dsp_Math_princarg
  (JNIEnv *, jclass, jfloat phase)
{
	return princargf(phase);
}
