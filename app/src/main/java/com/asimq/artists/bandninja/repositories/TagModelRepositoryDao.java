package com.asimq.artists.bandninja.repositories;

import java.util.concurrent.Executors;

import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.tags.TagsFromStorageTask;
import com.asimq.artists.bandninja.ui.CustomMultiSelectListPreference;

public class TagModelRepositoryDao implements TagModelRepository {

	public void searchForTags(@NonNull BandItemRepository bandItemRepository, @NonNull CustomMultiSelectListPreference customMultiSelectListPreference) {
		new TagsFromStorageTask(bandItemRepository, customMultiSelectListPreference).executeOnExecutor(Executors.newSingleThreadExecutor());
	}

}
