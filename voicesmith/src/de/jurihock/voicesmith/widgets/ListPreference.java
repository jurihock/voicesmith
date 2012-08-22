package de.jurihock.voicesmith.widgets;

import android.content.Context;
import android.util.AttributeSet;

public class ListPreference extends android.preference.ListPreference
{
	public ListPreference(Context context)
	{
		super(context);
	}

	public ListPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	public CharSequence getSummary()
	{
		return getEntry();
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult)
	{
		if(positiveResult)
		{
			// TODO: Check the notification
			notifyChanged();
		}
		
		super.onDialogClosed(positiveResult);
	}
}
