package com.asimq.artists.bandninja.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.TagModelRepository;

public class TagDetailViewModel extends AndroidViewModel {

	private final TagModelRepository tagModelRepository;

	public TagDetailViewModel(@NonNull Application application, @NonNull TagModelRepository tagModelRepository) {
		super(application);
		this.tagModelRepository = tagModelRepository;
	}

}
