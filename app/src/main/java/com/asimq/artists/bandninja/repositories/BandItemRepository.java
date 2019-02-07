package com.asimq.artists.bandninja.repositories;

import java.util.List;
import java.util.Set;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;
import com.asimq.artists.bandninja.room.TrackData;

public interface BandItemRepository {

	AlbumData getAlbumDataByNameAndId(@NonNull String name, @NonNull String mbid);

	@NonNull
	List<AlbumData> getAlbumDatas(@NonNull String mbid);

	@NonNull
	List<AlbumData> getAlbumDatasByAlbumNames(@NonNull List<String> names);

	@NonNull
	List<AlbumData> getAllAlbumDatas();

	@NonNull
	LiveData<List<AlbumData>> getAllAlbumLiveDatas();

	@NonNull
	LiveData<List<ArtistData>> getAllArtistData();

	@NonNull
	LiveData<List<TrackData>> getAllTrackLiveDatas();

	@NonNull
	LiveData<ArtistData> getArtistLiveDataById(@NonNull String mbid);

	@NonNull
	ArtistData getArtistDataByName(@NonNull String name);

	@NonNull
	List<ArtistData> getArtistDatasByNames(@NonNull Set<String> names);

	LiveData<List<ArtistData>> getArtistLiveDatasByNames(@NonNull Set<String> names);

	@NonNull
	LiveData<List<AlbumData>> getLiveAlbumDatas(@NonNull String mbid);

	@NonNull
	LiveData<ArtistData> getArtistLiveDataByName(@NonNull String name);

	List<TagData> getTopTagDatas();

	@NonNull
	LiveData<List<TagData>> getTopTagLiveDatas();

	List<TrackData> getTrackDatas(@NonNull String mbid);

	List<TrackData> getTrackDatasByArtistName(@NonNull String artistName);

	LiveData<List<TrackData>> getTrackLiveDatas(@NonNull String mbid);

	List<TrackData> getTrackLiveDatasByArtistAndAlbum(@NonNull String artistName, @NonNull String albumName);

	void saveAlbumData(@NonNull AlbumData albumData);

	void saveArtist(@NonNull ArtistData artistData);

	void saveMultipleAlbumDatas(@NonNull List<AlbumData> albumDatas);

	void saveMultipleArtists(@NonNull List<ArtistData> artistDataList);

	void saveMultipleTagDatas(@NonNull List<TagData> tagDatas);

	void saveMultipleTrackDatas(@NonNull List<TrackData> trackDatas);

	void saveTrackData(@NonNull TrackData trackData);

	void insertAlbumWithTracks(AlbumData albumData);

	@NonNull
	List<AlbumData> getAlbumsWithTracks(String artist);

}
