package com.asimq.artists.bandninja.asynctasks.searchResults;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;

import com.asimq.artists.bandninja.json.Artist;

public class EmptySearchResultsProcessor extends AsyncTask<Void, Void, Void> {

	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<Map<String, Artist>> musicItemObservable;

	public EmptySearchResultsProcessor(MediatorLiveData<Boolean> isRefreshingObservable,
			MediatorLiveData<Map<String, Artist>> musicItemObservable) {
		this.isRefreshingObservable = isRefreshingObservable;
		this.musicItemObservable = musicItemObservable;
	}

	@Override
	protected Void doInBackground(Void... voids) {
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		isRefreshingObservable.setValue(false);
		musicItemObservable.setValue(new LinkedHashMap<>());
	}
}
