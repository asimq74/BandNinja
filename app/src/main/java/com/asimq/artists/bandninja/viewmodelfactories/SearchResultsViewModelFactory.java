package com.asimq.artists.bandninja.viewmodelfactories;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;

@Singleton
public class SearchResultsViewModelFactory implements ViewModelProvider.Factory {

	private final Application application;
	private final BandItemRepository bandItemRepository;
	private final SearchResultsRepository searchResultsRepository;

	@Inject
	public SearchResultsViewModelFactory(@NonNull Application application,
			@NonNull SearchResultsRepository searchResultsRepository,
			@NonNull BandItemRepository bandItemRepository) {
		this.application = application;
		this.searchResultsRepository = searchResultsRepository;
		this.bandItemRepository = bandItemRepository;
	}

	@Override
	public SearchResultsViewModel create(@NonNull Class modelClass) {
		return new SearchResultsViewModel(application, searchResultsRepository, bandItemRepository);
	}
}
