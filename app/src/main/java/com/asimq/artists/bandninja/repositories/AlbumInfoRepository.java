package com.asimq.artists.bandninja.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Track;

import java.util.List;
import java.util.Map;

public interface AlbumInfoRepository {


    LiveData<List<Album>> getAlbums(@NonNull String artistName);

    LiveData<AlbumInfo> getAlbumInfo(@NonNull String artistName, @NonNull String albumName);

    LiveData<AlbumInfo> getAlbumInfo(@NonNull String mbId);

    void searchForAlbums(@NonNull Context context,
                         @NonNull MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable,
                         @NonNull MediatorLiveData<Boolean> isRefreshingObservable,
                         @NonNull String artistName);

    void searchForTracks(@NonNull Context context,
                         @NonNull MediatorLiveData<Map<String, Track>> tracksByAlbumLiveDataObservable,
                         @NonNull MediatorLiveData<Boolean> isRefreshingObservable,
                         @NonNull String artistName);
}
