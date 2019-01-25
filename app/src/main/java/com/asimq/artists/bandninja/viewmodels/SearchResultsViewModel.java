package com.asimq.artists.bandninja.viewmodels;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;
import com.asimq.artists.bandninja.room.ArtistData;

public class SearchResultsViewModel extends AndroidViewModel {

	private LiveData<Artist> mLiveArtist;
	private LiveData<List<Artist>> mLiveArtists;
	private final SearchResultsRepository searchResultsRepository;
	private final BandItemRepository bandItemRepository;

	// MediatorLiveData can observe other LiveData objects and react on their emissions.
	private final MediatorLiveData<List<ArtistData>> mObservableArtistDatas;

	public SearchResultsViewModel(@NonNull Application application,
			@NonNull SearchResultsRepository searchResultsRepository,
			@NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.searchResultsRepository = searchResultsRepository;
		this.bandItemRepository = bandItemRepository;
		mObservableArtistDatas = new MediatorLiveData<>();
		// set by default null, until we get data from the database.
		mObservableArtistDatas.setValue(new ArrayList<>());

		LiveData<List<ArtistData>> artistDatas = bandItemRepository.getAllArtistData();
		mObservableArtistDatas.addSource(artistDatas, mObservableArtistDatas::setValue);
	}

	public LiveData<Artist> getArtistInfo(@NonNull String artistName) {
		mLiveArtist = searchResultsRepository.getArtistInfo(artistName);
		return mLiveArtist;
	}

	public LiveData<List<ArtistData>> getArtistDatas() {
			return mObservableArtistDatas;
	}

	public LiveData<Boolean> getArtistsRefreshingMutableLiveData() {
		return searchResultsRepository.getArtistsRefreshingMutableLiveData();
	}

	public LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String artistQuery) {
		mLiveArtists = searchResultsRepository.getSearchResultsByArtist(artistQuery);
		return mLiveArtists;
	}

	public LiveData<List<Artist>> getTopArtists() {
		mLiveArtists = searchResultsRepository.getTopArtists();
		return mLiveArtists;
	}

	public LiveData<List<Artist>> getTopArtistsByTag(@NonNull String tag) {
		mLiveArtists = searchResultsRepository.getTopArtistsByTag(tag);
		return mLiveArtists;
	}
}
