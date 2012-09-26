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

package de.jurihock.voicesmith.activities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.jurihock.voicesmith.AAF;
import de.jurihock.voicesmith.R;
import de.jurihock.voicesmith.Utils;
import de.jurihock.voicesmith.audio.HeadsetMode;
import de.jurihock.voicesmith.services.AafService;
import de.jurihock.voicesmith.services.ServiceListener;
import de.jurihock.voicesmith.widgets.AafPicker;
import de.jurihock.voicesmith.widgets.ColoredToggleButton;
import de.jurihock.voicesmith.widgets.IntervalPicker;

public final class AafActivity extends AudioServiceActivity<AafService>
	implements
	PropertyChangeListener, OnClickListener,
	OnCheckedChangeListener, ServiceListener
{
	// Relevant activity widgets:
	private AafPicker			viewAafPicker			= null;
	private IntervalPicker		viewIntervalPicker		= null;
	private CheckBox			viewBluetoothHeadset	= null;
	private ColoredToggleButton	viewStartStopButton		= null;

	public AafActivity()
	{
		super(AafService.class);
	}

	/**
	 * Initializes the activity, its layout and widgets.
	 * */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setActionBarContentView(R.layout.aaf);

		viewAafPicker = (AafPicker) this.findViewById(R.id.viewAafPicker);
		viewAafPicker.setPropertyChangeListener(this);

		viewIntervalPicker = (IntervalPicker) this.findViewById(
			R.id.viewIntervalPicker);
		viewIntervalPicker.setPropertyChangeListener(this);

		viewBluetoothHeadset = (CheckBox) this.findViewById(
			R.id.viewBluetoothHeadset);
		viewBluetoothHeadset.setOnCheckedChangeListener(this);

		viewStartStopButton = (ColoredToggleButton) this.findViewById(
			R.id.viewStartStopButton);
		// set red if checked otherwise white
		viewStartStopButton.setOnClickListener(this);
	}

	@Override
	protected void onServiceConnected()
	{
		new Utils(this).log("AafActivity founds the service.");

		getService().setActivityVisible(true, this.getClass());
		getService().setListener(this);

		// Update widgets
		viewAafPicker.setAaf(getService().getAaf());
		viewBluetoothHeadset.setChecked(getService().getHeadsetMode()
			== HeadsetMode.BLUETOOTH_HEADSET);
		viewStartStopButton.setChecked(getService().isThreadRunning());

		if (getService().getAaf() == AAF.FAF)
		{
			viewIntervalPicker.setVisibility(View.VISIBLE);

			if(getService().getThreadParams() != null)
			{
				int interval = Integer.parseInt(
					getService().getThreadParams()[0].toString());
				viewIntervalPicker.setInterval(interval);
			}
		}
		else
		{
			viewIntervalPicker.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onServiceDisconnected()
	{
		new Utils(this).log("AafActivity losts the service.");

		if (!this.isFinishing())
		{
			getService().setActivityVisible(false, this.getClass());
		}

		getService().setListener(null);
	}

	public void onClick(View view)
	{
		if (getService().isThreadRunning())
		{
			if (viewStartStopButton.isChecked())
				viewStartStopButton.setChecked(false);
			
			getService().stopThread(false);
		}
		else
		{
			if (!viewStartStopButton.isChecked())
				viewStartStopButton.setChecked(true);
			
			getService().startThread();
		}

		// BZZZTT!!1!
		viewStartStopButton.performHapticFeedback(0);
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (isChecked && getService().getHeadsetMode()
				== HeadsetMode.WIRED_HEADSET)
		{
			getService().setHeadsetMode(HeadsetMode.BLUETOOTH_HEADSET);
		}
		else if (!isChecked && getService().getHeadsetMode()
				== HeadsetMode.BLUETOOTH_HEADSET)
		{
			getService().setHeadsetMode(HeadsetMode.WIRED_HEADSET);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource().equals(viewAafPicker))
		{
			AAF aaf = viewAafPicker.getAaf();

			getService().setAaf(aaf);

			if (aaf == AAF.FAF)
			{
				viewIntervalPicker.setVisibility(View.VISIBLE);

				getService().setThreadParams(
					viewIntervalPicker.getInterval());
			}
			else
			{
				viewIntervalPicker.setVisibility(View.GONE);
			}
		}
		else if (event.getSource().equals(viewIntervalPicker))
		{
			int interval = viewIntervalPicker.getInterval();

			getService().setThreadParams(interval);
		}
	}
	
	public void onServiceFailed()
	{
		if (viewStartStopButton.isChecked())
			viewStartStopButton.setChecked(false);

		new Utils(this).toast(getString(R.string.ServiceFailureMessage));

		// BZZZTT!!1!
		viewStartStopButton.performHapticFeedback(0);
	}
}
