package com.asimq.artists.bandninja.viewmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.SaveAlbumDataTask;
import com.asimq.artists.bandninja.asynctasks.SaveTracksTask;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.room.AlbumData;
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

	public AlbumDetailViewModel(@NonNull Application application,
			@NonNull AlbumInfoRepository albumInfoRepository,
								TrackDataDao trackDataDao, AlbumDataDao albumDataDao) {
		super(application);
		this.albumInfoRepository = albumInfoRepository;
		this.trackDataDao = trackDataDao;
		this.albumDataDao = albumDataDao;
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
		new SaveTracksTask(trackDataDao).execute(trackDatas);
	}

	public void saveAlbumData(@NonNull AlbumInfo albumInfo) {
		AlbumData albumData = new AlbumData(albumInfo);
		new SaveAlbumDataTask(albumDataDao).executeOnExecutor(Executors.newSingleThreadExecutor(), albumData);
	}
}
