package com.asimq.artists.bandninja.viewmodels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.asynctasks.SaveArtistDataTask;
import com.asimq.artists.bandninja.asynctasks.SaveTagDataTask;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistDetailViewModel extends AndroidViewModel {

	private final BandItemRepository bandItemRepository;
	private LiveData<ArtistData> mLiveArtistData;
	// MediatorLiveData can observe other LiveData objects and react on their emissions.
	private final MediatorLiveData<List<ArtistData>> mObservableArtistDatas;
	private LiveData<List<ArtistData>> mObservableArtistDatasFromStorage = new MediatorLiveData<>();

	public ArtistDetailViewModel(@NonNull Application application, @NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.bandItemRepository = bandItemRepository;
		this.mObservableArtistDatas = new MediatorLiveData<>();
		this.mObservableArtistDatas.setValue(new ArrayList<>());
		LiveData<List<ArtistData>> artistDatas = bandItemRepository.getAllArtistData();
		this.mObservableArtistDatas.addSource(artistDatas, mObservableArtistDatas::setValue);
	}


	public LiveData<List<ArtistData>> getArtistDataLiveDatasFromStorage(Map<String, Artist> searchResultsByName) {
		final Set<String> names = searchResultsByName.keySet();
		if (null == names || names.isEmpty()) {
			return mObservableArtistDatasFromStorage;
		}
		mObservableArtistDatasFromStorage = bandItemRepository.getArtistLiveDatasByNames(names);
		return mObservableArtistDatasFromStorage;
	}

	public void populateArtistDatasFromStorage(Map<String, Artist> searchResultsByName, List<ArtistData> artistDatas) {
		final Set<String> names = searchResultsByName.keySet();
		if (null == names || names.isEmpty()) {
			return;
		}
		new ArtistDatasByNamesTask(bandItemRepository, searchResultsByName, artistDatas).executeOnExecutor(Executors.newSingleThreadExecutor(), names);
	}

	private static class ArtistDatasByNamesTask extends AsyncTask<Set<String>, Void, List<ArtistData>> {

		private final BandItemRepository bandItemRepository;
		private final List<ArtistData> artistDatas;
		private final Map<String, Artist> searchResultsByName;

		final String TAG = this.getClass().getSimpleName();

		public ArtistDatasByNamesTask(@NonNull BandItemRepository bandItemRepository,
				@NonNull Map<String, Artist> searchResultsByName, @NonNull List<ArtistData> artistDatas) {
			this.bandItemRepository = bandItemRepository;
			this.artistDatas = artistDatas;
			this.searchResultsByName = searchResultsByName;
		}

		@Override
		protected List<ArtistData> doInBackground(Set<String>... params) {
			Set<String> names = params[0];
			return bandItemRepository.getArtistDatasByNames(names);
		}

		@Override
		protected void onPostExecute(List<ArtistData> artistDatas) {
			super.onPostExecute(artistDatas);
			Set<String> remainingArtistNames = new LinkedHashSet<>();
			remainingArtistNames.addAll(searchResultsByName.keySet());
			this.artistDatas.clear();
			this.artistDatas.addAll(artistDatas);
			for (ArtistData artistData : this.artistDatas) {
				Artist artist = new Artist(artistData);
				Log.i(TAG, "artist from storage: " + artist);
				searchResultsByName.put(artist.getName(), artist);
				remainingArtistNames.remove(artist.getName());
			}
			if (!remainingArtistNames.isEmpty()) {
				new ArtistsByNamesTask(bandItemRepository, artistDatas, searchResultsByName)
						.executeOnExecutor(Executors.newSingleThreadExecutor(), remainingArtistNames);
			}
		}
	}


	private static class ArtistsByNamesTask extends AsyncTask<Set<String>, Void, Void> {

		private final BandItemRepository bandItemRepository;
		private final List<ArtistData> artistDatas;
		private final Map<String, Artist> searchResultsByName;
		public static final String API_KEY = BuildConfig.LastFMApiKey;
		public static final String DEFAULT_FORMAT = "json";

		public ArtistsByNamesTask(BandItemRepository bandItemRepository, List<ArtistData> artistDatas, Map<String, Artist> searchResultsByName) {
			this.bandItemRepository = bandItemRepository;
			this.artistDatas = artistDatas;
			this.searchResultsByName = searchResultsByName;
		}

		@Override
		protected Void doInBackground(Set<String>... params) {
			final GetMusicInfo service
					= RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
			Set<String> names = params[0];
			for (String artistName : names) {
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
						final Artist artist = artistWrapper.getArtist();
						final ArtistData artistData = new ArtistData(artist);
						searchResultsByName.put(artistData.getName(), artist);
						artistDatas.add(artistData);
						new SaveArtistDataTask(bandItemRepository).execute(artistData);
					}
				});
			}
			return null;
		}
	}

	public LiveData<List<ArtistData>> getAllArtistDatas() {
		return mObservableArtistDatas;
	}


	public LiveData<ArtistData> getArtistDetail(@NonNull String mbid) {
		if (mLiveArtistData == null) {
			mLiveArtistData = bandItemRepository.getArtistData(mbid);
		}
		return mLiveArtistData;
	}

	public void saveArtist(@NonNull Artist artist) {
		ArtistData artistData = new ArtistData(artist);
		new SaveArtistDataTask(bandItemRepository).execute(artistData);
	}

}
