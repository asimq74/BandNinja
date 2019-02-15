package com.asimq.artists.bandninja.asynctasks.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.TagData;
import com.asimq.artists.bandninja.ui.CustomMultiSelectListPreference;

public class TagsFromStorageTask extends AsyncTask<Void, Void, List<TagData>> {

	final String TAG = this.getClass().getSimpleName();
	private final BandItemRepository bandItemRepository;
	private final CustomMultiSelectListPreference customMultiSelectListPreference;

	public TagsFromStorageTask(@NonNull BandItemRepository bandItemRepository,
			@NonNull CustomMultiSelectListPreference customMultiSelectListPreference) {
		this.bandItemRepository = bandItemRepository;
		this.customMultiSelectListPreference = customMultiSelectListPreference;
	}

	@Override
	protected List<TagData> doInBackground(Void... params) {
		return bandItemRepository.getTopTagDatas();
	}

	@Override
	protected void onPostExecute(List<TagData> tagDatas) {
		super.onPostExecute(tagDatas);
		List<Tag> tags = new ArrayList<>();
		if (!tagDatas.isEmpty()) {
			for (TagData tagData : tagDatas) {
				tags.add(new Tag(tagData.getName()));
			}
			customMultiSelectListPreference.setEntries(tags);
			return;
		}
		Log.d(TAG, "attempting to download the tags from the Server");
		new TagsFromServerTask(bandItemRepository, customMultiSelectListPreference)
				.executeOnExecutor(Executors.newSingleThreadExecutor());
	}
}
