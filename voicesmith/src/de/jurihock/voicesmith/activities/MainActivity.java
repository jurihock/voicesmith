/*******************************************************************************
 * src/de/jurihock/voicesmith/activities/MainActivity.java
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.jurihock.voicesmith.R;
import de.jurihock.voicesmith.Utils;

public final class MainActivity extends Activity implements OnItemClickListener
{
	private ListView		ctrlMainMenu	= null;

	private final int[]		mainMenuItemIds	=
											{
		R.string.titAbout,
		R.string.titTranspose,
		R.string.titRobotize,
		R.string.titDetune,
		R.string.titHoarseness,
		R.string.titDenoise,
		R.string.titDelay,
		R.string.titPreferences
											};

	private final String[]	mainMenuItems	= new String[mainMenuItemIds.length];

	static
	{
		Utils.loadNativeLibrary();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initControls();
	}

	private void initControls()
	{
		for (int i = 0; i < mainMenuItemIds.length; i++)
		{
			mainMenuItems[i] = this.getString(mainMenuItemIds[i]);
		}

		ctrlMainMenu = (ListView) findViewById(R.id.ctrlMainMenu);
		ctrlMainMenu.setOnItemClickListener(this);
		ctrlMainMenu.setAdapter(new ArrayAdapter<String>(this,
			android.R.layout.simple_list_item_1, mainMenuItems));

	}

	/**
	 * Starts the corresponding activity.
	 * */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Class<?> activityClass;

		switch (position)
		{
		case 0:
			activityClass = AboutActivity.class;
			break;
		case 1:
			activityClass = TransposeActivity.class;
			break;
		case 2:
			activityClass = RobotizeActivity.class;
			break;
		case 3:
			activityClass = DetuneActivity.class;
			break;
		case 4:
			activityClass = HoarsenessActivity.class;
			break;
		case 5:
			activityClass = DenoiseActivity.class;
			break;
		case 6:
			activityClass = DelayActivity.class;
			break;
		default:
			activityClass = PreferencesActivity.class;
			break;
		}

		Intent activityIntent = new Intent(this, activityClass);
		this.startActivity(activityIntent);
	}
}
