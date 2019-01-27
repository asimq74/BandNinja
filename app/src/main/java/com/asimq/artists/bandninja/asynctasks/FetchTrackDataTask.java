package com.asimq.artists.bandninja.asynctasks;

import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.TrackData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


public class FetchTrackDataTask extends AsyncTask<String, Void, List<TrackData>> {

    private final MediatorLiveData<Map<String, Track>> tracksByAlbumLiveDataObservable;
    private final MediatorLiveData<Boolean> isRefreshingObservable;
    @Inject
    BandItemRepository bandItemRepository;

    public FetchTrackDataTask(Context applicationContext, MediatorLiveData<Map<String, Track>> tracksByAlbumLiveDataObservable, MediatorLiveData<Boolean> isRefreshingObservable) {
        this.tracksByAlbumLiveDataObservable = tracksByAlbumLiveDataObservable;
        this.isRefreshingObservable = isRefreshingObservable;
        final MyApplication application = (MyApplication) applicationContext;
        application.getApplicationComponent().inject(this);
    }

    @Override
    protected void onPreExecute() {
        isRefreshingObservable.setValue(true);
        super.onPreExecute();
    }

    @Override
    protected List<TrackData> doInBackground(String... artists) {
        List<TrackData> trackDatas = bandItemRepository.getTrackDatas(artists[0]);
        return trackDatas;
    }

    @Override
    protected void onPostExecute(List<TrackData> trackDatas) {
        super.onPostExecute(trackDatas);
        Map<String, Track> trackMap = new LinkedHashMap<>();
        if (trackDatas == null || trackDatas.isEmpty()) {
            isRefreshingObservable.setValue(false);
            tracksByAlbumLiveDataObservable.setValue(trackMap);
            return;
        }
        for (TrackData trackData : trackDatas) {
            Track track = new Track(trackData);
            trackMap.put(trackData.getAlbumName(), track);
        }
        isRefreshingObservable.setValue(false);
        tracksByAlbumLiveDataObservable.setValue(trackMap);
    }
}
