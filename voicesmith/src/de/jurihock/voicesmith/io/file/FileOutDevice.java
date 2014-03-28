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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import android.content.Context;
import android.os.Environment;
import de.jurihock.voicesmith.Utils;

public final class FileOutDevice extends FileDevice
{
	private DataOutputStream	output	= null;

	public FileOutDevice(Context context, String fileName)
		throws IOException
	{
		this(context, fileName, ByteOrder.nativeOrder());
	}

	public FileOutDevice(Context context, String fileName, ByteOrder fileEncoding)
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

		if (file.exists()) file.delete();
		file.createNewFile();

		this.setFilePath(file.getAbsolutePath());
		this.setFileEncoding(fileEncoding);

		output = new DataOutputStream(new FileOutputStream(file));
	}

	@Override
	public int write(short[] buffer, int offset, int count)
	{
		int result = 0;

		final boolean swapBytes =
			this.getFileEncoding() != ByteOrder.nativeOrder();

		for (int i = 0; i < count; i++)
		{
			try
			{
				short value = buffer[i + offset];

				if (swapBytes) value = swapBytes(value);

				output.writeShort(value);
				result++;
			}
			catch (IOException exception)
			{
				new Utils(context).log(exception);
			}
		}

		flush();

		return result;
	}

	@Override
	public void flush()
	{
		try
		{
			output.flush();
		}
		catch (IOException exception)
		{
			new Utils(context).log(exception);
		}
	}

	@Override
	public void dispose()
	{
		if (output != null)
		{
			try
			{
				output.close();
			}
			catch (IOException exception)
			{
				new Utils(context).log(exception);
			}
			finally
			{
				output = null;
			}
		}
	}
}
