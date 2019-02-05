package com.asimq.artists.bandninja.asynctasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.ResultsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.utils.Util;
import com.google.gson.internal.LinkedHashTreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessSearchResultsAsyncTask extends AsyncTask<String, Void, Void> {

	class ArtistDataAsyncTask extends AsyncTask<Set<String>, Void, List<ArtistData>> {

		private final MediatorLiveData<List<Artist>> artistsLiveDataObservable;
		private final MediatorLiveData<Boolean> isRefreshingObservable;

		public ArtistDataAsyncTask(MediatorLiveData<List<Artist>> artistsLiveDataObservable,
				MediatorLiveData<Boolean> isRefreshingObservable) {
			this.artistsLiveDataObservable = artistsLiveDataObservable;
			this.isRefreshingObservable = isRefreshingObservable;
		}

		@Override
		protected List<ArtistData> doInBackground(Set<String>... lists) {
			Set<String> artistNames = lists[0];
			return bandItemRepository.getArtistDatasByNames(artistNames);
		}

		@Override
		protected void onPostExecute(List<ArtistData> artistDatas) {
			if (null != artistDatas) {
				for (ArtistData artistData : artistDatas) {
					dataAlreadyExistsKeys.add(artistData.getName());
					Artist artist = new Artist(artistData);
					resultsMap.put(artist.getName(), artist);
				}
			}
			Log.d(TAG, "results map after artist data task = " + resultsMap);
			if (resultsMap.size() == artistDatas.size()) {
				ArrayList artists = new ArrayList(resultsMap.values());
				Collections.sort(artists);
				artistsLiveDataObservable.setValue(artists);
				isRefreshingObservable.setValue(false);
				return;
			}
			for (String artistName : resultsMap.keySet()) {
				if (!dataAlreadyExistsKeys.contains(artistName)) {
					new ArtistInfoAsyncTask(artistsLiveDataObservable, artistName)
							.executeOnExecutor(Executors.newSingleThreadExecutor(), artistName);
				}
			}
		}
	}

	class ArtistInfoAsyncTask extends AsyncTask<String, Void, Void> {

		private final MediatorLiveData<List<Artist>> artistsLiveDataObservable;
		private final String taskQueryString;

		public ArtistInfoAsyncTask(MediatorLiveData<List<Artist>> artistsLiveDataObservable,
				String taskQueryString) {
			this.artistsLiveDataObservable = artistsLiveDataObservable;
			this.taskQueryString = taskQueryString;
			addTask(taskQueryString);
		}

		@Override
		protected Void doInBackground(String... strings) {
			String artistName = strings[0];
			final GetMusicInfo service
					= RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
			Call<ArtistWrapper> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
					API_KEY, DEFAULT_FORMAT);
			artistInfoCall.enqueue(new Callback<ArtistWrapper>() {
				@Override
				public void onFailure(Call<ArtistWrapper> call, Throwable t) {
				}

				@Override
				public void onResponse(Call<ArtistWrapper> call, Response<ArtistWrapper> response) {
					final ArtistWrapper artistWrapper = response.body();
					if (artistWrapper == null) {
						return;
					}
					Artist artist = artistWrapper.getArtist();
					String name = artist.getName();
					if (listenersMap.containsKey(name)) {
						artist.getStats().setListeners(listenersMap.get(name) + "");
					}
					resultsMap.put(name, artist);
					new SaveArtistDataTask(bandItemRepository).execute(new ArtistData(artist));
					removeTask(taskQueryString);
					if (isTasksEmpty()) {
						Log.d(TAG, "final results map = " + resultsMap);
						ArrayList artists = new ArrayList(resultsMap.values());
						Collections.sort(artists);
						artistsLiveDataObservable.setValue(artists);
						isRefreshingObservable.setValue(false);
					}
				}
			});
			return null;
		}

		@Override
		protected void onPostExecute(Void avoid) {

		}
	}

	class EmptyDataProcessor extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... voids) {
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			isRefreshingObservable.setValue(false);
			artistsLiveDataObservable.setValue(new ArrayList<>());
		}
	}
	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	public static final int SEARCH_RESULTS_LIMIT = 10;
	private static Map<String, Boolean> mapOfAttachmentTasks = new LinkedHashTreeMap<>();

	public static synchronized void addTask(String taskQueryString) {
		mapOfAttachmentTasks.put(taskQueryString, true);
	}

	public static synchronized boolean isTasksEmpty() {
		return mapOfAttachmentTasks.isEmpty();
	}

	public static synchronized void removeTask(String taskQueryString) {
		mapOfAttachmentTasks.remove(taskQueryString);
	}
	private final String TAG = this.getClass().getSimpleName();
	private final MediatorLiveData<List<Artist>> artistsLiveDataObservable;
	@Inject
	BandItemRepository bandItemRepository;
	private final List<String> dataAlreadyExistsKeys = new ArrayList<>();
	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final Map<String, Integer> listenersMap = new LinkedHashTreeMap<>();
	private final Map<String, Artist> resultsMap = new LinkedHashTreeMap<>();

	public ProcessSearchResultsAsyncTask(Context applicationContext,
			MediatorLiveData<List<Artist>> artistsLiveDataObservable,
			MediatorLiveData<Boolean> isRefreshingObservable) {
		final MyApplication application = (MyApplication) applicationContext;
		application.getApplicationComponent().inject(this);
		this.artistsLiveDataObservable = artistsLiveDataObservable;
		this.isRefreshingObservable = isRefreshingObservable;
	}

	@Override
	protected Void doInBackground(String... strings) {
		String query = strings[0];
		Log.d(TAG, "onQueryTextSubmit: query->" + query);
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<ResultsWrapper> call = service.getArtists("artist.search", query,
				API_KEY, DEFAULT_FORMAT, SEARCH_RESULTS_LIMIT);
		call.enqueue(new Callback<ResultsWrapper>() {

			@Override
			public void onFailure(Call<ResultsWrapper> call, Throwable t) {
				Log.e(TAG, "error calling service", t);
				new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
			}

			@Override
			public void onResponse(Call<ResultsWrapper> call, Response<ResultsWrapper> response) {
				final ResultsWrapper artistPojo = response.body();
				if (artistPojo == null) {
					new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}

				Log.i(TAG, "result: " + artistPojo.getResult());
				List<Artist> artists = artistPojo.getResult().getArtistmatches().getArtists();
				if (null == artists) {
					new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}
				artists = Util.removeAllItemsWithoutMbidOrImages(artists);
				Collections.sort(artists);
				if (artists.isEmpty()) {
					new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
				}
				for (Artist artist : artists) {
					resultsMap.put(artist.getName(), artist);
					listenersMap.put(artist.getName(), Util.stringToInt(artist.getStats().getListeners()));
				}
				if (null == resultsMap || resultsMap.keySet().isEmpty()) {
					Log.e(TAG, "no results returned");
					new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
					return;
				}
				Set<String> artistKeys = new LinkedHashSet<>();
				artistKeys.addAll(resultsMap.keySet());
				new ArtistDataAsyncTask(artistsLiveDataObservable, isRefreshingObservable)
						.executeOnExecutor(Executors.newSingleThreadExecutor(), artistKeys);
			}
		});
		return null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		isRefreshingObservable.setValue(true);
	}
}
