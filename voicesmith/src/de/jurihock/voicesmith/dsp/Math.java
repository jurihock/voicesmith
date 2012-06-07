/*******************************************************************************
 * src/de/jurihock/voicesmith/dsp/Math.java
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

package de.jurihock.voicesmith.dsp;

public final class Math
{
	public static final float	PI	= (float) java.lang.Math.PI;
	
	public static int round(float value)
	{
		return java.lang.Math.round(value);
	}
	
	public static native float pow(float base, float exponent);

	public static native float min(float a, float b);
	
	public static native float max(float a, float b);
	
	public static native float floor(float value);

	public static native float ceil(float value);

	public static native float sin(float angle);

	public static native float cos(float angle);

	public static native float sqrt(float value);

	public static native float atan2(float y, float x);
	
	public static native float abs(float real, float imag);
	
	public static native float arg(float real, float imag);
	
	public static native float real(float abs, float arg);
	
	public static native float imag(float abs, float arg);

	public static native float random(float min, float max);

	public static native float princarg(float phase);
}
