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
import de.jurihock.voicesmith.Disposable;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;

public abstract class AudioDevice implements Disposable
{
	private final Context	context;

	public Context getContext()
	{
		return context;
	}

	private int	sampleRate;

	public int getSampleRate()
	{
		return sampleRate;
	}
	
	protected void setSampleRate(int sampleRate)
	{
		this.sampleRate = sampleRate;
	}
	
	public AudioDevice(Context context)
	{
		this(context, new Preferences(context).getSampleRate());
	}

	public AudioDevice(Context context, int	sampleRate)
	{
		this.context = context;
		this.sampleRate = sampleRate;
		Utils.log("Current sample rate is %s Hz.", sampleRate);
	}

	public int read(short[] buffer, int offset, int count)
	{
		return 0;
	}

	public final boolean read(short[] buffer)
	{
		int count = 0;

		do
		{
			count += read(buffer, count, buffer.length - count);
		}
		while (count < buffer.length);

		return (count == buffer.length);
	}

	public int write(short[] buffer, int offset, int count)
	{
		return 0;
	}

	public final boolean write(short[] buffer)
	{
		int count = 0;

		do
		{
			count += write(buffer, count, buffer.length - count);
		}
		while (count < buffer.length);

		return (count == buffer.length);
	}

	public void flush()
	{
	}

	public void start()
	{
	}

	public void stop()
	{
	}

	public void dispose()
	{
	}
}
