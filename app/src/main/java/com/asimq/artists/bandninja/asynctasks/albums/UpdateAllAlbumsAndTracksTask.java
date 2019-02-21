package com.asimq.artists.bandninja.asynctasks.albums;

import java.util.List;

import javax.inject.Inject;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.AlbumInfoWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.TrackData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAllAlbumsAndTracksTask extends AsyncTask<Void, Void, List<AlbumData>> {

	private static final String API_KEY = BuildConfig.LastFMApiKey;
	private static final String DEFAULT_FORMAT = "json";
	private final String TAG = this.getClass().getSimpleName();
	@Inject
	BandItemRepository bandItemRepository;

	public UpdateAllAlbumsAndTracksTask(ApplicationComponent applicationComponent) {
		applicationComponent.inject(this);
	}

	@Override
	protected List<AlbumData> doInBackground(Void... params) {
		List<AlbumData> albumDatas = bandItemRepository.getAllAlbumDatas();
		for (AlbumData albumData : albumDatas) {
			downloadAlbumInfoToStorage(albumData.getArtist(), albumData.getName());
		}
		return albumDatas;
	}

	private void downloadAlbumInfoToStorage(@NonNull String artistName, @NonNull String albumName) {
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<AlbumInfoWrapper> albumInfoCall = service.getAlbumInfo("album.getinfo", artistName, albumName,
				API_KEY, DEFAULT_FORMAT);
		albumInfoCall.enqueue(new Callback<AlbumInfoWrapper>() {
			@Override
			public void onFailure(Call<AlbumInfoWrapper> call, Throwable t) {
				Log.e(TAG, String.format("get album info for %s - %s failed", artistName, albumName));
			}

			@Override
			public void onResponse(Call<AlbumInfoWrapper> call, Response<AlbumInfoWrapper> response) {
				final AlbumInfoWrapper albumInfoWrapper = response.body();
				if (albumInfoWrapper == null) {
					Log.e(TAG, String.format("get album info for %s - %s failed - no data was returned", artistName, albumName));
					return;
				}
				final AlbumInfo albumInfo = albumInfoWrapper.getAlbumInfo();
				final AlbumData albumData = new AlbumData(albumInfo);
				bandItemRepository.insertAlbumWithTracks(albumData);
				Log.d(TAG, String.format("updated album info for %s %s", albumData.getName(), albumData.getMbid()));
				for (TrackData trackData : albumData.getTrackDatas()) {
					Log.d(TAG, String.format("updated track Info for %s %s", albumData.getName(), trackData.getName()));
				}
			}
		});

	}

	@Override
	protected void onPostExecute(List<AlbumData> albumData) {
		super.onPostExecute(albumData);
		Log.d(TAG, "update album task finished! " + albumData.size() + " albums counted after download");
	}

}
