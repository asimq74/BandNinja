package com.asimq.artists.bandninja.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.ArtistTag;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;

@Database(entities = {ArtistData.class, ArtistTag.class}, version = 3, exportSchema = false)
public abstract class BandItemDatabase extends RoomDatabase {

	public abstract ArtistDataDao artistDataDao();

	public abstract ArtistTagDao artistTagDao();
}
