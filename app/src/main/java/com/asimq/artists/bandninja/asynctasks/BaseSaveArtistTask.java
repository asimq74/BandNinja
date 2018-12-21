package com.asimq.artists.bandninja.asynctasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.NavigationListener;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class BaseSaveArtistTask extends AsyncTask<ArtistData, Void, String> {

	private final ArtistDataDao artistDataDao;

	public BaseSaveArtistTask(@NonNull ArtistDataDao artistDataDao) {
		this.artistDataDao = artistDataDao;
	}

	@Override
	protected String doInBackground(ArtistData... artistData) {
		final ArtistData data = artistData[0];
		if (null == data.getMbid()) {
			return "";
		}
		artistDataDao.insertArtist(data);
		return data.getMbid();
	}

	@Override
	protected void onPostExecute(String output) {
		super.onPostExecute(output);
	}
}
