package com.asimq.artists.bandninja.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.ArtistTag;

import java.util.List;

@Dao
public interface ArtistTagDao {

    @Query("SELECT * from artistTags where artistDataMbId = :artistDataMbId")
    @NonNull
    LiveData<List<ArtistTag>> fetchArtistTagsByArtistId(@NonNull String artistDataMbId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArtistTag(@NonNull ArtistTag artistTag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleArtistTags(@NonNull List<ArtistTag> artistTagList);
}
