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
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import de.jurihock.voicesmith.DAFX;
import de.jurihock.voicesmith.R;

public final class DafxPicker extends RadioGroup implements
	OnClickListener
{
	private DAFX				dafx;

	private final RadioButton[]	buttons	= new RadioButton[DAFX.count()];

	private PropertyChangeListener	listener;

	public DAFX getDafx()
	{
		return dafx;
	}

	public void setDafx(DAFX dafx)
	{
		if (this.dafx == dafx) return;

		this.dafx = dafx;

		// Update buttons
		for (int i = 0; i < buttons.length; i++)
		{
			boolean isButtonChecked =
				buttons[i].getTag().toString()
					.equals(dafx.toString());

			buttons[i].setChecked(isButtonChecked);
		}
	}

	public void setPropertyChangeListener(PropertyChangeListener listener)
	{
		this.listener = listener;
	}

	public DafxPicker(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initButtons(context);
	}

	public DafxPicker(Context context)
	{
		super(context);
		initButtons(context);
	}

	private void initButtons(Context context)
	{
		// Inflate layout
		View.inflate(context, R.layout.dafxpicker, this);

		// Fish out all buttons
		DAFX[] dafxValues = DAFX.values();
		for (int i = 0; i < DAFX.count(); i++)
		{
			RadioButton button = (buttons[i] =
				(RadioButton) this.findViewWithTag(
					dafxValues[i].toString()));

			button.setOnClickListener(this);
		}

		// Check the first button by default
		dafx = DAFX.valueOf(0);
		buttons[0].setChecked(true);
	}

	public void onClick(View view)
	{
		Object tag = view.getTag();
		dafx = DAFX.valueOf(tag.toString());

		// Manually uncheck other buttons
		for (RadioButton button : buttons)
		{
			if (!button.getTag().equals(tag))
			{
				button.setChecked(false);
			}
		}

		// Notify the event subscriber
		if (listener != null)
		{
			listener.propertyChange(new PropertyChangeEvent(
				this, "dafx", null, dafx));
		}
	}
}
