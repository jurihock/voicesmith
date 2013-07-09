/*******************************************************************************
 * Voicesmith <http://voicesmith.jurihock.de/>
 * Copyright (c) 2011-2012 Juergen Hock
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

package de.jurihock.voicesmith.io.oscillators;

import static de.jurihock.voicesmith.dsp.Math.cos;
import android.content.Context;

public final class CosineWave extends PhaseAccumulator
{
	public CosineWave(Context context, int frequency)
	{
		super(context, frequency);
	}

	public CosineWave(Context context, int sampleRate, int frequency)
	{
		super(context, sampleRate, frequency);
	}

	public int read(float[] buffer, int offset, int count)
	{
		for (int i = 0; i < count; i++)
		{
			buffer[i + offset] = cos(getNextPhase());
		}

		return count;
	}

	@Override
	public int read(short[] buffer, int offset, int count)
	{
		final float amp = 32767F / 3; // TODO: Cosine amplitude getter/setter.
		
		for (int i = 0; i < count; i++)
		{
			buffer[i + offset] = (short) (amp * cos(getNextPhase()));
		}

		return count;
	}
}
