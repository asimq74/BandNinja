package com.asimq.artists.bandninja.repositories;

import java.util.List;

import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.AlbumInfo;

public interface AlbumInfoRepository {

	void searchForAlbums(@NonNull MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable,
			@NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull BandItemRepository bandItemRepository,
			@NonNull String artistName);

}
