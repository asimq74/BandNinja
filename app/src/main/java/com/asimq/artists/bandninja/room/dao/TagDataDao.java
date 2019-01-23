package com.asimq.artists.bandninja.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.TagData;

import java.util.List;

@Dao
public interface TagDataDao {


    @Query("SELECT * from tags")
    @NonNull
    List<TagData> fetchTopTagDatas();

    @Query("SELECT * from tags")
    @NonNull
    LiveData<List<TagData>> fetchTopTagLiveDatas();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleTagDatas(@NonNull List<TagData> tagDatas);
}
