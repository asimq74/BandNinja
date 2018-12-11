package com.asimq.artists.bandninja.repositories;

import java.util.Arrays;
import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.data.Artist;
import com.asimq.artists.bandninja.data.ArtistInfoPojo;
import com.asimq.artists.bandninja.data.ArtistsPojo;
import com.asimq.artists.bandninja.remote.retrofit.GetArtists;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsModelRepositoryDao implements SearchResultsRepository {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	final String TAG = this.getClass().getSimpleName();

	@Override
	public LiveData<Artist> getArtistInfo(@NonNull String artistName) {
		final GetArtists service = RetrofitClientInstance.getRetrofitInstance().create(GetArtists.class);
		final MutableLiveData<Artist> artistInfoMutableLiveData = new MutableLiveData<>();
		Call<ArtistInfoPojo> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
				API_KEY, DEFAULT_FORMAT);
		artistInfoCall.enqueue(new Callback<ArtistInfoPojo>() {
			@Override
			public void onFailure(Call<ArtistInfoPojo> call, Throwable t) {
				artistInfoMutableLiveData.setValue(null);
			}

			@Override
			public void onResponse(Call<ArtistInfoPojo> call, Response<ArtistInfoPojo> response) {
				final ArtistInfoPojo artistInfoPojo = response.body();
				if (artistInfoPojo == null) {
					return;
				}
				artistInfoMutableLiveData.setValue(artistInfoPojo.getArtist());
			}
		});
		return artistInfoMutableLiveData;
	}

	@Override
	public LiveData<List<Artist>> getSearchResultsByArtist(@NonNull String query) {
		final MutableLiveData<List<Artist>> artistMutableLiveData = new MutableLiveData<>();
		Log.d(TAG, "onQueryTextSubmit: query->" + query);
		final GetArtists service = RetrofitClientInstance.getRetrofitInstance().create(GetArtists.class);
		Call<ArtistsPojo> call = service.getArtists("artist.search", query,
				API_KEY, DEFAULT_FORMAT);
		call.enqueue(new Callback<ArtistsPojo>() {

			@Override
			public void onFailure(Call<ArtistsPojo> call, Throwable t) {
				Log.e(TAG, "error calling service", t);
				artistMutableLiveData.setValue(null);
			}

			@Override
			public void onResponse(Call<ArtistsPojo> call, Response<ArtistsPojo> response) {
				final ArtistsPojo artistPojo = response.body();
				if (artistPojo == null) {
					return;
				}

				Log.i(TAG, "result: " + artistPojo.getResult());
				final List<Artist> artists = artistPojo.getResult().getArtistmatches().getArtists();
				if (null == artists) {
					artistMutableLiveData.setValue(null);
					return;
				}
				artistMutableLiveData.setValue(artists);
			}
		});
		return artistMutableLiveData;
	}
}
