package com.asimq.artists.bandninja.viewmodels;

import java.util.List;
import java.util.concurrent.Executors;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.FetchAllSavedAlbumDataTask;
import com.asimq.artists.bandninja.asynctasks.albums.AlbumDataByNameAndIdFromStorageTask;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;

public class AlbumDetailViewModel extends AndroidViewModel {

	private final AlbumInfoRepository albumInfoRepository;
	private MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable = new MediatorLiveData<>();
	private final BandItemRepository bandItemRepository;
	private MediatorLiveData<Boolean> isRefreshingObservable = new MediatorLiveData<>();
	private MediatorLiveData<AlbumData> mObservableAlbumData = new MediatorLiveData<>();
	private MediatorLiveData<List<AlbumData>> mObservableAlbumDatas = new MediatorLiveData<>();

	public AlbumDetailViewModel(@NonNull Application application, @NonNull AlbumInfoRepository albumInfoRepository,
			@NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.albumInfoRepository = albumInfoRepository;
		this.bandItemRepository = bandItemRepository;
	}

	public MediatorLiveData<List<AlbumInfo>> getAlbumsLiveDataObservable() {
		return albumsLiveDataObservable;
	}

	public LiveData<List<AlbumData>> getAllAlbumDatas() {
		return mObservableAlbumDatas;
	}

	public MediatorLiveData<Boolean> getIsRefreshingObservable() {
		return isRefreshingObservable;
	}

	public MediatorLiveData<AlbumData> getObservableAlbumData() {
		return mObservableAlbumData;
	}

	public void obtainAlbumData(@NonNull String name, @NonNull String id) {
		new AlbumDataByNameAndIdFromStorageTask(bandItemRepository, mObservableAlbumData)
				.executeOnExecutor(Executors.newSingleThreadExecutor(), name, id);
	}

	public void obtainAllAlbumDatas(Context context) {
		new FetchAllSavedAlbumDataTask(context, mObservableAlbumDatas).
				executeOnExecutor(Executors.newSingleThreadExecutor());
	}

	public void searchForAlbums(@NonNull String artistName) {
		isRefreshingObservable.setValue(true);
		albumInfoRepository.searchForAlbums(albumsLiveDataObservable, isRefreshingObservable,
				bandItemRepository, artistName);
	}

}
