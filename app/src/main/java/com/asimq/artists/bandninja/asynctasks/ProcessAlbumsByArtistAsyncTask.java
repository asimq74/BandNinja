package com.asimq.artists.bandninja.asynctasks;

import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.asynctasks.ProcessSearchResultsAsyncTask.EmptyDataProcessor;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.AlbumInfoWrapper;
import com.asimq.artists.bandninja.json.TopAlbumsWrapper;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.utils.Util;
import com.google.gson.internal.LinkedHashTreeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessAlbumsByArtistAsyncTask extends AsyncTask<String, Void, Void> {

    public static final String API_KEY = BuildConfig.LastFMApiKey;
    public static final String DEFAULT_FORMAT = "json";
    public static final int PAGES = 1;
    public static final int LIMIT = 10;
    private static Map<String, Boolean> mapOfAttachmentTasks = new LinkedHashTreeMap<>();
    final String TAG = this.getClass().getSimpleName();
    private final Map<String, AlbumInfo> resultsMap = new TreeMap<>();
    private final Map<String, Integer> listenersMap = new TreeMap<>();
    private final List<String> dataAlreadyExistsKeys = new ArrayList<>();
    private final MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable;
    private final MediatorLiveData<Boolean> isRefreshingObservable;
    @Inject
    BandItemRepository bandItemRepository;

    public ProcessAlbumsByArtistAsyncTask(Context applicationContext,
                                          MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable,
                                          MediatorLiveData<Boolean> isRefreshingObservable) {
        final MyApplication application = (MyApplication) applicationContext;
        application.getApplicationComponent().inject(this);
        this.albumsLiveDataObservable = albumsLiveDataObservable;
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

    class EmptyDataProcessor extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isRefreshingObservable.setValue(false);
            albumsLiveDataObservable.setValue(new ArrayList<>());
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        String artistName = strings[0];
        Log.d(TAG, "onQueryTextSubmit: query->" + artistName);
        final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
        Call<TopAlbumsWrapper> topAlbumsWrapperCall
                = service.getTopAlbums("artist.getTopAlbums", artistName, API_KEY,
                DEFAULT_FORMAT, PAGES, LIMIT);
        topAlbumsWrapperCall.enqueue(new Callback<TopAlbumsWrapper>() {

            @Override
            public void onFailure(Call<TopAlbumsWrapper> call, Throwable t) {
                Log.e(TAG, "error calling service", t);
                new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
            }

            @Override
            public void onResponse(Call<TopAlbumsWrapper> call, Response<TopAlbumsWrapper> response) {
                final TopAlbumsWrapper topAlbumsWrapper = response.body();
                if (topAlbumsWrapper == null) {
                    new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
                    return;
                }

                Log.i(TAG, "result: " + topAlbumsWrapper.getTopAlbums());
                List<Album> albums = topAlbumsWrapper.getTopAlbums().getAlbums();
                if (null == albums) {
                    new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
                    return;
                }
                albums = Util.removeAllItemsWithoutMbidOrImages(albums);
                Collections.sort(albums);
                if (albums.isEmpty()) {
                    new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
                }
                for (Album album : albums) {
                    resultsMap.put(album.getName(), new AlbumInfo(album));
                    listenersMap.put(album.getName(), album.getPlaycount());
                }
                if (null == resultsMap || resultsMap.keySet().isEmpty()) {
                    Log.e(TAG, "no results returned");
                    new EmptyDataProcessor().executeOnExecutor(Executors.newSingleThreadExecutor());
                    return;
                }
                List<String> albumKeys = new ArrayList<>();
                albumKeys.addAll(resultsMap.keySet());
                new AlbumDataAsyncTask(albumsLiveDataObservable, isRefreshingObservable)
                        .executeOnExecutor(Executors.newSingleThreadExecutor(), albumKeys);
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

    class AlbumDataAsyncTask extends AsyncTask<List<String>, Void, List<AlbumData>> {

        private final MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable;
        private final MediatorLiveData<Boolean> isRefreshingObservable;

        public AlbumDataAsyncTask(MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable,
                                  MediatorLiveData<Boolean> isRefreshingObservable) {
            this.albumsLiveDataObservable = albumsLiveDataObservable;
            this.isRefreshingObservable = isRefreshingObservable;
        }

        @Override
        protected List<AlbumData> doInBackground(List<String>... lists) {
            List<String> names = lists[0];
            return bandItemRepository.getAlbumDatasByAlbumNames(names);
        }

        @Override
        protected void onPostExecute(List<AlbumData> albumDatas) {
            if (null != albumDatas) {
                for (AlbumData albumData : albumDatas) {
                    dataAlreadyExistsKeys.add(albumData.getName());
                    AlbumInfo albumInfo = new AlbumInfo(albumData);
                    resultsMap.put(albumInfo.getName(), albumInfo);
                }
            }
            Log.d(TAG, "results map after album data task = " + resultsMap);
            if (resultsMap.size() == albumDatas.size()) {
                ArrayList albums = new ArrayList(resultsMap.values());
                Collections.sort(albums);
                albumsLiveDataObservable.setValue(albums);
                isRefreshingObservable.setValue(false);
                return;
            }
            List<AlbumArtistTuple> albumArtistTuples = new ArrayList<>();
            for (String albumName : resultsMap.keySet()) {
                albumArtistTuples.add(new AlbumArtistTuple(resultsMap.get(albumName).getArtist(), albumName));
            }
            for (AlbumArtistTuple albumArtistTuple : albumArtistTuples) {
                if (!dataAlreadyExistsKeys.contains(albumArtistTuple.getAlbum())) {
                    new AlbumInfoAsyncTask(albumsLiveDataObservable, albumArtistTuple.getAlbum())
                            .executeOnExecutor(Executors.newSingleThreadExecutor(), albumArtistTuple.getAlbum(),
                                    albumArtistTuple.getArtist());
                }
            }
        }
    }


    class AlbumInfoAsyncTask extends AsyncTask<String, Void, Void> {

        private final MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable;
        private final String taskQueryString;

        public AlbumInfoAsyncTask(MediatorLiveData<List<AlbumInfo>> albumsLiveDataObservable,
                                  String taskQueryString) {
            this.albumsLiveDataObservable = albumsLiveDataObservable;
            this.taskQueryString = taskQueryString;
            addTask(taskQueryString);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String albumName = strings[0];
            String artistName = strings[1];
            final GetMusicInfo service
                    = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
            Call<AlbumInfoWrapper> albumInfoCall = service.getAlbumInfo("album.getinfo", artistName, albumName, API_KEY, DEFAULT_FORMAT);

            albumInfoCall.enqueue(new Callback<AlbumInfoWrapper>() {
                @Override
                public void onFailure(Call<AlbumInfoWrapper> call, Throwable t) {
                }

                @Override
                public void onResponse(Call<AlbumInfoWrapper> call, Response<AlbumInfoWrapper> response) {
                    final AlbumInfoWrapper albumInfoWrapper = response.body();
                    if (albumInfoWrapper == null) {
                        return;
                    }
                    AlbumInfo albumInfo = albumInfoWrapper.getAlbumInfo();
                    String name = albumInfo.getName();
                    resultsMap.put(name, albumInfo);
                    AlbumData albumData = new AlbumData(albumInfo);
                    List<TrackData> trackDatas = new ArrayList<>();
                    for (Track track : albumInfo.getTrackWrapper().getTracks()) {
                        TrackData trackData = new TrackData(track);
                        trackData.updateAlbumInfo(albumInfo);
                        trackDatas.add(trackData);
                    }
                    new SaveAlbumDataTask(bandItemRepository).execute(albumData);
                    if (!trackDatas.isEmpty()) {
                        new SaveTracksTask(bandItemRepository).
                                executeOnExecutor(Executors.newSingleThreadExecutor(), trackDatas);
                    }
                    removeTask(taskQueryString);
                    if (isTasksEmpty()) {
                        Log.d(TAG, "final results map = " + resultsMap);
                        ArrayList albums = new ArrayList(resultsMap.values());
                        Collections.sort(albums);
                        albumsLiveDataObservable.setValue(albums);
                        isRefreshingObservable.setValue(false);
                    }
                }
            });
            return null;
        }

    }
}
