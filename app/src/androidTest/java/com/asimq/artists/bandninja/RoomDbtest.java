package com.asimq.artists.bandninja;

import java.io.IOException;
import java.util.List;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
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
	private BandItemDatabase mDb;

	@Before
	public void createDb() {
		mDb = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), BandItemDatabase.class)
				.fallbackToDestructiveMigration().build();
		artistDataDao = mDb.artistDataDao();
		albumDataDao = mDb.albumDataDao();
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
		albumDataDao.insertAlbumData(albumData);
		List<AlbumData> byName = albumDataDao.fetchAlbumDatasByArtist("Band Aid");
		assert(byName.size() > 0);
	}
}
