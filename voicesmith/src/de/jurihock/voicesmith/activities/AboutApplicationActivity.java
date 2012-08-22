/*
 * Voicesmith <http://voicesmith.jurihock.de>
 *
 * Copyright (c) 2011-2012 by Juergen Hock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.jurihock.voicesmith.activities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import de.jurihock.voicesmith.R;
import de.jurihock.voicesmith.Utils;

public class AboutApplicationActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		TextView viewVersion = (TextView)findViewById(R.id.viewVersion);
		viewVersion.setText(getVersionString());
	}

	private String getVersionString()
	{
		try
		{
			PackageInfo info = getPackageManager()
				.getPackageInfo(getPackageName(), 0);

			return String.format(
				"Version %s Release %s",
				info.versionName,
				info.versionCode);
		}
		catch (PackageManager.NameNotFoundException exception)
		{
			Utils.log(exception);
		}

		return null;
	}
}
