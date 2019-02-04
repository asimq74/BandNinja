package com.asimq.artists.bandninja.asynctasks.artists;

import java.util.ArrayList;
import java.util.List;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;

import com.asimq.artists.bandninja.json.Artist;

public class EmptyArtistsProcessor extends AsyncTask<Void, Void, Void> {

	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<Artist>> musicItemObservable;

	public EmptyArtistsProcessor(MediatorLiveData<Boolean> isRefreshingObservable,
			MediatorLiveData<List<Artist>> musicItemObservable) {
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
		musicItemObservable.setValue(new ArrayList<>());
	}
}
