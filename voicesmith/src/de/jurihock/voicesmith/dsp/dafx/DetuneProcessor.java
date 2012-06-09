/*******************************************************************************
 * src/de/jurihock/voicesmith/dsp/dafx/DetuneProcessor.java
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

package de.jurihock.voicesmith.dsp.dafx;

import static de.jurihock.voicesmith.dsp.Math.abs;
import static de.jurihock.voicesmith.dsp.Math.arg;
import static de.jurihock.voicesmith.dsp.Math.imag;
import static de.jurihock.voicesmith.dsp.Math.real;

public final class DetuneProcessor
{
	public static void processFrame(float[] frame)
	{
		final int fftSize = frame.length / 2;
		float re, im, abs, phase;

		for (int i = 1; i < fftSize; i++)
		{
			// Get source Re and Im parts
			re = frame[2 * i];
			im = frame[2 * i + 1];
			abs = abs(re, im);

			// Invert phase value
			phase = -arg(re, im);

			// Compute destination Re and Im parts
			frame[2 * i] = real(abs, phase);
			frame[2 * i + 1] = imag(abs, phase);
		}
	}
}
