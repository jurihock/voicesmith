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

package de.jurihock.voicesmith.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.jurihock.voicesmith.R;

public final class IntervalPicker extends LinearLayout implements
	OnSeekBarChangeListener
{
	private final int					MAX_SEMITONES	= 24;
	private int							interval;

	private TextView					viewSeekLabel	= null;
	private SeekBar						viewSeekBar		= null;

	private PropertyChangeListener	listener		= null;

	/**
	 * Returns the selected interval from -12 to 12 semitones.
	 * */
	public int getInterval()
	{
		return interval;
	}

	public void setInterval(int interval)
	{
		if (this.interval == interval) return;

		this.interval = interval;

		// Update widgets
		viewSeekBar.setProgress(interval + MAX_SEMITONES / 2);
	}

	public void setPropertyChangeListener(PropertyChangeListener listener)
	{
		this.listener = listener;
	}

	public IntervalPicker(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initWidgets(context);
	}

	public IntervalPicker(Context context)
	{
		super(context);
		initWidgets(context);
	}

	private void initWidgets(Context context)
	{
		// Inflate layout
		View.inflate(context, R.layout.intervalpicker, this);

		// Find widgets
		viewSeekLabel = (TextView) findViewById(R.id.viewSeekLabel);
		viewSeekBar = (SeekBar) findViewById(R.id.viewSeekBar);

		// Set listeners
		viewSeekBar.setOnSeekBarChangeListener(this);

		// Disable the SeekBar progress gauge
		LayerDrawable d = (LayerDrawable)
			viewSeekBar.getProgressDrawable();
		d.setDrawableByLayerId(android.R.id.progress,
			new ColorDrawable(android.R.color.transparent));

		// Set SeekBar values
		viewSeekBar.setMax(MAX_SEMITONES);
		viewSeekBar.setSecondaryProgress(MAX_SEMITONES);
		viewSeekBar.setProgress(MAX_SEMITONES / 2);
	}

	/**
	 * Updates the SeekBar label text.
	 * */
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		interval = progress - MAX_SEMITONES / 2;

		String text = String.format(viewSeekLabel.getTag().toString(),
			(interval > 0) ? "+" + interval : "" + interval);

		viewSeekLabel.setText(text);
	}

	/**
	 * Notify the event subscriber.
	 * */
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		if (listener != null)
		{
			listener.propertyChange(new PropertyChangeEvent(
				this, "interval", null, interval));
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar)
	{
		// Not important function
	}
}
