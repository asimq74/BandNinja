package com.asimq.artists.bandninja.repositories;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TopTagsWrapper;

public interface TagModelRepository {

	LiveData<List<Tag>> getTopTagsLiveData();

	List<Tag> getTopTags();
}
