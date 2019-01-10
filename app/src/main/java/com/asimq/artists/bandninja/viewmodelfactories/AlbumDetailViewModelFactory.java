package com.asimq.artists.bandninja.viewmodelfactories;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;

@Singleton
public class AlbumDetailViewModelFactory implements ViewModelProvider.Factory {

	private final Application application;
	private final AlbumInfoRepository albumInfoRepository;

	@Inject
	public AlbumDetailViewModelFactory(@NonNull Application application,
										 @NonNull AlbumInfoRepository albumInfoRepository) {
		this.application = application;
		this.albumInfoRepository = albumInfoRepository;
	}

	@Override
	public AlbumDetailViewModel create(@NonNull Class modelClass) {
		return new AlbumDetailViewModel(application, albumInfoRepository);
	}
}
