package com.asimq.artists.bandninja.asynctasks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.TrackData;

public class FetchTrackDataTask extends AsyncTask<String, Void, List<TrackData>> {

	@Inject
	BandItemRepository bandItemRepository;
	private final MediatorLiveData<List<TrackData>> tracksByAlbumLiveDataObservable;

	public FetchTrackDataTask(Context applicationContext, MediatorLiveData<List<TrackData>> tracksByAlbumLiveDataObservable) {
		final MyApplication application = (MyApplication) applicationContext;
		application.getApplicationComponent().inject(this);
		this.tracksByAlbumLiveDataObservable = tracksByAlbumLiveDataObservable;
	}

	@Override
	protected List<TrackData> doInBackground(String... params) {
		List<TrackData> trackDatas = bandItemRepository.getTrackLiveDatasByArtistAndAlbum(params[0], params[1]);
		return trackDatas;
	}

	@Override
	protected void onPostExecute(List<TrackData> trackDatas) {
		super.onPostExecute(trackDatas);
		Map<String, Track> trackMap = new LinkedHashMap<>();
		if (trackDatas == null || trackDatas.isEmpty()) {
			return;
		}
		Log.d(TAG, "tracksByAlbumMap refreshed: " + trackDatas);
		tracksByAlbumLiveDataObservable.setValue(trackDatas);
	}

	private final String TAG = this.getClass().getSimpleName();
}
