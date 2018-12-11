package com.asimq.artists.bandninja.viewmodels;

import java.util.List;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.data.Artist;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;

public class SearchResultsViewModel extends AndroidViewModel {

	private final SearchResultsRepository searchResultsRepository;
	private LiveData<List<Artist>> mLiveArtists;
	private LiveData<Artist> mLiveArtist;

	public SearchResultsViewModel(@NonNull Application application, @NonNull SearchResultsRepository repository) {
		super(application);
		this.searchResultsRepository = repository;
	}

	public LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String artistQuery) {
		mLiveArtists = searchResultsRepository.getSearchResultsByArtist(artistQuery);
		return mLiveArtists;
	}

	public LiveData<Artist> getArtistInfo(@NonNull String artistName) {
		mLiveArtist = searchResultsRepository.getArtistInfo(artistName);
		return mLiveArtist;
	}
}
