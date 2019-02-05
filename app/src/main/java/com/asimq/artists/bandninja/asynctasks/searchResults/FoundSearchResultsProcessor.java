package com.asimq.artists.bandninja.asynctasks.searchResults;

import java.util.Map;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;

import com.asimq.artists.bandninja.json.Artist;

public class FoundSearchResultsProcessor extends AsyncTask<Map<String, Artist>, Void, Map<String, Artist>> {

	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<Map<String, Artist>> musicItemObservable;

	public FoundSearchResultsProcessor(MediatorLiveData<Boolean> isRefreshingObservable,
			MediatorLiveData<Map<String, Artist>> musicItemObservable) {
		this.isRefreshingObservable = isRefreshingObservable;
		this.musicItemObservable = musicItemObservable;
	}

	@Override
	protected Map<String, Artist> doInBackground(Map<String, Artist>... maps) {
		return maps[0];
	}

	@Override
	protected void onPostExecute(Map<String, Artist> artistMap) {
		super.onPostExecute(artistMap);
		isRefreshingObservable.setValue(false);
		musicItemObservable.setValue(artistMap);
	}
}
