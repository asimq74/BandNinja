package com.asimq.artists.bandninja.viewmodels;

import java.util.List;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;
import com.asimq.artists.bandninja.room.ArtistTag;

public class SearchResultsViewModel extends AndroidViewModel {

	private final SearchResultsRepository searchResultsRepository;
	private final BandItemRepository bandItemRepository;
	private LiveData<List<Artist>> mLiveArtists;
	private LiveData<Artist> mLiveArtist;
	private LiveData<List<ArtistTag>> mLiveArtistTags;


	public SearchResultsViewModel(@NonNull Application application,
								  @NonNull SearchResultsRepository searchResultsRepository,
								  @NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.searchResultsRepository = searchResultsRepository;
		this.bandItemRepository = bandItemRepository;
	}


	public LiveData<Boolean> getArtistsRefreshingMutableLiveData() {
		return searchResultsRepository.getArtistsRefreshingMutableLiveData();
	}

	public LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String artistQuery) {
		mLiveArtists = searchResultsRepository.getSearchResultsByArtist(artistQuery);
		return mLiveArtists;
	}

	public LiveData<List<ArtistTag>> getArtistTags(@NonNull Artist artist) {
		mLiveArtistTags = bandItemRepository.getArtistTags(artist.getMbid());
		if (mLiveArtistTags == null) {
			bandItemRepository.saveMultipleArtistTags(ArtistTag.getTags(artist));
		}
		return mLiveArtistTags;
	}

	public LiveData<Artist> getArtistInfo(@NonNull String artistName) {
		mLiveArtist = searchResultsRepository.getArtistInfo(artistName);
		return mLiveArtist;
	}

	public LiveData<List<Artist>> getTopArtistsByTag(@NonNull String tag) {
		mLiveArtists = searchResultsRepository.getTopArtistsByTag(tag);
		return mLiveArtists;
	}

	public LiveData<List<Artist>> getTopArtists() {
		mLiveArtists = searchResultsRepository.getTopArtists();
		return mLiveArtists;
	}
}
