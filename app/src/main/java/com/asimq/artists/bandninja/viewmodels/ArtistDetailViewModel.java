package com.asimq.artists.bandninja.viewmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.SaveArtistDataTask;
import com.asimq.artists.bandninja.asynctasks.artists.ArtistDatasByNamesFromStorageTask;
import com.asimq.artists.bandninja.asynctasks.artists.EmptyArtistsProcessor;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;

public class ArtistDetailViewModel extends AndroidViewModel {

	private MediatorLiveData<List<Artist>> artistsObservable = new MediatorLiveData<>();
	private final BandItemRepository bandItemRepository;
	private MediatorLiveData<Boolean> isRefreshingObservable = new MediatorLiveData<>();
	private LiveData<ArtistData> mLiveArtistData;
	private LiveData<List<ArtistData>> mObservableArtistDatasFromStorage = new MediatorLiveData<>();

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

	public LiveData<ArtistData> getArtistDetail(@NonNull String mbid) {
		if (mLiveArtistData == null) {
			mLiveArtistData = bandItemRepository.getArtistData(mbid);
		}
		return mLiveArtistData;
	}

	public MediatorLiveData<List<Artist>> getArtistsObservable() {
		return artistsObservable;
	}

	public MediatorLiveData<Boolean> getIsRefreshingObservable() {
		return isRefreshingObservable;
	}

	public void populateArtistDatasFromStorage(@NonNull Map<String, Artist> searchResultsByName) {
		isRefreshingObservable.setValue(true);
		final Set<String> names = searchResultsByName.keySet();
		if (names.isEmpty()) {
			new EmptyArtistsProcessor(isRefreshingObservable, artistsObservable)
					.executeOnExecutor(Executors.newSingleThreadExecutor());
			return;
		}
		new ArtistDatasByNamesFromStorageTask(bandItemRepository, searchResultsByName, new ArrayList<>(),
				isRefreshingObservable, artistsObservable).executeOnExecutor(Executors.newSingleThreadExecutor(), names);
	}

	public void saveArtist(@NonNull Artist artist) {
		ArtistData artistData = new ArtistData(artist);
		new SaveArtistDataTask(bandItemRepository).execute(artistData);
	}

}
