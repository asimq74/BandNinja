package com.asimq.artists.bandninja.viewmodels;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.albums.FetchAllSavedAlbumDataTask;
import com.asimq.artists.bandninja.asynctasks.artists.ArtistDatasByNamesFromStorageTask;
import com.asimq.artists.bandninja.asynctasks.artists.EmptyArtistsProcessor;
import com.asimq.artists.bandninja.asynctasks.artists.FetchAllSavedArtistDataTask;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;

public class ArtistDetailViewModel extends AndroidViewModel {

	private MediatorLiveData<List<Artist>> artistsObservable = new MediatorLiveData<>();
	private final BandItemRepository bandItemRepository;
	private MediatorLiveData<Boolean> isRefreshingObservable = new MediatorLiveData<>();
	private LiveData<ArtistData> mLiveArtistData;
	private MediatorLiveData<List<ArtistData>> mObservableArtistDatas = new MediatorLiveData<>();

	public ArtistDetailViewModel(@NonNull Application application, @NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.bandItemRepository = bandItemRepository;
	}

	public LiveData<ArtistData> getArtistLiveDataById(@NonNull String mbid) {
		if (mLiveArtistData == null) {
			mLiveArtistData = bandItemRepository.getArtistLiveDataById(mbid);
		}
		return mLiveArtistData;
	}

	public LiveData<ArtistData> getArtistLiveDataByName(@NonNull String artistName) {
		if (mLiveArtistData == null) {
			mLiveArtistData = bandItemRepository.getArtistLiveDataByName(artistName);
		}
		return mLiveArtistData;
	}

	public MediatorLiveData<List<Artist>> getArtistsObservable() {
		return artistsObservable;
	}

	public MediatorLiveData<Boolean> getIsRefreshingObservable() {
		return isRefreshingObservable;
	}

	public MediatorLiveData<List<ArtistData>> getObservableArtistDatas() {
		return mObservableArtistDatas;
	}

	public void obtainAllArtistDatas(Context context) {
		new FetchAllSavedArtistDataTask(context, mObservableArtistDatas).
				executeOnExecutor(Executors.newSingleThreadExecutor());
	}

	public void populateArtistDatasFromStorage(@NonNull Map<String, Artist> searchResultsByName) {
		isRefreshingObservable.setValue(true);
		final Set<String> names = searchResultsByName.keySet();
		if (names.isEmpty()) {
			new EmptyArtistsProcessor(isRefreshingObservable, artistsObservable)
					.executeOnExecutor(Executors.newSingleThreadExecutor());
			return;
		}
		new ArtistDatasByNamesFromStorageTask(bandItemRepository, searchResultsByName, isRefreshingObservable,
				artistsObservable).executeOnExecutor(Executors.newSingleThreadExecutor(), names);
	}
}
