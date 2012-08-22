package de.jurihock.voicesmith.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public final class ColoredToggleButton extends ToggleButton implements
	OnCheckedChangeListener
{
	private static final int	COLOR_BACKGROUND_ON	= Color.RED;
	private static final int	COLOR_ICON			= Color.WHITE;

	public ColoredToggleButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public ColoredToggleButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public ColoredToggleButton(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		this.setOnCheckedChangeListener(this);
		onCheckedChanged(this, isChecked());
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		StateListDrawable selector = (StateListDrawable) this.getBackground();
		LayerDrawable background = (LayerDrawable) selector.getCurrent();
		Drawable icon = background.getDrawable(1);

		if (isChecked)
		{
			background.setColorFilter(COLOR_BACKGROUND_ON,
				PorterDuff.Mode.SRC_ATOP);

			icon.setColorFilter(COLOR_ICON,
				PorterDuff.Mode.SRC_IN);
		}
		else
		{
			icon.setColorFilter(COLOR_ICON,
				PorterDuff.Mode.SRC_IN);
		}
	}
}