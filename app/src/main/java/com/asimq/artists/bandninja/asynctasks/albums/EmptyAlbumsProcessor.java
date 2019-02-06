package com.asimq.artists.bandninja.asynctasks.albums;

import java.util.ArrayList;
import java.util.List;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;

import com.asimq.artists.bandninja.json.AlbumInfo;

public class EmptyAlbumsProcessor extends AsyncTask<Void, Void, Void> {
	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<AlbumInfo>> musicItemObservable;

	public EmptyAlbumsProcessor(MediatorLiveData<Boolean> isRefreshingObservable, MediatorLiveData<List<AlbumInfo>> musicItemObservable) {
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