package com.asimq.artists.bandninja.viewmodels;

import java.util.Map;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;

public class SearchResultsViewModel extends AndroidViewModel {

	private MediatorLiveData<Boolean> isRefreshingObservable = new MediatorLiveData<>();
	private MediatorLiveData<Map<String, Artist>> searchResultsByArtistObservable = new MediatorLiveData<>();
	private final SearchResultsRepository searchResultsRepository;

	public SearchResultsViewModel(@NonNull Application application,
			@NonNull SearchResultsRepository searchResultsRepository) {
		super(application);
		this.searchResultsRepository = searchResultsRepository;
	}

	public MediatorLiveData<Boolean> getIsRefreshingObservable() {
		return isRefreshingObservable;
	}

	public MediatorLiveData<Map<String, Artist>> getSearchResultsByArtistObservable() {
		return searchResultsByArtistObservable;
	}

	public void searchForArtistByTag(@NonNull String tagName) {
		searchResultsRepository.searchForArtistByTag(tagName, isRefreshingObservable, searchResultsByArtistObservable);
	}

	public void searchForTopArtists() {
		searchResultsRepository.searchForTopArtists(isRefreshingObservable, searchResultsByArtistObservable);
	}

	public void searchResultsByArtistName(@NonNull String query) {
		searchResultsRepository.searchResultsByArtistName(query, isRefreshingObservable, searchResultsByArtistObservable);
	}
}
