package de.jurihock.voicesmith.activities;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import de.jurihock.voicesmith.R;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;

public class SupportActivity extends GDActivity
{
    private static final String HTML_URL = "file:///android_asset/support.html";

    private WebView viewHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final int margin = (int) getResources()
                .getDimension(R.dimen.LayoutMargin);

        viewHtml = new WebView(this);
        viewHtml.setPadding(margin, margin, margin, margin);
        viewHtml.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        setActionBarContentView(viewHtml);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(viewHtml.getUrl() == null || !viewHtml.getUrl().equals(HTML_URL))
        {
            viewHtml.loadUrl(HTML_URL);
        }
    }
}
