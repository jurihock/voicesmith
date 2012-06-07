/*******************************************************************************
 * src/de/jurihock/voicesmith/activities/PreferencesActivity.java
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

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.jurihock.voicesmith.Preferences;
import de.jurihock.voicesmith.R;

public final class PreferencesActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.preferences);
		this.addPreferencesFromResource(R.xml.preferences);

		new Preferences(this);

		initPreferenceListeners();
	}

	private void initPreferenceListeners()
	{
		EditTextPreference prefVolumeLevel = (EditTextPreference)
			this.findPreference("prefVolumeLevel");
		{
			prefVolumeLevel.setSummary(prefVolumeLevel.getText() + "%");

			prefVolumeLevel.setOnPreferenceChangeListener(
				new Preference.OnPreferenceChangeListener()
				{
					public boolean onPreferenceChange(Preference preference, Object newValue)
					{
						try
						{
							int volumeLevel = Integer.parseInt(
								(String) newValue);

							if((0 <= volumeLevel) && (volumeLevel <= 100))
							{
								preference.setSummary(newValue + "%");

								return true;
							}
						}
						catch (NumberFormatException exception)
						{
						}
						
						return false;
					}
				});
		}

		ListPreference prefSampleRate = (ListPreference)
			this.findPreference("prefSampleRate");
		{
			prefSampleRate.setSummary(prefSampleRate.getValue() + " Hz");

			prefSampleRate.setOnPreferenceChangeListener(
				new Preference.OnPreferenceChangeListener()
				{
					public boolean onPreferenceChange(Preference preference, Object newValue)
					{
						preference.setSummary(newValue + " Hz");

						return true;
					}
				});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.preferences, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.ctrlResetPreferences:
			new Preferences(this).reset();
			finish();
			break;
		}

		return true;
	}
}
