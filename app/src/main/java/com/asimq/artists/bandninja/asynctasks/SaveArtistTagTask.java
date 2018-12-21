package com.asimq.artists.bandninja.asynctasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.ArtistTag;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;

public class SaveArtistTagTask extends AsyncTask<Artist, Void, String> {

	private final ArtistTagDao artistTagDao;

	public SaveArtistTagTask(ArtistTagDao artistTagDao) {
		this.artistTagDao = artistTagDao;
	}

	@Override
	protected String doInBackground(Artist... artist) {
		final Artist data = artist[0];
		if (null == data.getMbid()) {
			return "";
		}
		artistTagDao.insertMultipleArtistTags(ArtistTag.getTags(data));
		return data.getMbid();
	}

	@Override
	protected void onPostExecute(String output) {
		super.onPostExecute(output);
	}
}
