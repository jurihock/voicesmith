#ifndef Math_h
#define Math_h

#include <math.h>

const float PI = M_PI;

// MATLAB conform modulo implementation.
static float modf(float x, float y)
{
	return x - (y * floorf(x / y));
}

// Returns the principal phase argument,
// so that princarg(2*PI*n + phi) = phi,
// where -PI < phi <= PI.
static float princargf(float phase)
{
	return modf(phase + PI, 2 * PI) - PI;
}

#endif
