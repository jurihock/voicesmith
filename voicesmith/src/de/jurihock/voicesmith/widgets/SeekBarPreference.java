package de.jurihock.voicesmith.widgets;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import de.jurihock.voicesmith.R;

public class SeekBarPreference extends DialogPreference implements
	OnSeekBarChangeListener
{
	private static final String	ANDROID_NS			= "http://schemas.android.com/apk/res/android";
	private static final String	VOICESMITH_NS		= "http://voicesmith.jurihock.de";

	private static final String	MIN_VALUE_ATTR		= "minValue";
	private static final String	MAX_VALUE_ATTR		= "maxValue";
	private static final String	DEFAULT_VALUE_ATTR	= "defaultValue";
	private static final String	VALUE_FORMAT_ATTR	= "valueFormat";

	private static final int	DEFAULT_MIN_VALUE	= 0;
	private static final int	DEFAULT_MAX_VALUE	= 100;
	private static final int	DEFAULT_VALUE		= 0;

	private final int			minValue;
	private final int			maxValue;
	private final int			defaultValue;
	private final String		valueFormat;
	private int					currentValue;

	private TextView			label				= null;

	public SeekBarPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		// Get attribute values
		minValue = attrs.getAttributeIntValue(
			VOICESMITH_NS, MIN_VALUE_ATTR, DEFAULT_MIN_VALUE);
		maxValue = attrs.getAttributeIntValue(
			VOICESMITH_NS, MAX_VALUE_ATTR, DEFAULT_MAX_VALUE);
		defaultValue = attrs.getAttributeIntValue(
			ANDROID_NS, DEFAULT_VALUE_ATTR, DEFAULT_VALUE);
		valueFormat = attrs.getAttributeValue(
			VOICESMITH_NS, VALUE_FORMAT_ATTR);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		// Get attribute values
		minValue = attrs.getAttributeIntValue(
			VOICESMITH_NS, MIN_VALUE_ATTR, DEFAULT_MIN_VALUE);
		maxValue = attrs.getAttributeIntValue(
			VOICESMITH_NS, MAX_VALUE_ATTR, DEFAULT_MAX_VALUE);
		defaultValue = attrs.getAttributeIntValue(
			ANDROID_NS, DEFAULT_VALUE_ATTR, DEFAULT_VALUE);
		valueFormat = attrs.getAttributeValue(
			VOICESMITH_NS, VALUE_FORMAT_ATTR);
	}

	private String format(String value)
	{
		if (valueFormat == null)
		{
			return value;
		}
		else
		{
			return String.format(valueFormat, value);
		}
	}

	@Override
	public CharSequence getSummary()
	{
		return format(getPersistedString(Integer.toString(defaultValue)));
	}

	@Override
	protected View onCreateDialogView()
	{
		currentValue = Integer.parseInt(getPersistedString(
			Integer.toString(defaultValue)));

		final int margin = (int) getContext().getResources()
			.getDimension(R.dimen.LayoutMargin);

		LinearLayout layout = new LinearLayout(getContext());
		{
			layout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setPadding(margin, margin, margin, margin);
		}

		label = new TextView(getContext());
		{
			label.setText(format(Integer.toString(currentValue)));
			label.setGravity(Gravity.CENTER);
			label.setPadding(0, 0, 0, margin);
			layout.addView(label);
		}

		SeekBar seekbar = new SeekBar(getContext());
		{
			seekbar.setMax(maxValue - minValue);
			seekbar.setProgress(currentValue - minValue);
			seekbar.setOnSeekBarChangeListener(this);
			layout.addView(seekbar);
		}

		return layout;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult)
	{
		super.onDialogClosed(positiveResult);

		if (positiveResult)
		{
			String newValue = Integer.toString(currentValue);

			// TODO: Check the notification
			persistString(newValue);
			callChangeListener(newValue);
			notifyChanged();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		currentValue = progress + minValue;

		label.setText(format(Integer.toString(currentValue)));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}
}
