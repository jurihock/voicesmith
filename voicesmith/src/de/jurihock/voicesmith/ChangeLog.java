package de.jurihock.voicesmith;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ChangeLog extends AlertDialog.Builder
{
    private final Context context;

    public ChangeLog(Context context)
    {
        super(context);

        this.context = context;

        setTitle(new Utils(context).getVersionString(R.string.ChangeLogTitle));
        setMessage(R.string.ChangeLogMessage);

        setNegativeButton(android.R.string.ok,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                    }
                }
        );
    }

    @Override
    public AlertDialog show()
    {
        Preferences preferences = new Preferences(context);

        if (!preferences.isChangeLogShowed())
        {
            preferences.setChangeLogShowed(true);
            return super.show();
        }

        return null;
    }
}
