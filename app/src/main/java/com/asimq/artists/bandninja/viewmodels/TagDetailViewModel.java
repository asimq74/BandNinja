package com.asimq.artists.bandninja.viewmodels;

import java.util.List;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.repositories.TagModelRepository;

public class TagDetailViewModel extends AndroidViewModel {

	private final TagModelRepository tagModelRepository;
	private LiveData<List<Tag>> mLiveAllTags;

	public TagDetailViewModel(@NonNull Application application, @NonNull TagModelRepository tagModelRepository) {
		super(application);
		this.tagModelRepository = tagModelRepository;
	}

	public LiveData<List<Tag>> getTopTags() {
		mLiveAllTags = tagModelRepository.getTopTagsLiveData();
		return mLiveAllTags;
	}

}
