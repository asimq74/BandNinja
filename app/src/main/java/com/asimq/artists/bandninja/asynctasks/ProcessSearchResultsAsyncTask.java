package com.asimq.artists.bandninja.asynctasks;

import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.ResultsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessSearchResultsAsyncTask extends AsyncTask<String, Void, Void> {

    public static final String API_KEY = BuildConfig.LastFMApiKey;
    public static final String DEFAULT_FORMAT = "json";
    public static final int SEARCH_RESULTS_LIMIT = 10;
    private static Map<String, Boolean> mapOfAttachmentTasks = new HashMap<>();
    final String TAG = this.getClass().getSimpleName();
    private final Map<String, Artist> resultsMap = new HashMap<>();
    private final Map<String, Integer> listenersMap = new HashMap<>();
    private final List<String> dataAlreadyExistsKeys = new ArrayList<>();
    private final MediatorLiveData<List<Artist>> artistsLiveDataObservable;
    private final MediatorLiveData<Boolean> isRefreshingObservable;
    @Inject
    BandItemRepository bandItemRepository;

    public ProcessSearchResultsAsyncTask(Context applicationContext,
                                         MediatorLiveData<List<Artist>> artistsLiveDataObservable,
                                         MediatorLiveData<Boolean> isRefreshingObservable) {
        final MyApplication application = (MyApplication) applicationContext;
        application.getApplicationComponent().inject(this);
        this.artistsLiveDataObservable = artistsLiveDataObservable;
        this.isRefreshingObservable = isRefreshingObservable;
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
    protected Void doInBackground(String... strings) {
        String query = strings[0];
        Log.d(TAG, "onQueryTextSubmit: query->" + query);
        final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
        Call<ResultsWrapper> call = service.getArtists("artist.search", query,
                API_KEY, DEFAULT_FORMAT, SEARCH_RESULTS_LIMIT);
        call.enqueue(new Callback<ResultsWrapper>() {

            @Override
            public void onFailure(Call<ResultsWrapper> call, Throwable t) {
                Log.e(TAG, "error calling service", t);
                isRefreshingObservable.setValue(false);
                artistsLiveDataObservable.setValue(new ArrayList<Artist>());
            }

            @Override
            public void onResponse(Call<ResultsWrapper> call, Response<ResultsWrapper> response) {
                final ResultsWrapper artistPojo = response.body();
                if (artistPojo == null) {
                    isRefreshingObservable.setValue(false);
                    artistsLiveDataObservable.setValue(new ArrayList<>());
                    return;
                }

                Log.i(TAG, "result: " + artistPojo.getResult());
                List<Artist> artists = artistPojo.getResult().getArtistmatches().getArtists();
                if (null == artists) {
                    isRefreshingObservable.setValue(false);
                    artistsLiveDataObservable.setValue(new ArrayList<>());
                    return;
                }
                artists = Util.removeAllItemsWithoutMbidOrImages(artists);
                Collections.sort(artists);
                if (artists.isEmpty()) {
                    isRefreshingObservable.setValue(false);
                    artistsLiveDataObservable.setValue(new ArrayList<>());
                }
                for (Artist artist : artists) {
                    resultsMap.put(artist.getName(), artist);
                    listenersMap.put(artist.getName(), artist.getListeners());
                }
                if (null == resultsMap || resultsMap.keySet().isEmpty()) {
                    Log.e(TAG, "no results returned");
                    artistsLiveDataObservable.setValue(new ArrayList<>());
                    isRefreshingObservable.setValue(false);
                    return;
                }
                List<String> artistKeys = new ArrayList<>();
                artistKeys.addAll(resultsMap.keySet());
                new ArtistDataAsyncTask(artistsLiveDataObservable, isRefreshingObservable)
                        .executeOnExecutor(Executors.newSingleThreadExecutor(), artistKeys);
            }
        });
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isRefreshingObservable.setValue(true);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    class ArtistDataAsyncTask extends AsyncTask<List<String>, Void, List<ArtistData>> {

        private final MediatorLiveData<List<Artist>> artistsLiveDataObservable;
        private final MediatorLiveData<Boolean> isRefreshingObservable;

        public ArtistDataAsyncTask(MediatorLiveData<List<Artist>> artistsLiveDataObservable,
                                   MediatorLiveData<Boolean> isRefreshingObservable) {
            this.artistsLiveDataObservable = artistsLiveDataObservable;
            this.isRefreshingObservable = isRefreshingObservable;
        }

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
            if (resultsMap.size() == artistDatas.size()) {
                ArrayList artists = new ArrayList(resultsMap.values());
                Collections.sort(artists);
                artistsLiveDataObservable.setValue(artists);
                isRefreshingObservable.setValue(false);
                return;
            }
            for (String artistName : resultsMap.keySet()) {
                if (!dataAlreadyExistsKeys.contains(artistName)) {
                    new ArtistInfoAsyncTask(artistsLiveDataObservable, artistName)
                            .executeOnExecutor(Executors.newSingleThreadExecutor(), artistName);
                }
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
                    String name = artist.getName();
                    if (listenersMap.containsKey(name)) {
                        artist.setListeners(listenersMap.get(name));
                    }
                    resultsMap.put(name, artist);
                    new SaveArtistDataTask(bandItemRepository).execute(new ArtistData(artist));
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            removeTask(taskQueryString);
            if (isTasksEmpty()) {
                Log.d(TAG, "final results map = " + resultsMap);
                ArrayList artists = new ArrayList(resultsMap.values());
                Collections.sort(artists);
                artistsLiveDataObservable.setValue(artists);
                isRefreshingObservable.setValue(false);
            }
        }
    }
}
