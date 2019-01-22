package com.asimq.artists.bandninja.asynctasks;

import java.util.List;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;

public class SaveTracksTask extends AsyncTask<List<TrackData>, Void, List<TrackData>> {

	final String TAG = this.getClass().getSimpleName();
	private final TrackDataDao trackDataDao;

	public SaveTracksTask(@NonNull TrackDataDao trackDataDao) {
		this.trackDataDao = trackDataDao;
	}

	@Override
	protected List<TrackData> doInBackground(List<TrackData>... params) {
		final List<TrackData> datas = params[0];
		trackDataDao.insertMultipleTrackDatas(datas);
		return datas;
	}

	@Override
	protected void onPostExecute(List<TrackData> output) {
		super.onPostExecute(output);
		Log.d(TAG, "inserted tracks: " + output);
	}
}
