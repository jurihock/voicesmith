/*******************************************************************************
 * src/de/jurihock/voicesmith/activities/TransposeActivity.java
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

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.jurihock.voicesmith.R;
import de.jurihock.voicesmith.threads.TransposeThread;

public final class TransposeActivity extends AudioActivity implements
	OnSeekBarChangeListener
{
	private TextView	ctrlSeekBarLabel;
	private SeekBar		ctrlSeekBar;

	public TransposeActivity()
	{
		super(TransposeThread.class,
			R.string.titTranspose,
			R.string.sumTranspose);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.others);

		initControls();
	}

	private void initControls()
	{
		// Find controls
		ctrlSeekBarLabel = (TextView) findViewById(R.id.ctrlSeekBarLabel);
		ctrlSeekBar = (SeekBar) findViewById(R.id.ctrlSeekBar);

		// Set listeners
		ctrlSeekBar.setOnSeekBarChangeListener(this);

		// Disable the SeekBar progress gauge
		LayerDrawable d = (LayerDrawable) ctrlSeekBar.getProgressDrawable();
		d.setDrawableByLayerId(android.R.id.progress,
			new ColorDrawable(android.R.color.transparent));

		// Reset the SeekBar value to "0"
		ctrlSeekBar.setProgress(ctrlSeekBar.getMax() / 2);

		// Change visibility
		ctrlSeekBarLabel.setVisibility(View.VISIBLE);
		ctrlSeekBar.setVisibility(View.VISIBLE);
	}

	/**
	 * Updates the SeekBar label text.
	 * */
	public void onProgressChanged(SeekBar seekBar, int progress,
		boolean fromUser)
	{
		int progressValue = ctrlSeekBar.getProgress() - ctrlSeekBar.getMax()
			/ 2;
		String progressText = (progressValue > 0) ? "+" + progressValue : ""
			+ progressValue;

		ctrlSeekBarLabel.setText(progressText);
	}

	public void onStartTrackingTouch(SeekBar seekBar)
	{
		// Not required at the moment.
	}

	/**
	 * Updates the audio thread context.
	 * */
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		int progressValue = ctrlSeekBar.getProgress()
			- ctrlSeekBar.getMax() / 2;

		((TransposeThread) thread).setSemitones(progressValue);
	}
}
