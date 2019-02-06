package com.asimq.artists.bandninja;

import java.io.IOException;
import java.util.List;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;
import com.asimq.artists.bandninja.room.database.BandItemDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RoomDbtest {
	private ArtistDataDao artistDataDao;
	private AlbumDataDao albumDataDao;
	private TrackDataDao trackDataDao;
	private BandItemDatabase mDb;

	@Before
	public void createDb() {
		mDb = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), BandItemDatabase.class)
				.fallbackToDestructiveMigration().build();
		artistDataDao = mDb.artistDataDao();
		albumDataDao = mDb.albumDataDao();
		trackDataDao = mDb.trackDataDao();
	}

	@After
	public void closeDb() throws IOException {
		if (mDb != null) {
			mDb.close();
		}
	}

	@Test
	public void writeUserAndReadInList() throws Exception {
		AlbumData albumData = new AlbumData();
		albumData.setArtist("Band Aid");
		albumData.setImage("");
		albumData.setMbid("asdfsafsfd");
		albumData.setName("Feed The World");
		TrackData track1 = new TrackData();
		track1.setName("track 1");
		track1.setAlbumName(albumData.getName());
		track1.setAlbumId(albumData.getMbid());
		albumData.getTrackDatas().add(track1);
		TrackData track2 = new TrackData();
		track2.setName("track 2");
		track2.setAlbumName(albumData.getName());
		track2.setAlbumId(albumData.getMbid());
		albumData.getTrackDatas().add(track2);
		insertAlbumWithTracks(albumData);
		List<AlbumData> byName = getAlbumsWithTracks("Band Aid");
		assert(byName.size() > 0);
		final AlbumData albumData1 = byName.get(0);
		final List<TrackData> trackDatas = albumData1.getTrackDatas();
		assert(trackDatas.size() == 2);
		final TrackData trackData = trackDatas.get(0);
		assertEquals("track 1", trackData.getName());
		assertEquals(albumData1.getMbid(), trackData.getAlbumId());
		assertEquals(albumData1.getName(), trackData.getAlbumName());
		assertEquals(albumData1.getArtist(), trackData.getArtistName());
	}

	public void insertAlbumWithTracks(AlbumData albumData) {
		List<TrackData> trackDataList = albumData.getTrackDatas();
		for (int i = 0; i < trackDataList.size(); i++) {
			final TrackData trackData = trackDataList.get(i);
			trackData.setAlbumId(albumData.getMbid());
			trackData.setAlbumName(albumData.getName());
			trackData.setArtistName(albumData.getArtist());
			trackDataDao.insertTrackData(trackData);
		}
		albumDataDao.insertAlbumData(albumData);
	}

	public List<AlbumData> getAlbumsWithTracks(String artist) {
		List<AlbumData> byName = albumDataDao.fetchAlbumDatasByArtist(artist);
		for (AlbumData albumData : byName) {
			albumData.setTrackDatas(trackDataDao.fetchTrackDatasByArtistAndAlbum(albumData.getArtist(), albumData.getName()));
		}
		return byName;
	}

}
