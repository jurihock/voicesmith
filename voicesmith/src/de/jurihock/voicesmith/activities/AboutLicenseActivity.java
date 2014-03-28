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

package de.jurihock.voicesmith.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import de.jurihock.voicesmith.R;

public class AboutLicenseActivity extends Activity
{
	private static final String HTML_URL = "file:///android_asset/license.html";

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
		setContentView(viewHtml);
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
