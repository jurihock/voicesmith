/*******************************************************************************
 * jni/Math/MathDefs.h
 * is part of the Voicesmith project
 * <http://voicesmith.jurihock.de>
 *
 * Copyright (C) 2011-2012 Juergen Hock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

#ifndef MathDefs_h
#define MathDefs_h

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
