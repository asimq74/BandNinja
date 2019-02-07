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

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	public static final int LIMIT = 10;
	public static final int PAGES = 1;

	@Override
	public LiveData<AlbumInfo> getAlbumInfo(@NonNull String mbId) {
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		final MutableLiveData<AlbumInfo> albumMutableLiveData = new MutableLiveData<>();
		Call<AlbumInfoWrapper> albumInfoCall = service.getAlbumInfo("album.getinfo", mbId, API_KEY, DEFAULT_FORMAT);
		albumInfoCall.enqueue(new Callback<AlbumInfoWrapper>() {
			@Override
			public void onFailure(Call<AlbumInfoWrapper> call, Throwable t) {
				albumMutableLiveData.setValue(new AlbumInfo());
			}

			@Override
			public void onResponse(Call<AlbumInfoWrapper> call, Response<AlbumInfoWrapper> response) {
				final AlbumInfoWrapper albumInfoWrapper = response.body();
				if (null == albumInfoWrapper) {
					return;
				}
				albumMutableLiveData.setValue(albumInfoWrapper.getAlbumInfo());
			}
		});
		return albumMutableLiveData;
	}

	@Override
	public LiveData<AlbumInfo> getAlbumInfo(@NonNull String artistName, @NonNull String albumName) {
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		final MutableLiveData<AlbumInfo> albumMutableLiveData = new MutableLiveData<>();
		Call<AlbumInfoWrapper> albumInfoCall = service.getAlbumInfo("album.getinfo", artistName, albumName, API_KEY, DEFAULT_FORMAT);
		albumInfoCall.enqueue(new Callback<AlbumInfoWrapper>() {
			@Override
			public void onFailure(Call<AlbumInfoWrapper> call, Throwable t) {
				albumMutableLiveData.setValue(new AlbumInfo());
			}

			@Override
			public void onResponse(Call<AlbumInfoWrapper> call, Response<AlbumInfoWrapper> response) {
				final AlbumInfoWrapper albumInfoWrapper = response.body();
				if (null == albumInfoWrapper) {
					return;
				}
				albumMutableLiveData.setValue(albumInfoWrapper.getAlbumInfo());
			}
		});
		return albumMutableLiveData;
	}

	@Override
	public LiveData<List<Album>> getAlbums(@NonNull String artistName) {
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		final MutableLiveData<List<Album>> albumsMutableLiveData = new MutableLiveData<>();
		Call<TopAlbumsWrapper> topAlbumsWrapperCall
				= service.getTopAlbums("artist.getTopAlbums", artistName, API_KEY,
				DEFAULT_FORMAT, PAGES, LIMIT);
		topAlbumsWrapperCall.enqueue(new Callback<TopAlbumsWrapper>() {
			@Override
			public void onFailure(Call<TopAlbumsWrapper> call, Throwable t) {
				albumsMutableLiveData.setValue(Collections.EMPTY_LIST);
			}

			@Override
			public void onResponse(Call<TopAlbumsWrapper> call, Response<TopAlbumsWrapper> response) {
				final TopAlbumsWrapper topAlbumsWrapper = response.body();
				List<Album> albums = topAlbumsWrapper.getTopAlbums().getAlbums();
				if (null == topAlbumsWrapper || null == topAlbumsWrapper.getTopAlbums()
						|| albums.isEmpty()) {
					return;
				}
				albums = Util.removeAllItemsWithoutMbidOrImages(albums);
				albumsMutableLiveData.setValue(albums);
			}
		});
		return albumsMutableLiveData;
	}

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
