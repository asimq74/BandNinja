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
	LiveData<List<ArtistData>> fetchAllArtistLiveDatas();

	@Query("SELECT * from artists ORDER BY mbid ASC")
	@NonNull
	List<ArtistData> fetchAllArtistDatas();

	@Query("SELECT * from artists where mbid = :mbid")
	@NonNull
	LiveData<ArtistData> fetchLiveArtistDataById(@NonNull String mbid);

	@Query("SELECT * from artists where name = :name")
	@NonNull
	LiveData<ArtistData> fetchLiveArtistDataByName(@NonNull String name);

	@Query("SELECT * from artists where name = :name")
	@NonNull
	ArtistData fetchArtistDataByName(@NonNull String name);

	@Query("SELECT * from artists where name IN (:names)")
	@NonNull
	List<ArtistData> fetchArtistDataByNames(@NonNull List<String> names);

	@Query("SELECT * from artists where mbid = :mbid")
	@NonNull
	ArtistData fetchArtistDataById(@NonNull String mbid);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertArtist(@NonNull ArtistData artistData);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertMultipleArtists(@NonNull List<ArtistData> artistDataList);
}
