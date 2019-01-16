package com.asimq.artists.bandninja.room.database;

import java.util.List;

import javax.inject.Inject;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.ArtistTag;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;

public class BandItemDataSource implements BandItemRepository {

	private final ArtistDataDao artistDataDao;
	private final ArtistTagDao artistTagDao;

	@Inject
	public BandItemDataSource(ArtistDataDao artistDataDao, ArtistTagDao artistTagDao) {
		this.artistDataDao = artistDataDao;
		this.artistTagDao = artistTagDao;
	}

	@NonNull
	@Override
	public LiveData<List<ArtistTag>> getArtistTags(@NonNull String artistDataMbId) {
		return artistTagDao.fetchArtistTagsByArtistId(artistDataMbId);
	}

	@Override
	public void saveArtistTag(@NonNull ArtistTag artistTag) {
		artistTagDao.insertArtistTag(artistTag);
	}

	@Override
	public void saveMultipleArtistTags(@NonNull List<ArtistTag> artistTagList) {
		artistTagDao.insertMultipleArtistTags(artistTagList);
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

	@Override
	public void saveArtist(@NonNull ArtistData artistData) {
		artistDataDao.insertArtist(artistData);
	}

	@Override
	public void saveMultipleArtists(@NonNull List<ArtistData> artistDataList) {
		artistDataDao.insertMultipleArtists(artistDataList);
	}

}
