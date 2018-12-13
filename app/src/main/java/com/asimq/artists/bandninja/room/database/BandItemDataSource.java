package com.asimq.artists.bandninja.room.database;

import java.util.List;

import javax.inject.Inject;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class BandItemDataSource implements BandItemRepository {

	private ArtistDataDao artistDataDao;

	@Inject
	public BandItemDataSource(ArtistDataDao artistDataDao) {
		this.artistDataDao = artistDataDao;
	}

	@NonNull
	@Override
	public LiveData<List<ArtistData>> getAllArtistData() {
		return artistDataDao.fetchAllArtists();
	}

	@NonNull
	@Override
	public LiveData<ArtistData> getArtistData(@NonNull String mbid) {
		return artistDataDao.fetchArtistById(mbid);
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
