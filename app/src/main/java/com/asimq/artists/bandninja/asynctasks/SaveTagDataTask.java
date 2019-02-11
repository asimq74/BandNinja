package com.asimq.artists.bandninja.asynctasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;

import java.util.List;

public class SaveTagDataTask extends AsyncTask<List<TagData>, Void, List<TagData>> {

	private final BandItemRepository repository;

	public SaveTagDataTask(@NonNull BandItemRepository repository) {
		this.repository = repository;
	}

	@Override
	protected List<TagData> doInBackground(List<TagData>... params) {
		final List<TagData> data = params[0];
		repository.saveMultipleTagDatas(data);
		return data;
	}

}
