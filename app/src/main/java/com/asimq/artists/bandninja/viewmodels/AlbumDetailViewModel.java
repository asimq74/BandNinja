package com.asimq.artists.bandninja.viewmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.AlbumArtistTuple;
import com.asimq.artists.bandninja.asynctasks.FetchTrackDataTask;
import com.asimq.artists.bandninja.asynctasks.ProcessAlbumsByArtistAsyncTask;
import com.asimq.artists.bandninja.asynctasks.SaveAlbumDataTask;
import com.asimq.artists.bandninja.asynctasks.SaveTracksTask;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;

public class AlbumDetailViewModel extends AndroidViewModel {

	private final AlbumInfoRepository albumInfoRepository;
	private LiveData<List<Album>> mLiveAlbums;
	private LiveData<AlbumInfo> mLiveAlbumInfo;
	private LiveData<List<TrackData>> mLiveTrackDatas;
	private LiveData<List<AlbumData>> mLiveAlbumDatas;
	private final TrackDataDao trackDataDao;
	private final AlbumDataDao albumDataDao;
	private final MediatorLiveData<List<AlbumData>> mObservableAlbumDatas;
	private final MediatorLiveData<List<TrackData>> mObservableTrackDatas;
	private final BandItemRepository bandItemRepository;

	private MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable = new MediatorLiveData<>();
	private MediatorLiveData<Boolean> isRefreshingObservable = new MediatorLiveData<>();
	private MediatorLiveData<Map<String, Track>> tracksByAlbumLiveDataObservable = new MediatorLiveData<>();

	public MediatorLiveData<Map<String, Track>> getTracksByAlbumLiveDataObservable() {
		return tracksByAlbumLiveDataObservable;
	}

	public AlbumDetailViewModel(@NonNull Application application,
								@NonNull AlbumInfoRepository albumInfoRepository,
								@NonNull BandItemRepository bandItemRepository,
								TrackDataDao trackDataDao, AlbumDataDao albumDataDao) {
		super(application);
		this.albumInfoRepository = albumInfoRepository;
		this.bandItemRepository = bandItemRepository;
		this.trackDataDao = trackDataDao;
		this.albumDataDao = albumDataDao;
		this.mObservableAlbumDatas = new MediatorLiveData<>();
		this.mObservableAlbumDatas.setValue(new ArrayList<>());
		LiveData<List<AlbumData>> albumDatas = bandItemRepository.getAllAlbumDatas();
		this.mObservableAlbumDatas.addSource(albumDatas, mObservableAlbumDatas::setValue);
		this.mObservableTrackDatas = new MediatorLiveData<>();
		this.mObservableTrackDatas.setValue(new ArrayList<>());
		LiveData<List<TrackData>> trackDatas = bandItemRepository.getAllTrackLiveDatas();
		this.mObservableTrackDatas.addSource(trackDatas, mObservableTrackDatas::setValue);
	}


	public void searchForTracks(@NonNull Context context, @NonNull String artistName) {
		albumInfoRepository.searchForTracks(context, tracksByAlbumLiveDataObservable,
				isRefreshingObservable, artistName);
	}

	public void searchForAlbums(@NonNull Context context, @NonNull String artistName) {
		albumInfoRepository.searchForAlbums(context, albumsLiveDataObservable,
				isRefreshingObservable, artistName);
	}

	public MediatorLiveData<List<AlbumInfo>> getAlbumsLiveDataObservable() {
		return albumsLiveDataObservable;
	}

	public MediatorLiveData<Boolean> getIsRefreshingObservable() {
		return isRefreshingObservable;
	}

	public LiveData<List<AlbumData>> getAllAlbumDatas() {
		return mObservableAlbumDatas;
	}

	public LiveData<List<TrackData>> getAllTrackDatas() {
		return mObservableTrackDatas;
	}

	public LiveData<List<Album>> getAlbumsByArtist(@NonNull String artistName) {
		mLiveAlbums = albumInfoRepository.getAlbums(artistName);
		return mLiveAlbums;
	}

	public LiveData<List<AlbumData>> getAlbumDatas(@NonNull String artistName) {
		mLiveAlbumDatas = albumDataDao.fetchLiveAlbumDatasByArtist(artistName);
		return mLiveAlbumDatas;
	}

	public LiveData<List<TrackData>> getTrackDatas(@NonNull String mbid) {
		mLiveTrackDatas = trackDataDao.fetchLiveTrackDatas(mbid);
		return mLiveTrackDatas;
	}

	public LiveData<AlbumInfo> getAlbumInfo(@NonNull String artistName, @NonNull String albumName) {
		mLiveAlbumInfo = albumInfoRepository.getAlbumInfo(artistName, albumName);
		return mLiveAlbumInfo;
	}

	public LiveData<AlbumInfo> getAlbumInfo(@NonNull String mbId) {
		mLiveAlbumInfo = albumInfoRepository.getAlbumInfo(mbId);
		return mLiveAlbumInfo;
	}

	public void saveTracks(@NonNull AlbumInfo albumInfo) {
		List<TrackData> trackDatas = new ArrayList<>();
		for (Track track : albumInfo.getTrackWrapper().getTracks()) {
			TrackData trackData = new TrackData(track);
			trackData.updateAlbumInfo(albumInfo);
			trackDatas.add(trackData);
		}
		new SaveTracksTask(bandItemRepository).execute(trackDatas);
	}

	public void saveAlbumData(@NonNull AlbumInfo albumInfo) {
		AlbumData albumData = new AlbumData(albumInfo);
		new SaveAlbumDataTask(bandItemRepository).executeOnExecutor(Executors.newSingleThreadExecutor(), albumData);
	}
}
