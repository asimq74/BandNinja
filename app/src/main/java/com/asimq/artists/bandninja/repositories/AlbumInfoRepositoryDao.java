package com.asimq.artists.bandninja.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.asynctasks.albums.AlbumDatasByAlbumNameFromStorageTask;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.AlbumInfoWrapper;
import com.asimq.artists.bandninja.json.TopAlbumsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.utils.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumInfoRepositoryDao implements AlbumInfoRepository {

	@Override
	public void searchForAlbums(@NonNull MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable,
			@NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull BandItemRepository bandItemRepository,
			@NonNull String artistName) {
		List<AlbumData> albumDatas = new ArrayList<>();
		new AlbumDatasByAlbumNameFromStorageTask(bandItemRepository, albumDatas, isRefreshingObservable,
				albumsLiveDataObservable).executeOnExecutor(Executors.newSingleThreadExecutor(), artistName);
	}

}
