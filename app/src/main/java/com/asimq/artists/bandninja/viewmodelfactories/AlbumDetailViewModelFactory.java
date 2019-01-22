package com.asimq.artists.bandninja.viewmodelfactories;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;

@Singleton
public class AlbumDetailViewModelFactory implements ViewModelProvider.Factory {

	private final AlbumInfoRepository albumInfoRepository;
	private final Application application;
	private final TrackDataDao trackDataDao;

	@Inject
	public AlbumDetailViewModelFactory(@NonNull Application application, @NonNull AlbumInfoRepository albumInfoRepository,
			@NonNull TrackDataDao trackDataDao) {
		this.application = application;
		this.albumInfoRepository = albumInfoRepository;
		this.trackDataDao = trackDataDao;
	}

	@Override
	public AlbumDetailViewModel create(@NonNull Class modelClass) {
		return new AlbumDetailViewModel(application, albumInfoRepository, trackDataDao);
	}
}
