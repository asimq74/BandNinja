package com.asimq.artists.bandninja.repositories;

import java.util.Collections;
import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.ResultsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.utils.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsModelRepositoryDao implements SearchResultsRepository {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	public static final int SEARCH_RESULTS_LIMIT = 10;

	final String TAG = this.getClass().getSimpleName();

	private MutableLiveData<Boolean> artistsRefreshingMutableLiveData = new MutableLiveData<>();

	@Override
	public LiveData<Boolean> getArtistsRefreshingMutableLiveData() {
		return artistsRefreshingMutableLiveData;
	}

	@Override
	public LiveData<Artist> getArtistInfo(@NonNull String artistName) {
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		final MutableLiveData<Artist> artistInfoMutableLiveData = new MutableLiveData<>();
		Call<ArtistWrapper> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
				API_KEY, DEFAULT_FORMAT);
		artistInfoCall.enqueue(new Callback<ArtistWrapper>() {
			@Override
			public void onFailure(Call<ArtistWrapper> call, Throwable t) {
				artistInfoMutableLiveData.setValue(null);
			}

			@Override
			public void onResponse(Call<ArtistWrapper> call, Response<ArtistWrapper> response) {
				final ArtistWrapper artistWrapper = response.body();
				if (artistWrapper == null) {
					return;
				}
				artistInfoMutableLiveData.setValue(artistWrapper.getArtist());
			}
		});
		return artistInfoMutableLiveData;
	}

	@Override
	public LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String query) {
		artistsRefreshingMutableLiveData.setValue(true);
		final MutableLiveData<List<Artist>> artistMutableLiveData = new MutableLiveData<>();
		Log.d(TAG, "onQueryTextSubmit: query->" + query);
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<ResultsWrapper> call = service.getArtists("artist.search", query,
				API_KEY, DEFAULT_FORMAT, SEARCH_RESULTS_LIMIT);
		call.enqueue(new Callback<ResultsWrapper>() {

			@Override
			public void onFailure(Call<ResultsWrapper> call, Throwable t) {
				Log.e(TAG, "error calling service", t);
				artistMutableLiveData.setValue(null);
			}

			@Override
			public void onResponse(Call<ResultsWrapper> call, Response<ResultsWrapper> response) {
				final ResultsWrapper artistPojo = response.body();
				if (artistPojo == null) {
					return;
				}

				Log.i(TAG, "result: " + artistPojo.getResult());
				List<Artist> artists = artistPojo.getResult().getArtistmatches().getArtists();
				if (null == artists) {
					artistMutableLiveData.setValue(null);
					return;
				}
				artists = Util.removeAllItemsWithoutMbidOrImages(artists);
				Collections.sort(artists);
				artistMutableLiveData.setValue(artists);
			}
		});
		artistsRefreshingMutableLiveData.setValue(false);
		return artistMutableLiveData;
	}
}
