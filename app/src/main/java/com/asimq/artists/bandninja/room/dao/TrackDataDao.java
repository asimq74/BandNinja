package com.asimq.artists.bandninja.room.dao;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.TrackData;

@Dao
public interface TrackDataDao {

	@Query("SELECT * from tracks")
	@NonNull
	List<TrackData> fetchAllTrackDatas();

	@Query("SELECT * from tracks")
	@NonNull
	LiveData<List<TrackData>> fetchAllTrackLiveDatas();

	@Query("SELECT * from tracks where albumId = :mbid ORDER BY number ASC")
	@NonNull
	LiveData<List<TrackData>> fetchLiveTrackDatas(@NonNull String mbid);

	@Query("SELECT * from tracks where albumId = :mbid ORDER BY number ASC")
	@NonNull
	List<TrackData> fetchTrackDatas(@NonNull String mbid);

	@Query("SELECT * from tracks where artistName = :artistName ORDER BY number ASC")
	@NonNull
	List<TrackData> fetchTrackDatasByArtist(String artistName);

	@Query("SELECT * from tracks where artistName = :artistName and albumName = :albumName ORDER BY number ASC")
	@NonNull
	List<TrackData> getTrackLiveDatasByArtistAndAlbum(@NonNull String artistName, @NonNull String albumName);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertMultipleTrackDatas(@NonNull List<TrackData> trackDatas);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertTrackData(@NonNull TrackData trackData);
}
