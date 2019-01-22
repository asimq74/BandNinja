package com.asimq.artists.bandninja.repositories;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.ArtistTag;
import com.asimq.artists.bandninja.room.TrackData;

public interface BandItemRepository {

	@NonNull
	LiveData<List<ArtistData>> getAllArtistData();

	@NonNull
	LiveData<ArtistData> getArtistData(@NonNull String mbid);

	void saveArtist(@NonNull ArtistData artistData);

	void saveMultipleArtists(@NonNull List<ArtistData> artistDataList);

	@NonNull
	LiveData<List<ArtistTag>> getArtistTags(@NonNull String artistDataMbId);

	void saveArtistTag(@NonNull ArtistTag artistTag);

	void saveMultipleArtistTags(@NonNull List<ArtistTag> artistTagList);


	List<TrackData> getTrackDatas(@NonNull String mbid);

	LiveData<List<TrackData>> getTrackLiveDatas(@NonNull String mbid);


	void saveTrackData(@NonNull TrackData trackData);

	void saveMultipleTrackDatas(@NonNull List<TrackData> trackDatas);

}
