package com.asimq.artists.bandninja.viewmodelfactories;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;

@Singleton
public class AlbumDetailViewModelFactory implements ViewModelProvider.Factory {

	private final AlbumInfoRepository albumInfoRepository;
	private final Application application;
	private final BandItemRepository bandItemRepository;

	@Inject
	public AlbumDetailViewModelFactory(@NonNull Application application, @NonNull AlbumInfoRepository albumInfoRepository,
									   BandItemRepository bandItemRepository) {
		this.application = application;
		this.albumInfoRepository = albumInfoRepository;
		this.bandItemRepository = bandItemRepository;
	}

	@Override
	public AlbumDetailViewModel create(@NonNull Class modelClass) {
		return new AlbumDetailViewModel(application, albumInfoRepository, bandItemRepository);
	}
}
