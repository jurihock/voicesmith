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

import de.jurihock.voicesmith.ChangeLog;
import greendroid.app.GDListActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.SeparatorItem;
import greendroid.widget.item.TextItem;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import de.jurihock.voicesmith.R;
import de.jurihock.voicesmith.Utils;
import de.jurihock.voicesmith.services.AafService;
import de.jurihock.voicesmith.services.DafxService;

public final class HomeActivity extends GDListActivity
{
	public HomeActivity()
	{
		super(ActionBar.Type.Normal);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Change the action bar icon
		ImageButton actionBarButton = (ImageButton) getActionBar()
			.findViewById(R.id.gd_action_bar_home_item);
		actionBarButton.setImageResource(R.drawable.action_bar_icon);
		actionBarButton.setClickable(false);

		// Init menu items
		ItemAdapter items = new ItemAdapter(this);
		{
			items.add(newMenuItem(
				R.string.DafxActivity,
				DafxActivity.class));

			items.add(newMenuItem(
				R.string.AafActivity,
				AafActivity.class));

			items.add(newMenuItem(
				R.string.PreferenceActivity,
				PreferenceActivity.class));

            items.add(new SeparatorItem("Help"));

            items.add(newMenuItem(
                    R.string.SupportActivity,
                    SupportActivity.class));

            items.add(newMenuItem(
                    R.string.ContributionActivity,
                    ContributionActivity.class));

			items.add(newMenuItem(
				R.string.AboutActivity,
				AboutActivity.class));
		}
		setListAdapter(items);

        new ChangeLog(this).show();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Context serviceContext = this.getApplicationContext();
		Class<?> serviceClass;
		Intent serviceIntent;

		// Stop the DafxService if it's running
		serviceClass = DafxService.class;
		serviceIntent = new Intent(serviceContext, serviceClass);
		if (new Utils(serviceContext).isServiceRunning(serviceClass))
		{
			new Utils(this).log("Stopping DafxService.");
			stopService(serviceIntent);
		}

		// Stop the AafService if it's running
		serviceClass = AafService.class;
		serviceIntent = new Intent(serviceContext, serviceClass);
		if (new Utils(serviceContext).isServiceRunning(serviceClass))
		{
			new Utils(this).log("Stopping AafService.");
			stopService(serviceIntent);
		}
	}

	private Item newMenuItem(int activityName, Class<?> activityClass)
	{
		TextItem item = new TextItem(getString(activityName));
		{
			item.setTag(activityClass);
		}

		return item;
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id)
	{
		Item item = (Item) listView.getAdapter().getItem(position);
		Intent intent = new Intent(this, (Class<?>) item.getTag());
		startActivity(intent);
	}
}
