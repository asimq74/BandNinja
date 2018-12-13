package com.asimq.artists.bandninja.asynctasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.MainNavigationActivity;
import com.asimq.artists.bandninja.NavigationListener;
import com.asimq.artists.bandninja.ResultActivity;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class SaveArtistTask extends AsyncTask<ArtistData, Void, String> {

	private final ArtistDataDao artistDataDao;
	private final NavigationListener listener;

	public SaveArtistTask(@NonNull ArtistDataDao artistDataDao, @NonNull NavigationListener listener) {
		this.artistDataDao = artistDataDao;
		this.listener = listener;
	}

	@Override
	protected String doInBackground(ArtistData... artistData) {
		final ArtistData data = artistData[0];
		artistDataDao.insertArtist(data);
		return data.getMbid();
	}

	@Override
	protected void onPostExecute(String output) {
		super.onPostExecute(output);
		listener.navigateTo(output);
	}
}
