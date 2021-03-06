package com.asimq.artists.bandninja.asynctasks.artists;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class SaveArtistDataTask extends AsyncTask<ArtistData, Void, ArtistData> {

	private final BandItemRepository repository;

	public SaveArtistDataTask(@NonNull BandItemRepository repository) {
		this.repository = repository;
	}

	@Override
	protected ArtistData doInBackground(ArtistData... params) {
		final ArtistData data = params[0];
		if (null == data.getMbid() && null == data.getName()) {
			return new ArtistData();
		}
		repository.saveArtist(data);
		return data;
	}

}
