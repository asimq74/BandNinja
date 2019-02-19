package com.asimq.artists.bandninja.asynctasks.artists;

import java.util.List;

import javax.inject.Inject;

import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;

public class FetchAllSavedArtistDataTask extends AsyncTask<String, Void, List<ArtistData>> {

	private final String TAG = this.getClass().getSimpleName();
	@Inject
	BandItemRepository bandItemRepository;
	private final MediatorLiveData<List<ArtistData>> mObservableArtistDatas;

	public FetchAllSavedArtistDataTask(Context applicationContext,
			MediatorLiveData<List<ArtistData>> mObservableArtistDatas) {
		final MyApplication application = (MyApplication) applicationContext;
		application.getApplicationComponent().inject(this);
		this.mObservableArtistDatas = mObservableArtistDatas;
	}

	@Override
	protected List<ArtistData> doInBackground(String... params) {
		List<ArtistData> artistDatas = bandItemRepository.getAllArtistDatas();
		return artistDatas;
	}

	@Override
	protected void onPostExecute(List<ArtistData> artistDatas) {
		super.onPostExecute(artistDatas);
		if (artistDatas == null || artistDatas.isEmpty()) {
			return;
		}
		Log.d(TAG, "artistDatas refreshed: " + artistDatas);
		mObservableArtistDatas.setValue(artistDatas);
	}
}
