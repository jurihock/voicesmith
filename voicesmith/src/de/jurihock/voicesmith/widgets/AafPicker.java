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
import de.jurihock.voicesmith.AAF;
import de.jurihock.voicesmith.R;

public final class AafPicker extends RadioGroup implements
	OnClickListener
{
	private AAF						aaf;

	private final RadioButton[]		buttons	= new RadioButton[AAF.count()];

	private PropertyChangeListener	listener;

	public AAF getAaf()
	{
		return aaf;
	}

	public void setAaf(AAF aaf)
	{
		if (this.aaf == aaf) return;

		this.aaf = aaf;

		// Update buttons
		for (int i = 0; i < buttons.length; i++)
		{
			boolean isButtonChecked =
				buttons[i].getTag().toString()
					.equals(aaf.toString());

			buttons[i].setChecked(isButtonChecked);
		}
	}

	public void setPropertyChangeListener(PropertyChangeListener listener)
	{
		this.listener = listener;
	}

	public AafPicker(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initButtons(context);
	}

	public AafPicker(Context context)
	{
		super(context);
		initButtons(context);
	}

	private void initButtons(Context context)
	{
		// Inflate layout
		View.inflate(context, R.layout.aafpicker, this);

		// Fish out all buttons
		AAF[] aafValues = AAF.values();
		for (int i = 0; i < AAF.count(); i++)
		{
			RadioButton button = (buttons[i] =
				(RadioButton) this.findViewWithTag(
					aafValues[i].toString()));

			button.setOnClickListener(this);
		}

		// Check the first button by default
		aaf = AAF.valueOf(0);
		buttons[0].setChecked(true);
	}

	public void onClick(View view)
	{
		Object tag = view.getTag();
		aaf = AAF.valueOf(tag.toString());

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
				this, "aaf", null, aaf));
		}
	}
}
