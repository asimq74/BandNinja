package com.asimq.artists.bandninja.asynctasks.albums;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;

public class AlbumDataByNameAndIdFromStorageTask extends AsyncTask<String, Void, AlbumData> {

	private final BandItemRepository bandItemRepository;
	private final MediatorLiveData<AlbumData> observableAlbumData;

	public AlbumDataByNameAndIdFromStorageTask(@NonNull BandItemRepository bandItemRepository, MediatorLiveData<AlbumData> observableAlbumData) {
		this.bandItemRepository = bandItemRepository;
		this.observableAlbumData = observableAlbumData;
	}

	@Override
	protected AlbumData doInBackground(String... params) {
		String album = params[0];
		String id = params[1];
		return bandItemRepository.getAlbumDataByNameAndId(album, id);
	}

	@Override
	protected void onPostExecute(AlbumData albumData) {
		super.onPostExecute(albumData);
		observableAlbumData.setValue(albumData);
	}

}
