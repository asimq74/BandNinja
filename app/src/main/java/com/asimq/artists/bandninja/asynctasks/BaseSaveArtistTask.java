package com.asimq.artists.bandninja.asynctasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class BaseSaveArtistTask extends AsyncTask<ArtistData, Void, ArtistData> {

	final String TAG = this.getClass().getSimpleName();
	private final ArtistDataDao artistDataDao;

	public BaseSaveArtistTask(@NonNull ArtistDataDao artistDataDao) {
		this.artistDataDao = artistDataDao;
	}

	@Override
	protected ArtistData doInBackground(ArtistData... artistData) {
		final ArtistData data = artistData[0];
		if (null == data.getMbid()) {
			return new ArtistData();
		}
		artistDataDao.insertArtist(data);
		return data;
	}

	@Override
	protected void onPostExecute(ArtistData output) {
		super.onPostExecute(output);
		Log.d(TAG, "inserted: " + output);
	}
}
