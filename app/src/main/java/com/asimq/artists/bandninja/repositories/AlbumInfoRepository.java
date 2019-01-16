package com.asimq.artists.bandninja.repositories;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;

import java.util.List;

public interface AlbumInfoRepository {


    LiveData<List<Album>> getAlbums(@NonNull String artistName);

    LiveData<AlbumInfo> getAlbumInfo(@NonNull String artistName, @NonNull String albumName);
}
