package com.asimq.artists.bandninja.repositories;

import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.room.ArtistData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessSearchResultsAsyncTask extends AsyncTask<String, Void, Map<String, Artist>> {

    public static final String API_KEY = BuildConfig.LastFMApiKey;
    public static final String DEFAULT_FORMAT = "json";
    private static Map<String, Boolean> mapOfAttachmentTasks = new HashMap<>();
    final String TAG = this.getClass().getSimpleName();
    @Inject
    SearchResultsRepository searchResultsRepository;
    @Inject
    BandItemRepository bandItemRepository;

    private final Map<String, Artist> resultsMap = new HashMap<>();
    private final List<String> dataAlreadyExistsKeys = new ArrayList<>();

    public ProcessSearchResultsAsyncTask(Context applicationContext) {
        final MyApplication application = (MyApplication) applicationContext;
        application.getApplicationComponent().inject(this);
    }

    public static synchronized void addTask(String taskQueryString) {
        mapOfAttachmentTasks.put(taskQueryString, true);
    }

    public static synchronized void removeTask(String taskQueryString) {
        mapOfAttachmentTasks.remove(taskQueryString);
    }

    public static synchronized boolean isTasksEmpty() {
        return mapOfAttachmentTasks.isEmpty();
    }

    @Override
    protected Map<String, Artist> doInBackground(String... strings) {
        String query = strings[0];
        return searchResultsRepository.getSearchResultsByArtistName(query);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Map<String, Artist> map) {
        super.onPostExecute(map);
        if (!(null == map || map.keySet().isEmpty())) {
            resultsMap.putAll(map);
            List<String> artistKeys = new ArrayList<>();
            artistKeys.addAll(resultsMap.keySet());
            new ArtistDataAsyncTask().executeOnExecutor(Executors.newSingleThreadExecutor(), artistKeys);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    class ArtistDataAsyncTask extends AsyncTask<List<String>, Void, List<ArtistData>> {
        @Override
        protected List<ArtistData> doInBackground(List<String>... lists) {
            List<String> artistNames = lists[0];
            return bandItemRepository.getArtistDatasByNames(artistNames);
        }

        @Override
        protected void onPostExecute(List<ArtistData> artistDatas) {
            if (null != artistDatas) {
                for (ArtistData artistData : artistDatas) {
                    dataAlreadyExistsKeys.add(artistData.getName());
                    Artist artist = new Artist(artistData);
                    resultsMap.put(artist.getName(), artist);
                }
            }
            Log.d(TAG, "results map after artist data task = " + resultsMap);
            for (String artistName : resultsMap.keySet()) {
                if (dataAlreadyExistsKeys.contains(artistName)) {
                    break;
                }
                new ArtistInfoAsyncTask(null, artistName)
                        .executeOnExecutor(Executors.newSingleThreadExecutor(), artistName);
            }
        }
    }

    class ArtistInfoAsyncTask extends AsyncTask<String, Void, Void> {

        private final MediatorLiveData<List<Artist>> artistsLiveDataObservable;
        private final String taskQueryString;

        public ArtistInfoAsyncTask(MediatorLiveData<List<Artist>> artistsLiveDataObservable,
                                   String taskQueryString) {
            this.artistsLiveDataObservable = artistsLiveDataObservable;
            this.taskQueryString = taskQueryString;
            addTask(taskQueryString);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String artistName = strings[0];
            final GetMusicInfo service
                    = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
            Call<ArtistWrapper> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
                    API_KEY, DEFAULT_FORMAT);
            artistInfoCall.enqueue(new Callback<ArtistWrapper>() {
                @Override
                public void onFailure(Call<ArtistWrapper> call, Throwable t) {
                }

                @Override
                public void onResponse(Call<ArtistWrapper> call, Response<ArtistWrapper> response) {
                    final ArtistWrapper artistWrapper = response.body();
                    if (artistWrapper == null) {
                        return;
                    }
                    Artist artist = artistWrapper.getArtist();
                    resultsMap.put(artist.getName(), artist);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            removeTask(taskQueryString);

            if (isTasksEmpty()) {
                Log.d(TAG, "final results map = " + resultsMap);
            }
        }
    }
}
