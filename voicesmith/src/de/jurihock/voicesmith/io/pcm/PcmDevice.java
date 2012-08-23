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

package de.jurihock.voicesmith.io.pcm;

import android.content.Context;
import de.jurihock.voicesmith.io.AudioDevice;

public abstract class PcmDevice extends AudioDevice
{
	public PcmDevice(Context context, int sampleRate)
	{
		super(context, sampleRate);
	}

	public PcmDevice(Context context)
	{
		super(context);
	}

	private int	audioSource;

	public int getAudioSource()
	{
		return audioSource;
	}

	protected void setAudioSource(int audioSource)
	{
		this.audioSource = audioSource;
	}

	private int	channels;

	public int getChannels()
	{
		return channels;
	}

	protected void setChannels(int channels)
	{
		this.channels = channels;
	}

	private int	encoding;

	public int getEncoding()
	{
		return encoding;
	}

	protected void setEncoding(int encoding)
	{
		this.encoding = encoding;
	}

	private int	minBufferSize;

	/**
	 * [Bytes]
	 * */
	public int getMinBufferSize()
	{
		return minBufferSize;
	}

	/**
	 * [Bytes]
	 * @param minBufferSize [Bytes]
	 * */
	protected void setMinBufferSize(int minBufferSize)
	{
		this.minBufferSize = minBufferSize;
	}

	private int	bufferSize;

	/**
	 * [Bytes]
	 * */
	public int getBufferSize()
	{
		return bufferSize;
	}

	/**
	 * [Bytes]
	 * @param bufferSize [Bytes]
	 * */
	protected void setBufferSize(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}
}
