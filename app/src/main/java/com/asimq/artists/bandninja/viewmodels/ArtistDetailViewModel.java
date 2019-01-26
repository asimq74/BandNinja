package com.asimq.artists.bandninja.viewmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.SaveArtistDataTask;
import com.asimq.artists.bandninja.asynctasks.SaveTagDataTask;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;

public class ArtistDetailViewModel extends AndroidViewModel {

	private final BandItemRepository bandItemRepository;
	private LiveData<ArtistData> mLiveArtistData;
	// MediatorLiveData can observe other LiveData objects and react on their emissions.
	private final MediatorLiveData<List<ArtistData>> mObservableArtistDatas;

	public ArtistDetailViewModel(@NonNull Application application, @NonNull BandItemRepository bandItemRepository) {
		super(application);
		this.bandItemRepository = bandItemRepository;
		this.mObservableArtistDatas = new MediatorLiveData<>();
		this.mObservableArtistDatas.setValue(new ArrayList<>());
		LiveData<List<ArtistData>> artistDatas = bandItemRepository.getAllArtistData();
		this.mObservableArtistDatas.addSource(artistDatas, mObservableArtistDatas::setValue);
	}


	public LiveData<List<ArtistData>> getAllArtistDatas() {
		return mObservableArtistDatas;
	}


	public LiveData<ArtistData> getArtistDetail(@NonNull String mbid) {
		if (mLiveArtistData == null) {
			mLiveArtistData = bandItemRepository.getArtistData(mbid);
		}
		return mLiveArtistData;
	}

	public void saveArtist(@NonNull Artist artist) {
		ArtistData artistData = new ArtistData(artist);
		new SaveArtistDataTask(bandItemRepository).execute(artistData);
	}

}
