package com.asimq.artists.bandninja.asynctasks;

import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.TrackData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class FetchAllSavedAlbumDataTask extends AsyncTask<String, Void, List<AlbumData>> {

	@Inject
	BandItemRepository bandItemRepository;
	private final MediatorLiveData<List<AlbumData>> mObservableAlbumDatas;

	public FetchAllSavedAlbumDataTask(Context applicationContext,
									  MediatorLiveData<List<AlbumData>> mObservableAlbumDatas) {
		final MyApplication application = (MyApplication) applicationContext;
		application.getApplicationComponent().inject(this);
		this.mObservableAlbumDatas = mObservableAlbumDatas;
	}

	@Override
	protected List<AlbumData> doInBackground(String... params) {
		List<AlbumData> albumDatas = bandItemRepository.getAllAlbumDatas();
		return albumDatas;
	}

	@Override
	protected void onPostExecute(List<AlbumData> albumDatas) {
		super.onPostExecute(albumDatas);
		if (albumDatas == null || albumDatas.isEmpty()) {
			return;
		}
		Log.d(TAG, "albumDatas refreshed: " + albumDatas);
		mObservableAlbumDatas.setValue(albumDatas);
	}

	private final String TAG = this.getClass().getSimpleName();
}
