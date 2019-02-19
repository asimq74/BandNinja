package com.asimq.artists.bandninja.room.database;

import java.util.List;
import java.util.Set;

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

	@Override
	public AlbumData getAlbumDataByNameAndId(@NonNull String name, @NonNull String mbid) {
		AlbumData albumData = albumDataDao.fetchAlbumDataByNameAndId(name, mbid);
		albumData.setTrackDatas(trackDataDao.fetchTrackDatasByArtistAndAlbum(albumData.getArtist(), albumData.getName()));
		return albumData;
	}

	@Override
	public List<AlbumData> getAlbumsWithTracks(String artist) {
		List<AlbumData> albumDatasByArtist = albumDataDao.fetchAlbumDatasByArtist(artist);
		for (AlbumData albumData : albumDatasByArtist) {
			albumData.setTrackDatas(trackDataDao.fetchTrackDatasByArtistAndAlbum(albumData.getArtist(), albumData.getName()));
		}
		return albumDatasByArtist;
	}

	@NonNull
	@Override
	public List<AlbumData> getAllAlbumDatas() {
		List<AlbumData> albumDatasByArtist = albumDataDao.fetchAllAlbumDatas();
		for (AlbumData albumData : albumDatasByArtist) {
			albumData.setTrackDatas(trackDataDao.fetchTrackDatasByArtistAndAlbum(albumData.getArtist(), albumData.getName()));
		}
		return albumDatasByArtist;
	}

	@NonNull
	@Override
	public List<ArtistData> getAllArtistDatas() {
		return artistDataDao.fetchAllArtistDatas();
	}

	@NonNull
	@Override
	public List<ArtistData> getArtistDatasByNames(@NonNull Set<String> names) {
		return artistDataDao.fetchArtistDataByNames(names);
	}

	@NonNull
	@Override
	public LiveData<ArtistData> getArtistLiveDataById(@NonNull String mbid) {
		return artistDataDao.fetchLiveArtistDataById(mbid);
	}

	@NonNull
	@Override
	public LiveData<ArtistData> getArtistLiveDataByName(@NonNull String name) {
		return artistDataDao.fetchLiveArtistDataByName(name);
	}

	@Override
	public List<TagData> getTopTagDatas() {
		return tagDataDao.fetchTopTagDatas();
	}

	@Override
	public void insertAlbumWithTracks(AlbumData albumData) {
		List<TrackData> trackDataList = albumData.getTrackDatas();
		for (TrackData trackData : trackDataList) {
			trackData.setAlbumId(albumData.getMbid());
			trackData.setAlbumName(albumData.getName());
			trackData.setArtistName(albumData.getArtist());
			trackDataDao.insertTrackData(trackData);
		}
		albumDataDao.insertAlbumData(albumData);
	}

	@Override
	public void saveArtist(@NonNull ArtistData artistData) {
		artistDataDao.insertArtist(artistData);
	}

	@Override
	public void saveMultipleTagDatas(@NonNull List<TagData> tagDatas) {
		tagDataDao.insertMultipleTagDatas(tagDatas);
	}

	@Override
	public void saveTag(@NonNull TagData tagData) {
		tagDataDao.insertTagData(tagData);
	}

}
