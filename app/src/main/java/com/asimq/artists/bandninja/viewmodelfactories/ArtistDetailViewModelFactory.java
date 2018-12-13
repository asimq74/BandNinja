package com.asimq.artists.bandninja.viewmodelfactories;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.viewmodels.ArtistDetailViewModel;

@Singleton
public class ArtistDetailViewModelFactory implements ViewModelProvider.Factory {

	private final Application application;
	private final BandItemRepository bandItemRepository;

	@Inject
	public ArtistDetailViewModelFactory(@NonNull Application application, @NonNull BandItemRepository bandItemRepository) {
		this.application = application;
		this.bandItemRepository = bandItemRepository;
	}

	@Override
	public ArtistDetailViewModel create(@NonNull Class modelClass) {
		return new ArtistDetailViewModel(application, bandItemRepository);
	}
}
