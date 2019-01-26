package com.asimq.artists.bandninja.repositories;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;
import com.asimq.artists.bandninja.room.TrackData;

public interface BandItemRepository {

	@NonNull
	LiveData<List<TrackData>> getAllTrackLiveDatas();

	@NonNull
	LiveData<List<AlbumData>> getAllAlbumDatas();

	@NonNull
	LiveData<ArtistData> getLiveArtistDataByName(@NonNull String name);

	@NonNull
	List<ArtistData> getArtistDatasByNames(@NonNull List<String> names);

	@NonNull
	ArtistData getArtistDataByName(@NonNull String name);

	@NonNull
	List<AlbumData> getAlbumDatas(@NonNull String mbid);

	@NonNull
	LiveData<List<ArtistData>> getAllArtistData();

	@NonNull
	LiveData<ArtistData> getArtistData(@NonNull String mbid);

	@NonNull
	LiveData<List<AlbumData>> getLiveAlbumDatas(@NonNull String mbid);

	List<TagData> getTopTagDatas();

	@Query("SELECT * from tags")
	@NonNull
	LiveData<List<TagData>> getTopTagLiveDatas();

	List<TrackData> getTrackDatas(@NonNull String mbid);

	LiveData<List<TrackData>> getTrackLiveDatas(@NonNull String mbid);

	void saveAlbumData(@NonNull AlbumData albumData);

	void saveArtist(@NonNull ArtistData artistData);

	void saveMultipleAlbumDatas(@NonNull List<AlbumData> albumDatas);

	void saveMultipleArtists(@NonNull List<ArtistData> artistDataList);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void saveMultipleTagDatas(@NonNull List<TagData> tagDatas);

	void saveMultipleTrackDatas(@NonNull List<TrackData> trackDatas);

	void saveTrackData(@NonNull TrackData trackData);

}
