package com.asimq.artists.bandninja.repositories;

import java.util.List;
import java.util.Set;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;

public interface BandItemRepository {

	AlbumData getAlbumDataByNameAndId(@NonNull String name, @NonNull String mbid);

	@NonNull
	List<AlbumData> getAlbumsWithTracks(String artist);

	@NonNull
	List<AlbumData> getAllAlbumDatas();

	@NonNull
	List<ArtistData> getAllArtistDatas();

	@NonNull
	List<ArtistData> getArtistDatasByNames(@NonNull Set<String> names);

	@NonNull
	LiveData<ArtistData> getArtistLiveDataById(@NonNull String mbid);

	@NonNull
	LiveData<ArtistData> getArtistLiveDataByName(@NonNull String name);

	List<TagData> getTopTagDatas();

	void insertAlbumWithTracks(@NonNull AlbumData albumData);

	void saveArtist(@NonNull ArtistData artistData);

	void saveMultipleTagDatas(@NonNull List<TagData> tagDatas);

	void saveTag(@NonNull TagData tagData);

}
