package com.asimq.artists.bandninja.repositories;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistsWrapper;

import retrofit2.Call;

public interface SearchResultsRepository {

	LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String query);

	LiveData<Artist> getArtistInfo(@NonNull String artistName);

	LiveData<Boolean> getArtistsRefreshingMutableLiveData();

	LiveData<List<Artist>> getTopArtistsByTag(@NonNull String tag);

	LiveData<List<Artist>> getTopArtists();
}
