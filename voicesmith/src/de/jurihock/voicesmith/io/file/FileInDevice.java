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

package de.jurihock.voicesmith.io.file;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import android.content.Context;
import android.os.Environment;
import de.jurihock.voicesmith.Utils;

public final class FileInDevice extends FileDevice
{
	private DataInputStream	input	= null;

	public FileInDevice(Context context, String fileName)
		throws IOException
	{
		this(context, fileName, ByteOrder.nativeOrder());
	}

	public FileInDevice(Context context, String fileName, ByteOrder fileEncoding)
		throws IOException
	{
		super(context);

		if (!Environment.MEDIA_MOUNTED.equals(
			Environment.getExternalStorageState()))
		{
			throw new IOException("Unable to mount external storage!");
		}

		File fileDir = Environment.getExternalStorageDirectory();
		File file = new File(fileDir, fileName);

		this.setFilePath(file.getAbsolutePath());
		this.setFileEncoding(fileEncoding);

		input = new DataInputStream(new FileInputStream(file));
	}

	@Override
	public int read(short[] buffer, int offset, int count)
	{
		int result = 0;

		final boolean swapBytes =
			this.getFileEncoding() != ByteOrder.nativeOrder();

		for (int i = 0; i < count; i++)
		{
			try
			{
				short value = input.readShort();

				if (swapBytes) value = swapBytes(value);

				buffer[i + offset] = value;
				result++;
			}
			catch (EOFException eof)
			{
				buffer[i + offset] = 0;
				result++;
			}
			catch (IOException exception)
			{
				new Utils(context).log(exception);
			}
		}

		return result;
	}

	@Override
	public void dispose()
	{
		if (input != null)
		{
			try
			{
				input.close();
			}
			catch (IOException exception)
			{
				new Utils(context).log(exception);
			}
			finally
			{
				input = null;
			}
		}
	}
}
