/*******************************************************************************
 * src/de/jurihock/voicesmith/activities/AboutActivity.java
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

package de.jurihock.voicesmith.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;
import de.jurihock.voicesmith.R;
import de.jurihock.voicesmith.Utils;

public final class AboutActivity extends Activity
{
	private TextView	ctrlText	= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		initControls();
	}

	private void initControls()
	{
		ctrlText = (TextView) findViewById(R.id.ctrlText);

		ctrlText.append(getVersionString() + '\n');

		InputStream stream = null;
		try
		{
			stream = this.getResources().openRawResource(
				R.raw.about);

			BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				ctrlText.append(line + '\n');
			}
		}
		catch (IOException exception)
		{
			Utils.log(exception);
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException exception)
				{
					Utils.log(exception);
				}
				stream = null;
			}
		}
	}

	private String getVersionString()
	{
		try
		{
			PackageInfo info = getPackageManager()
				.getPackageInfo(getPackageName(), 0);

			return String.format(
				"Voicesmith version %s release %s",
				info.versionName,
				info.versionCode);
		}
		catch (NameNotFoundException exception)
		{
			Utils.log(exception);
		}

		return null;
	}
}
