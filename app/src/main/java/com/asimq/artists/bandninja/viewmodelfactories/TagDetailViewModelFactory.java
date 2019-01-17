package com.asimq.artists.bandninja.viewmodelfactories;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.repositories.TagModelRepository;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.TagDetailViewModel;

@Singleton
public class TagDetailViewModelFactory implements ViewModelProvider.Factory {

	private final Application application;
	private final TagModelRepository tagModelRepository;

	@Inject
	public TagDetailViewModelFactory(@NonNull Application application,
										 @NonNull TagModelRepository tagModelRepository) {
		this.application = application;
		this.tagModelRepository = tagModelRepository;
	}

	@Override
	public TagDetailViewModel create(@NonNull Class modelClass) {
		return new TagDetailViewModel(application, tagModelRepository);
	}
}
