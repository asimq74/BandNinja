package com.asimq.artists.bandninja.room.dao;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.ArtistData;

@Dao
public interface ArtistDataDao {

	@Query("SELECT * from artists ORDER BY mbid ASC")
	@NonNull
	LiveData<List<ArtistData>> fetchAllArtists();

	@Query("SELECT * from artists where mbid = :mbid")
	@NonNull
	LiveData<ArtistData> fetchArtistById(@NonNull String mbid);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertArtist(@NonNull ArtistData artistData);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertMultipleArtists(@NonNull List<ArtistData> artistDataList);
}
