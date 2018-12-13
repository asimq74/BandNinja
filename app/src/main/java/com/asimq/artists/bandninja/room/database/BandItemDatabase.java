package com.asimq.artists.bandninja.room.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

@Database(entities = {ArtistData.class}, version = 1, exportSchema = false)
public abstract class BandItemDatabase extends RoomDatabase {

	public abstract ArtistDataDao artistDataDao();
}
