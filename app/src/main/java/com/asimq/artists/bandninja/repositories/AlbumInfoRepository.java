package com.asimq.artists.bandninja.repositories;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.Artist;

public interface AlbumInfoRepository {


	LiveData<List<Album>> getAlbums(@NonNull String artistName);
}
