package com.asimq.artists.bandninja.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by Asim Qureshi
 */

public class BandAppWidgetRemoteViewsService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		Log.d(getClass().getSimpleName(), "onGetViewFactory: " + "Service called");
		return new TopArtistsAppWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}
