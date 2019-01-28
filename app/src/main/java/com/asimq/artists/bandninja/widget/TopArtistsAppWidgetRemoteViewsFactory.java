package com.asimq.artists.bandninja.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.asimq.artists.bandninja.MainActivity;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class TopArtistsAppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

	@Inject
	ArtistDataDao artistDataDao;
	private Map<String, ExtrasTuple> itemExtrasMap = new HashMap<>();
	private List<String> items = new ArrayList<>();
	private final Context mContext;


	public static class ExtrasTuple {
		private String currentTag = "";
		private String currentArtist = "";
		private String currentAlbum = "";
		private String currentMethod = "";

		public ExtrasTuple(String currentTag, String currentArtist, String currentAlbum, String currentMethod) {
			this.currentTag = currentTag;
			this.currentArtist = currentArtist;
			this.currentAlbum = currentAlbum;
			this.currentMethod = currentMethod;
		}

		public String getCurrentAlbum() {
			return currentAlbum;
		}

		public String getCurrentTag() {
			return currentTag;
		}

		public String getCurrentArtist() {
			return currentArtist;
		}

		public String getCurrentMethod() {
			return currentMethod;
		}
	}

	public TopArtistsAppWidgetRemoteViewsFactory(Context mContext, Intent intent) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
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
				items == null || position >= items.size()) {
			return null;
		}
		String menuItem = items.get(position);
		RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.band_ninja_app_widget_list_item);
		remoteViews.setTextViewText(R.id.widgetItemLabel, menuItem);
		Bundle extras = new Bundle();
		final ExtrasTuple extrasTuple = itemExtrasMap.get(menuItem);
		extras.putString(MainActivity.EXTRA_CURRENT_ALBUM, extrasTuple.getCurrentAlbum());
		extras.putString(MainActivity.EXTRA_CURRENT_ARTIST, extrasTuple.getCurrentArtist());
		extras.putString(MainActivity.EXTRA_CURRENT_METHOD, extrasTuple.getCurrentMethod());
		extras.putString(MainActivity.EXTRA_CURRENT_TAG, extrasTuple.getCurrentTag());
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);
		remoteViews.setOnClickFillInIntent(R.id.widgetItemContainer, fillInIntent);
		return remoteViews;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@NonNull
	private void createItem(String key, List<String> items, String currentMethod, String artist, String tag) {
		items.add(key);
		ExtrasTuple tuple = new ExtrasTuple(tag, artist, "", currentMethod);
		itemExtrasMap.put(key, tuple);
	}

	private List<String> getWidgetListItems() {
		Log.d(TAG, "getWidgetListItems called: ");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		Set<String> favoriteGenres = prefs.getStringSet(mContext.getString(R.string.favorite_genre_key), new HashSet<>());
		List<String> items = new ArrayList<>();
		createItem(mContext.getString(R.string.searchForAnArtist), items, MainActivity.ON_SEARCH_FOR_AN_ARTIST, "", "");
		createItem(mContext.getString(R.string.topArtists), items, MainActivity.ON_DISPLAYING_TOP_ARTISTS, "", "");
		for (String genre : favoriteGenres) {
			if (!genre.isEmpty()) {
				String capitalGenre = genre.substring(0, 1).toUpperCase() + genre.substring(1);
				final String byGenreKey = String.format("Top Artists in %s", capitalGenre);
				createItem(byGenreKey, items, MainActivity.ON_DISPLAYING_ARTISTS_BY_TAG, "", genre);
			}
		}
		return items;
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

	@Override
	public void onDataSetChanged() {
		final long identityToken = Binder.clearCallingIdentity();
		items.clear();
		items = getWidgetListItems();
		Log.d(TAG, "onDataSetChanged->items: " + items);
		Binder.restoreCallingIdentity(identityToken);
	}

	final String TAG = this.getClass().getSimpleName();

	@Override
	public void onDestroy() {
		if (items != null) {
			items = null;
		}
	}
}
