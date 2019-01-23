package com.asimq.artists.bandninja.widget;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class TopArtistsAppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

	private List<String> artistDescriptions = new ArrayList<>();
	private final Context mContext;

	public TopArtistsAppWidgetRemoteViewsFactory(Context mContext, Intent intent) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return artistDescriptions == null ? 0 : artistDescriptions.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		if (position == AdapterView.INVALID_POSITION ||
				artistDescriptions == null || position >= artistDescriptions.size()) {
			return null;
		}
		RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.band_ninja_app_widget_list_item);
		remoteViews.setTextViewText(R.id.widgetItemLabel, artistDescriptions.get(position));
		return remoteViews;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
		final MyApplication application = (MyApplication) mContext.getApplicationContext();
		(application).getApplicationComponent().inject(this);
	}

	@Inject
	ArtistDataDao artistDataDao;

	@Override
	public void onDataSetChanged() {
		final long identityToken = Binder.clearCallingIdentity();
		List<ArtistData> artistDatas  = artistDataDao.fetchAllArtistDatas();
		artistDescriptions.clear();
		for (ArtistData data : artistDatas) {
			artistDescriptions.add(data.getName());
		}
		Binder.restoreCallingIdentity(identityToken);
	}

	@Override
	public void onDestroy() {
		if (artistDescriptions != null) {
			artistDescriptions = null;
		}
	}
}
