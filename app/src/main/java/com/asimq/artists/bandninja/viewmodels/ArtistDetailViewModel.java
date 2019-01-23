package com.asimq.artists.bandninja.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.asynctasks.SaveArtistDataTask;
import com.asimq.artists.bandninja.asynctasks.SaveArtistTagTask;
import com.asimq.artists.bandninja.asynctasks.SaveTagDataTask;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.room.TagData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

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

    public void saveArtist(@NonNull Artist artist) {
        ArtistData artistData = new ArtistData(artist);
        new SaveArtistDataTask(bandItemRepository).execute(artistData);
    }

    public void saveTagDatas(@NonNull List<Tag> tags) {
        List<TagData> tagDatas = new ArrayList<>();
        for (Tag tag: tags) {
            tagDatas.add(new TagData(tag));
        }
        new SaveTagDataTask(bandItemRepository).executeOnExecutor(Executors.newSingleThreadExecutor(), tagDatas);
    }
}
