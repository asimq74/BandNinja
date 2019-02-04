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
import com.asimq.artists.bandninja.asynctasks.artists.ArtistDatasByNamesFromStorageTask;
import com.asimq.artists.bandninja.asynctasks.artists.EmptyArtistsProcessor;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.BaseMusicItem;
import com.asimq.artists.bandninja.json.MusicItem;
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
	private LiveData<List<ArtistData>> mObservableArtistDatasFromStorage = new MediatorLiveData<>();

	private MediatorLiveData<Boolean> isRefreshingObservable = new MediatorLiveData<>();
	private MediatorLiveData<List<Artist>> artistDatasObservable = new MediatorLiveData<>();


	public ArtistDetailViewModel(@NonNull Application application, @NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.bandItemRepository = bandItemRepository;
	}


	public LiveData<List<ArtistData>> getArtistDataLiveDatasFromStorage(Map<String, Artist> searchResultsByName) {
		final Set<String> names = searchResultsByName.keySet();
		if (null == names || names.isEmpty()) {
			return mObservableArtistDatasFromStorage;
		}
		mObservableArtistDatasFromStorage = bandItemRepository.getArtistLiveDatasByNames(names);
		return mObservableArtistDatasFromStorage;
	}

	public void populateArtistDatasFromStorage(@NonNull Map<String, Artist> searchResultsByName,
			@NonNull List<ArtistData> artistDatas) {
		isRefreshingObservable.setValue(true);
		final Set<String> names = searchResultsByName.keySet();
		if (names.isEmpty()) {
			new EmptyArtistsProcessor(isRefreshingObservable, artistDatasObservable)
					.executeOnExecutor(Executors.newSingleThreadExecutor());
			return;
		}
		new ArtistDatasByNamesFromStorageTask(bandItemRepository, searchResultsByName, artistDatas,
				isRefreshingObservable, artistDatasObservable).executeOnExecutor(Executors.newSingleThreadExecutor(), names);
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
