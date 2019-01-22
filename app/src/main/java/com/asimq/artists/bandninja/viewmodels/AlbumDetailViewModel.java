package com.asimq.artists.bandninja.viewmodels;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.SaveTracksTask;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;

public class AlbumDetailViewModel extends AndroidViewModel {

	private final AlbumInfoRepository albumInfoRepository;
	private LiveData<List<Album>> mLiveAlbums;
	private LiveData<AlbumInfo> mLiveAlbumInfo;
	private final TrackDataDao trackDataDao;

	public AlbumDetailViewModel(@NonNull Application application,
			@NonNull AlbumInfoRepository albumInfoRepository, TrackDataDao trackDataDao) {
		super(application);
		this.albumInfoRepository = albumInfoRepository;
		this.trackDataDao = trackDataDao;
	}

	public LiveData<List<Album>> getAlbumsByArtist(@NonNull String artistName) {
		mLiveAlbums = albumInfoRepository.getAlbums(artistName);
		return mLiveAlbums;
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
}
