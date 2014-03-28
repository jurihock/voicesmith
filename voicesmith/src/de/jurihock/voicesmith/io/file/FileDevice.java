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

package de.jurihock.voicesmith.io.file;

import java.nio.ByteOrder;

import android.content.Context;
import de.jurihock.voicesmith.io.AudioDevice;

public abstract class FileDevice extends AudioDevice
{
	public FileDevice(Context context)
	{
		super(context);
	}

	private String	filePath;

	public String getFilePath()
	{
		return filePath;
	}

	protected void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	private ByteOrder	fileEncoding;

	public ByteOrder getFileEncoding()
	{
		return fileEncoding;
	}

	public void setFileEncoding(ByteOrder fileEncoding)
	{
		this.fileEncoding = fileEncoding;
	}

	/**
	 * Swaps low and high bytes of the given short value.
	 * */
	protected static short swapBytes(short value)
	{
		return (short) ((((value >> 0) & 0xFF) << 8) + (((value >> 8) & 0xFF) << 0));
	}
}
