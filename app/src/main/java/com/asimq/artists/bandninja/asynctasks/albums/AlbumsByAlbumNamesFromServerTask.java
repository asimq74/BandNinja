package com.asimq.artists.bandninja.asynctasks.albums;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.TopAlbumsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.repositories.BandItemRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumsByAlbumNamesFromServerTask extends AsyncTask<String, Void, Void> {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	public static final int PAGES = 1;
	public static final int LIMIT = 10;

	final String TAG = this.getClass().getSimpleName();
	private List<AlbumInfo> albumInfos = new ArrayList<>();
	private final BandItemRepository bandItemRepository;
	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<AlbumInfo>> musicItemObservable;

	public AlbumsByAlbumNamesFromServerTask(BandItemRepository bandItemRepository,
			@NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull MediatorLiveData<List<AlbumInfo>> musicItemObservable) {
		this.bandItemRepository = bandItemRepository;
		this.isRefreshingObservable = isRefreshingObservable;
		this.musicItemObservable = musicItemObservable;
	}


	@Override
	protected Void doInBackground(String... params) {
		String artistName = params[0];
		Log.d(TAG, "onQueryTextSubmit: query->" + artistName);
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<TopAlbumsWrapper> topAlbumsWrapperCall
				= service.getTopAlbums("artist.getTopAlbums", artistName, API_KEY, DEFAULT_FORMAT, PAGES, LIMIT);
		topAlbumsWrapperCall.enqueue(new Callback<TopAlbumsWrapper>() {
				@Override
				public void onFailure(Call<TopAlbumsWrapper> call, Throwable t) {
					Log.d(TAG, String.format("failed to add albums from artist %s from server", artistName));
					new EmptyAlbumsProcessor(isRefreshingObservable, musicItemObservable).executeOnExecutor(Executors.newSingleThreadExecutor());
				}

				@Override
				public void onResponse(Call<TopAlbumsWrapper> call, Response<TopAlbumsWrapper> response) {
					final TopAlbumsWrapper topAlbumsWrapper = response.body();
					if (topAlbumsWrapper == null) {
						new EmptyAlbumsProcessor(isRefreshingObservable, musicItemObservable).executeOnExecutor(Executors.newSingleThreadExecutor());
						return;
					}
					final List<Album> albums = topAlbumsWrapper.getTopAlbums().getAlbums();
					if (albums.isEmpty()) {
						new EmptyAlbumsProcessor(isRefreshingObservable, musicItemObservable).executeOnExecutor(Executors.newSingleThreadExecutor());
						return;
					}
					for (Album album : albums) {
						final AlbumInfo albumInfo = new AlbumInfo(album);
						Log.d(TAG, String.format("adding album %s from server", album.getName()));
						albumInfos.add(albumInfo);
					}
					Log.d(TAG, String.format("attempting to obtain album infos from server for artist %s", artistName));
					new AlbumInfosByNamesFromServerTask(bandItemRepository,isRefreshingObservable, musicItemObservable)
							.executeOnExecutor(Executors.newSingleThreadExecutor(), albumInfos);
				}
			});

		return null;
	}
}
