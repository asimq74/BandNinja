package com.asimq.artists.bandninja.asynctasks.albums;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;

public class SaveAlbumDataTask extends AsyncTask<AlbumData, Void, AlbumData> {

	private final BandItemRepository repository;

	public SaveAlbumDataTask(@NonNull BandItemRepository repository) {
		this.repository = repository;
	}

	@Override
	protected AlbumData doInBackground(AlbumData... params) {
		final AlbumData data = params[0];
		repository.insertAlbumWithTracks(data);
		return data;
	}
}
