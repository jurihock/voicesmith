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

package de.jurihock.voicesmith.io;

import android.content.Context;
import de.jurihock.voicesmith.io.AudioDevice;

import static de.jurihock.voicesmith.dsp.Math.*;

public final class SineWaveOscillator extends AudioDevice
{
	private final float	a	= 32767F / 3;
	private final float	f	= 440;

	private final int	N	= getSampleRate();
	private int			n	= 0;

	public SineWaveOscillator(Context context)
	{
		super(context);
	}

	@Override
	public int read(short[] buffer, int offset, int count)
	{
		for (int i = 0; i < count; i++)
		{
			float t = (float) n / N;
			short value = (short) (a * sin(2 * PI * f * t));

			buffer[i + offset] = value;
			if (++n >= N) n = 0;
		}

		return count;
	}

	@Override
	public void flush()
	{
		n = 0;
	}
}
