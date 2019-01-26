package com.asimq.artists.bandninja.room.database;

import java.util.List;

import javax.inject.Inject;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.TagDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;

public class BandItemDataSource implements BandItemRepository {

	private final AlbumDataDao albumDataDao;
	private final ArtistDataDao artistDataDao;
	private final TagDataDao tagDataDao;
	private final TrackDataDao trackDataDao;

	@Inject
	public BandItemDataSource(ArtistDataDao artistDataDao,
			TrackDataDao trackDataDao, AlbumDataDao albumDataDao,
			TagDataDao tagDataDao) {
		this.artistDataDao = artistDataDao;
		this.trackDataDao = trackDataDao;
		this.albumDataDao = albumDataDao;
		this.tagDataDao = tagDataDao;
	}

	@NonNull
	@Override
	public ArtistData getArtistDataByName(@NonNull String name) {
		return artistDataDao.fetchArtistDataByName(name);
	}

	@NonNull
	@Override
	public LiveData<List<TrackData>> getAllTrackLiveDatas() {
		return trackDataDao.fetchAllTrackLiveDatas();
	}

	@NonNull
	@Override
	public LiveData<ArtistData> getLiveArtistDataByName(@NonNull String name) {
		return artistDataDao.fetchLiveArtistDataByName(name);
	}

	@NonNull
	@Override
	public List<AlbumData> getAlbumDatas(@NonNull String mbid) {
		return albumDataDao.fetchAlbumDatas(mbid);
	}


	@NonNull
	@Override
	public LiveData<List<AlbumData>> getAllAlbumDatas() {
		return albumDataDao.fetchAllLiveAlbumDatas();
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
	public LiveData<List<AlbumData>> getLiveAlbumDatas(@NonNull String mbid) {
		return albumDataDao.fetchLiveAlbumDatas(mbid);
	}

	@Override
	public List<TagData> getTopTagDatas() {
		return tagDataDao.fetchTopTagDatas();
	}

	@NonNull
	@Override
	public LiveData<List<TagData>> getTopTagLiveDatas() {
		return tagDataDao.fetchTopTagLiveDatas();
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
	public void saveAlbumData(@NonNull AlbumData albumData) {
		albumDataDao.insertAlbumData(albumData);
	}

	@Override
	public void saveArtist(@NonNull ArtistData artistData) {
		artistDataDao.insertArtist(artistData);
	}

	@Override
	public void saveMultipleAlbumDatas(@NonNull List<AlbumData> albumDatas) {
		albumDataDao.insertMultipleAlbumDatas(albumDatas);
	}

	@Override
	public void saveMultipleArtists(@NonNull List<ArtistData> artistDataList) {
		artistDataDao.insertMultipleArtists(artistDataList);
	}

	@Override
	public void saveMultipleTagDatas(@NonNull List<TagData> tagDatas) {
		tagDataDao.insertMultipleTagDatas(tagDatas);
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
