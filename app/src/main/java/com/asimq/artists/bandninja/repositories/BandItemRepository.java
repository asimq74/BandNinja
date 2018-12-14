package com.asimq.artists.bandninja.repositories;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.ArtistData;

public interface BandItemRepository {

	@NonNull
	LiveData<List<ArtistData>> getAllArtistData();

	@NonNull
	LiveData<ArtistData> getArtistData(@NonNull String mbid);

	void saveArtist(@NonNull ArtistData artistData);

	void saveMultipleArtists(@NonNull List<ArtistData> artistDataList);

}
