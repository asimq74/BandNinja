package com.asimq.artists.bandninja.repositories;

import java.util.List;
import java.util.Map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistsWrapper;

import retrofit2.Call;

public interface SearchResultsRepository {

	LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String query);

	LiveData<Artist> getArtistInfo(@NonNull String artistName);

	Artist getArtist(@NonNull String artistName);

	void searchForArtist(@NonNull Context context,
						 @NonNull MediatorLiveData<List<Artist>> artistsLiveDataObservable,
						 @NonNull MediatorLiveData<Boolean> isRefreshingObservable,
						 @NonNull String artistName);

	void searchForArtistByTag(@NonNull Context context,
							  @NonNull MediatorLiveData<List<Artist>> artistsLiveDataObservable,
							  @NonNull MediatorLiveData<Boolean> isRefreshingObservable,
							  @NonNull String tagName);

	void searchForTopArtists(@NonNull Context context,
							  @NonNull MediatorLiveData<List<Artist>> artistsLiveDataObservable,
							  @NonNull MediatorLiveData<Boolean> isRefreshingObservable);

	Map<String, Artist> getSearchResultsByArtistName(@NonNull String query);

	LiveData<Boolean> getArtistsRefreshingMutableLiveData();

	LiveData<List<Artist>> getTopArtistsByTag(@NonNull String tag);

	LiveData<List<Artist>> getTopArtists();
}
