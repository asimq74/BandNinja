package com.asimq.artists.bandninja.asynctasks.albums;

import java.util.List;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;

import com.asimq.artists.bandninja.json.AlbumInfo;

public class FoundAlbumsProcessor extends AsyncTask<List<AlbumInfo>, Void, List<AlbumInfo>> {

	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<AlbumInfo>> musicItemObservable;

	public FoundAlbumsProcessor(MediatorLiveData<Boolean> isRefreshingObservable, MediatorLiveData<List<AlbumInfo>> musicItemObservable) {
		this.isRefreshingObservable = isRefreshingObservable;
		this.musicItemObservable = musicItemObservable;
	}

	@Override
	protected List<AlbumInfo> doInBackground(List<AlbumInfo>... params) {
		return params[0];
	}

	@Override
	protected void onPostExecute(List<AlbumInfo> albumInfos) {
		super.onPostExecute(albumInfos);
		musicItemObservable.setValue(albumInfos);
		isRefreshingObservable.setValue(false);
	}
}