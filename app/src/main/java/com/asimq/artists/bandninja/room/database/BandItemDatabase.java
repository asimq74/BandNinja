package com.asimq.artists.bandninja.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.ArtistTag;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;

@Database(entities = {ArtistData.class, ArtistTag.class, TrackData.class}, version = 5, exportSchema = false)
public abstract class BandItemDatabase extends RoomDatabase {

	public abstract ArtistDataDao artistDataDao();

	public abstract ArtistTagDao artistTagDao();

	public abstract TrackDataDao trackDataDao();
}
