/*
 * Voicesmith <http://voicesmith.jurihock.de/>
 *
 * Copyright (c) 2011-2014 Juergen Hock
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
 */

package de.jurihock.voicesmith.io;

import android.content.Context;
import de.jurihock.voicesmith.Disposable;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.Utils;

public abstract class AudioDevice implements Disposable
{
	protected final Context	context;

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
		new Utils(context).log("Current sample rate is %s Hz.", sampleRate);
	}

	public int read(short[] buffer, int offset, int count)
	{
		return -1;
	}

	public final boolean read(short[] buffer)
	{
        if (buffer == null) return false;
        if (buffer.length == 0) return false;

		int count = 0;

		do
		{
            int result = read(buffer, count, buffer.length - count);
            if (result < 0) return false; // error on reading data
			count += result;
		}
		while (count < buffer.length);

		return true;
	}

	public int write(short[] buffer, int offset, int count)
	{
		return -1;
	}

	public final boolean write(short[] buffer)
	{
        if (buffer == null) return false;
        if (buffer.length == 0) return false;

		int count = 0;

		do
		{
            int result = write(buffer, count, buffer.length - count);
            if (result < 0) return false; // error on writing data
            count += result;
		}
		while (count < buffer.length);

        return true;
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
