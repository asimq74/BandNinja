package com.asimq.artists.bandninja.viewmodels;

import java.util.List;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;

public class AlbumDetailViewModel extends AndroidViewModel {

	private final AlbumInfoRepository albumInfoRepository;
	private LiveData<List<Album>> mLiveAlbums;

	public AlbumDetailViewModel(@NonNull Application application,
			@NonNull AlbumInfoRepository albumInfoRepository) {
		super(application);
		this.albumInfoRepository = albumInfoRepository;
	}

	public LiveData<List<Album>> getAlbumsByArtist(@NonNull String artistName) {
		mLiveAlbums = albumInfoRepository.getAlbums(artistName);
		return mLiveAlbums;
	}

}
