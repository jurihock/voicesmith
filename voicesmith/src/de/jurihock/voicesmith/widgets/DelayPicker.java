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

package de.jurihock.voicesmith.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.jurihock.voicesmith.R;

public final class DelayPicker extends LinearLayout implements
	OnSeekBarChangeListener
{
	private int						delay;

	private TextView				viewSeekLabel	= null;
	private SeekBar					viewSeekBar		= null;

	private PropertyChangeListener	listener		= null;

	public int getDelay()
	{
		return delay;
	}

	public void setDelay(int delay)
	{
		if (this.delay == delay) return;

		this.delay = delay;

		// Update widgets
		viewSeekBar.setProgress(delay);
	}

	public void setPropertyChangeListener(PropertyChangeListener listener)
	{
		this.listener = listener;
	}

	public DelayPicker(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initWidgets(context);
	}

	public DelayPicker(Context context)
	{
		super(context);
		initWidgets(context);
	}

	private void initWidgets(Context context)
	{
		// Inflate layout
		View.inflate(context, R.layout.delaypicker, this);

		// Find widgets
		viewSeekLabel = (TextView) findViewById(R.id.viewSeekLabel);
		viewSeekBar = (SeekBar) findViewById(R.id.viewSeekBar);

		// Set listeners
		viewSeekBar.setOnSeekBarChangeListener(this);

		// Set SeekBar values
		viewSeekBar.setMax(1000);
		viewSeekBar.setProgress(0);
	}

	/**
	 * Updates the SeekBar label text.
	 * */
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		delay = progress;

		String text = String.format(viewSeekLabel.getTag().toString(), delay);

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
				this, "interval", null, delay));
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar)
	{
		// Not important function
	}
}
