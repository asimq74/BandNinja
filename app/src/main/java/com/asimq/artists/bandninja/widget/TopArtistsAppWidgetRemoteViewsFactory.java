package com.asimq.artists.bandninja.widget;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.support.v4.app.TaskStackBuilder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.asimq.artists.bandninja.DetailsActivity;
import com.asimq.artists.bandninja.MainActivity;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class TopArtistsAppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

	private List<ArtistData> artistDatas = new ArrayList<>();
	private final Context mContext;

	public TopArtistsAppWidgetRemoteViewsFactory(Context mContext, Intent intent) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return artistDatas == null ? 0 : artistDatas.size();
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
				artistDatas == null || position >= artistDatas.size()) {
			return null;
		}
		RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.band_ninja_app_widget_list_item);
		ArtistData artistData = artistDatas.get(position);
		remoteViews.setTextViewText(R.id.widgetItemLabel, artistData.getName());
//		final Intent widgetListItemIntent = new Intent(mContext, DetailsActivity.class);
//		widgetListItemIntent.putExtra(DetailsActivity.EXTRA_IMAGE, artistData.getImage());
//		widgetListItemIntent.putExtra(DetailsActivity.EXTRA_TITLE, artistData.getName());
//		// template to handle the click listener for each item
//		PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(mContext)
//				.addNextIntentWithParentStack(widgetListItemIntent)
//				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//		remoteViews.setOnClickPendingIntent(R.id.widgetItemLabel, clickPendingIntentTemplate);
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
		artistDatas.clear();
		artistDatas = artistDataDao.fetchAllArtistDatas();
		Binder.restoreCallingIdentity(identityToken);
	}

	@Override
	public void onDestroy() {
		if (artistDatas != null) {
			artistDatas = null;
		}
	}
}
