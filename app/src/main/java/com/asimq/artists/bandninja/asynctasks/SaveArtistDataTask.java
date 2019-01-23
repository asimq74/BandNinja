package com.asimq.artists.bandninja.asynctasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class SaveArtistDataTask extends AsyncTask<ArtistData, Void, ArtistData> {

	final String TAG = this.getClass().getSimpleName();
	private final BandItemRepository repository;

	public SaveArtistDataTask(@NonNull BandItemRepository repository) {
		this.repository = repository;
	}

	@Override
	protected ArtistData doInBackground(ArtistData... params) {
		final ArtistData data = params[0];
		if (null == data.getMbid()) {
			return new ArtistData();
		}
		repository.saveArtist(data);
		return data;
	}

	@Override
	protected void onPostExecute(ArtistData output) {
		super.onPostExecute(output);
		Log.d(TAG, "inserted artist: " + output);
	}
}
