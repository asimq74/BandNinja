package com.asimq.artists.bandninja.repositories;

import java.util.Collections;
import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.ResultsWrapper;
import com.asimq.artists.bandninja.json.TopAlbumsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetArtists;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumInfoRepositoryDao implements AlbumInfoRepository {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	public static final int PAGES = 1;
	final String TAG = this.getClass().getSimpleName();

	@Override
	public LiveData<List<Album>> getAlbums(@NonNull String artistName) {
		final GetArtists service = RetrofitClientInstance.getRetrofitInstance().create(GetArtists.class);
		final MutableLiveData<List<Album>> albumsMutableLiveData = new MutableLiveData<>();
		Call<TopAlbumsWrapper> topAlbumsWrapperCall = service.getTopAlbums("artist.getTopAlbums", artistName, API_KEY, DEFAULT_FORMAT, PAGES);
		topAlbumsWrapperCall.enqueue(new Callback<TopAlbumsWrapper>() {
			@Override
			public void onResponse(Call<TopAlbumsWrapper> call, Response<TopAlbumsWrapper> response) {
				final TopAlbumsWrapper topAlbumsWrapper = response.body();
				if (null == topAlbumsWrapper || null == topAlbumsWrapper.getTopAlbums()
						|| topAlbumsWrapper.getTopAlbums().getAlbums().isEmpty()) {
					return;
				}
				albumsMutableLiveData.setValue(topAlbumsWrapper.getTopAlbums().getAlbums());
			}

			@Override
			public void onFailure(Call<TopAlbumsWrapper> call, Throwable t) {
				albumsMutableLiveData.setValue(Collections.EMPTY_LIST);
			}
		});
		return albumsMutableLiveData;
	}

}
