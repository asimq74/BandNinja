package com.asimq.artists.bandninja.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.TrackData;

import java.util.List;

@Dao
public interface AlbumDataDao {


	@Query("SELECT * from albums where mbid = :mbid")
	@NonNull
	List<AlbumData> fetchAlbumDatas(@NonNull String mbid);

	@Query("SELECT * from albums where mbid = :mbid")
	@NonNull
	LiveData<List<AlbumData>> fetchLiveAlbumDatas(@NonNull String mbid);


	@Query("SELECT * from albums where artist = :artist")
	@NonNull
	LiveData<List<AlbumData>> fetchLiveAlbumDatasByArtist(@NonNull String artist);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertAlbumData(@NonNull AlbumData albumData);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertMultipleAlbumDatas(@NonNull List<AlbumData> albumDatas);
}
