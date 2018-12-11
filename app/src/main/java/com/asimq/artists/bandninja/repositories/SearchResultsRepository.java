package com.asimq.artists.bandninja.repositories;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.data.Artist;

public interface SearchResultsRepository {

	LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String query);

	LiveData<Artist> getArtistInfo(@NonNull String artistName);
}
