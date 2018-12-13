package com.asimq.artists.bandninja.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;

public class ArtistDetailViewModel extends AndroidViewModel {

	private final BandItemRepository bandItemRepository;
	private LiveData<ArtistData> mLiveArtistData;

	public ArtistDetailViewModel(@NonNull Application application, @NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.bandItemRepository = bandItemRepository;
	}

	public LiveData<ArtistData> getArtistDetail(@NonNull String mbid) {
		if (mLiveArtistData == null) {
			mLiveArtistData = bandItemRepository.getArtistData(mbid);
		}
		return mLiveArtistData;
	}
}
