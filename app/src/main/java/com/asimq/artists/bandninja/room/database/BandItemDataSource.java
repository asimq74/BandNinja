package com.asimq.artists.bandninja.room.database;

import java.util.List;

import javax.inject.Inject;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.ArtistTag;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;

public class BandItemDataSource implements BandItemRepository {

	private final ArtistDataDao artistDataDao;
	private final ArtistTagDao artistTagDao;
	private final TrackDataDao trackDataDao;

	@Inject
	public BandItemDataSource(ArtistDataDao artistDataDao, ArtistTagDao artistTagDao, TrackDataDao trackDataDao) {
		this.artistDataDao = artistDataDao;
		this.artistTagDao = artistTagDao;
		this.trackDataDao = trackDataDao;
	}

	@NonNull
	@Override
	public LiveData<List<ArtistData>> getAllArtistData() {
		return artistDataDao.fetchAllArtistLiveDatas();
	}

	@NonNull
	@Override
	public LiveData<ArtistData> getArtistData(@NonNull String mbid) {
		return artistDataDao.fetchLiveArtistDataById(mbid);
	}

	@NonNull
	@Override
	public LiveData<List<ArtistTag>> getArtistTags(@NonNull String artistDataMbId) {
		return artistTagDao.fetchArtistTagsByArtistId(artistDataMbId);
	}

	@Override
	public List<TrackData> getTrackDatas(@NonNull String mbid) {
		return trackDataDao.fetchTrackDatas(mbid);
	}

	@Override
	public LiveData<List<TrackData>> getTrackLiveDatas(@NonNull String mbid) {
		return trackDataDao.fetchLiveTrackDatas(mbid);
	}

	@Override
	public void saveArtist(@NonNull ArtistData artistData) {
		artistDataDao.insertArtist(artistData);
	}

	@Override
	public void saveArtistTag(@NonNull ArtistTag artistTag) {
		artistTagDao.insertArtistTag(artistTag);
	}

	@Override
	public void saveMultipleArtistTags(@NonNull List<ArtistTag> artistTagList) {
		artistTagDao.insertMultipleArtistTags(artistTagList);
	}

	@Override
	public void saveMultipleArtists(@NonNull List<ArtistData> artistDataList) {
		artistDataDao.insertMultipleArtists(artistDataList);
	}

	@Override
	public void saveMultipleTrackDatas(@NonNull List<TrackData> trackDatas) {
		trackDataDao.insertMultipleTrackDatas(trackDatas);
	}

	@Override
	public void saveTrackData(@NonNull TrackData trackData) {
		trackDataDao.insertTrackData(trackData);
	}
}
