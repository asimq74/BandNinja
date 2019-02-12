package com.asimq.artists.bandninja.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.asynctasks.searchResults.EmptySearchResultsProcessor;
import com.asimq.artists.bandninja.asynctasks.searchResults.FoundSearchResultsProcessor;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.ArtistsWrapper;
import com.asimq.artists.bandninja.json.ResultsWrapper;
import com.asimq.artists.bandninja.json.TopArtistsByTagWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.utils.Util;
import com.google.gson.internal.LinkedTreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsModelRepositoryDao implements SearchResultsRepository {

	public class ArtistContainer {

		private Artist artist;

		public ArtistContainer(Artist artist) {
			this.artist = artist;
		}

		public Artist getArtist() {
			return artist;
		}

		public void setArtist(Artist artist) {
			this.artist = artist;
		}
	}

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	public static final int SEARCH_RESULTS_LIMIT = 10;
	final String TAG = this.getClass().getSimpleName();

	@Override
	public Artist getArtist(@NonNull String artistName) {
		final GetMusicInfo service
				= BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<ArtistWrapper> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
				API_KEY, DEFAULT_FORMAT);
		final ArtistContainer container = new ArtistContainer(new Artist());
		artistInfoCall.enqueue(new Callback<ArtistWrapper>() {
			@Override
			public void onFailure(Call<ArtistWrapper> call, Throwable t) {
				container.setArtist(new Artist());
			}

			@Override
			public void onResponse(Call<ArtistWrapper> call, Response<ArtistWrapper> response) {
				final ArtistWrapper artistWrapper = response.body();
				if (artistWrapper == null) {
					container.setArtist(new Artist());
					return;
				}
				container.setArtist(artistWrapper.getArtist());
			}
		});
		return container.getArtist();
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
	public LiveData<List<Artist>> getTopArtists() {
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		final MutableLiveData<List<Artist>> artistMutableLiveData = new MutableLiveData<>();
		Call<ArtistsWrapper> topArtistsCall = service.getTopArtists("chart.gettopartists", API_KEY, DEFAULT_FORMAT,
				SEARCH_RESULTS_LIMIT);
		topArtistsCall.enqueue(new Callback<ArtistsWrapper>() {
			@Override
			public void onFailure(Call<ArtistsWrapper> call, Throwable t) {
				artistMutableLiveData.setValue(new ArrayList<>());
			}

			@Override
			public void onResponse(Call<ArtistsWrapper> call, Response<ArtistsWrapper> response) {
				final ArtistsWrapper artistsWrapper = response.body();
				if (artistsWrapper == null) {
					return;
				}
				List<Artist> artists = artistsWrapper.getTopArtists().getArtists();
				if (null == artists) {
					artistMutableLiveData.setValue(new ArrayList<>());
					return;
				}
				artists = Util.removeAllItemsWithoutMbidOrImages(artists);
				Collections.sort(artists);
				artistMutableLiveData.setValue(artists);
			}
		});
		return artistMutableLiveData;
	}

	@Override
	public void searchForArtistByTag(@NonNull String tagName, @NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull MediatorLiveData<Map<String, Artist>> searchResultsByArtistObservable) {
		Map<String, Artist> resultsByArtistName = new LinkedTreeMap<>();
		isRefreshingObservable.setValue(true);
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<TopArtistsByTagWrapper> artistInfoByTagCall = service.getArtistByTag("tag.gettopartists", tagName,
				API_KEY, DEFAULT_FORMAT);
		artistInfoByTagCall.enqueue(new Callback<TopArtistsByTagWrapper>() {
			@Override
			public void onFailure(Call<TopArtistsByTagWrapper> call, Throwable t) {
				Log.e(TAG, "error calling service", t);
				new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
						.executeOnExecutor(Executors.newSingleThreadExecutor());
			}

			@Override
			public void onResponse(Call<TopArtistsByTagWrapper> call, Response<TopArtistsByTagWrapper> response) {
				final TopArtistsByTagWrapper artistPojo = response.body();
				if (artistPojo == null) {
					new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
							.executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}

				Log.i(TAG, "result: " + artistPojo.getTopArtists());
				List<Artist> artists = artistPojo.getTopArtists().getArtists();
				if (null == artists) {
					new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
							.executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}
				artists = Util.removeAllItemsWithoutMbidOrImages(artists);
				Collections.sort(artists);
				for (Artist artist : artists) {
					resultsByArtistName.put(artist.getName(), artist);
				}
				new FoundSearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
						.executeOnExecutor(Executors.newSingleThreadExecutor(), resultsByArtistName);
			}
		});
	}

	@Override
	public void searchForTopArtists(@NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull MediatorLiveData<Map<String, Artist>> searchResultsByArtistObservable) {
		isRefreshingObservable.setValue(true);
		Map<String, Artist> resultsByArtistName = new LinkedTreeMap<>();
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance()
				.create(GetMusicInfo.class);
		Call<ArtistsWrapper> topArtistsCall = service.getTopArtists("chart.gettopartists",
				API_KEY, DEFAULT_FORMAT, SEARCH_RESULTS_LIMIT);
		topArtistsCall.enqueue(new Callback<ArtistsWrapper>() {
			@Override
			public void onFailure(Call<ArtistsWrapper> call, Throwable t) {
				Log.e(TAG, "error calling service", t);
				new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
						.executeOnExecutor(Executors.newSingleThreadExecutor());
			}

			@Override
			public void onResponse(Call<ArtistsWrapper> call, Response<ArtistsWrapper> response) {
				final ArtistsWrapper artistPojo = response.body();
				if (artistPojo == null) {
					new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
							.executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}

				Log.i(TAG, "result: " + artistPojo.getTopArtists());
				List<Artist> artists = artistPojo.getTopArtists().getArtists();
				if (null == artists) {
					new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
							.executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}
				artists = Util.removeAllItemsWithoutMbidOrImages(artists);
				Collections.sort(artists);
				for (Artist artist : artists) {
					resultsByArtistName.put(artist.getName(), artist);
				}
				new FoundSearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
						.executeOnExecutor(Executors.newSingleThreadExecutor(), resultsByArtistName);
			}
		});
	}

	@Override
	public void searchResultsByArtistName(@NonNull String query, @NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull MediatorLiveData<Map<String, Artist>> searchResultsByArtistObservable) {
		isRefreshingObservable.setValue(true);
		Map<String, Artist> resultsByArtistName = new LinkedTreeMap<>();
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<ResultsWrapper> call = service.getArtists("artist.search", query,
				API_KEY, DEFAULT_FORMAT, SEARCH_RESULTS_LIMIT);
		call.enqueue(new Callback<ResultsWrapper>() {

			@Override
			public void onFailure(Call<ResultsWrapper> call, Throwable t) {
				Log.e(TAG, "error calling service", t);
				new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
						.executeOnExecutor(Executors.newSingleThreadExecutor());
			}

			@Override
			public void onResponse(Call<ResultsWrapper> call, Response<ResultsWrapper> response) {
				final ResultsWrapper artistPojo = response.body();
				if (artistPojo == null) {
					new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
							.executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}

				Log.i(TAG, "result: " + artistPojo.getResult());
				List<Artist> artists = artistPojo.getResult().getArtistmatches().getArtists();
				if (null == artists) {
					new EmptySearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
							.executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}
				artists = Util.removeAllItemsWithoutMbidOrImages(artists);
				Collections.sort(artists);
				for (Artist artist : artists) {
					resultsByArtistName.put(artist.getName(), artist);
				}
				new FoundSearchResultsProcessor(isRefreshingObservable, searchResultsByArtistObservable)
						.executeOnExecutor(Executors.newSingleThreadExecutor(), resultsByArtistName);
			}
		});
	}
}
