package com.asimq.artists.bandninja.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.TagDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;

@Database(entities = {ArtistData.class, TrackData.class, AlbumData.class,
		TagData.class}, version = 10, exportSchema = false)
public abstract class BandItemDatabase extends RoomDatabase {

	public abstract AlbumDataDao albumDataDao();

	public abstract ArtistDataDao artistDataDao();

	public abstract TagDataDao tagDataDao();

	public abstract TrackDataDao trackDataDao();
}
