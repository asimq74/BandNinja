package com.asimq.artists.bandninja.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.AlbumInfoWrapper;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TopTagsWrapper;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TagData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.TagDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;
import com.asimq.artists.bandninja.utils.Util.ServiceStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BandDataSyncAsyncTask extends AsyncTask<Void, Void, ServiceStatus> {

	class UpdateAlbumTask extends AsyncTask<Void, Void, List<AlbumData>> {

		@Override
		protected List<AlbumData> doInBackground(Void... params) {
			List<AlbumData> albumDatas = new ArrayList<>();
			albumDatas = albumDataDao.fetchAllAlbumDatas();
			for (AlbumData albumData : albumDatas) {
				downloadAlbumInfoToStorage(albumData.getArtist(), albumData.getName());
			}
			return albumDatas;
		}

		@Override
		protected void onPostExecute(List<AlbumData> albumData) {
			super.onPostExecute(albumData);
			Log.d(TAG, "update album task finished! " + albumData.size() + " albums counted after download");
		}
	}

	class UpdateArtistTask extends AsyncTask<Void, Void, List<ArtistData>> {

		@Override
		protected List<ArtistData> doInBackground(Void... params) {
			List<ArtistData> artistDatas = artistDataDao.fetchAllArtistDatas();
			for (ArtistData artistData : artistDatas) {
				downloadArtistInfoToStorage(artistData.getName());
			}
			return artistDatas;
		}

		@Override
		protected void onPostExecute(List<ArtistData> artistData) {
			super.onPostExecute(artistData);
			Log.d(TAG, "update artist task finished! " + artistData.size() + " artists counted after download");
		}
	}

	class UpdateTagsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			downloadTagDataToStorage();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Log.d(TAG, "update tags task finished!");
		}
	}

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	private static final String TAG = BandDataSyncAsyncTask.class.getSimpleName();
	@Inject
	AlbumDataDao albumDataDao;
	@Inject
	ArtistDataDao artistDataDao;
	@Inject
	BandItemRepository bandItemRepository;
	@Inject
	TagDataDao tagDataDao;
	@Inject
	TrackDataDao trackDataDao;

	public BandDataSyncAsyncTask(ApplicationComponent applicationComponent) {
		applicationComponent.inject(this);
	}

	@Override
	protected ServiceStatus doInBackground(Void... voids) {
		doWork();
		return ServiceStatus.SUCCESS;
	}

	private void doWork() {
		new UpdateArtistTask().executeOnExecutor(Executors.newSingleThreadExecutor());
		new UpdateAlbumTask().executeOnExecutor(Executors.newSingleThreadExecutor());
		new UpdateTagsTask().executeOnExecutor(Executors.newSingleThreadExecutor());
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
				albumDataDao.insertAlbumData(albumData);
				for (Track track : albumInfo.getTrackWrapper().getTracks()) {
					TrackData trackData = new TrackData(track);
					trackData.setAlbumName(albumInfo.getName());
					trackData.setAlbumId(albumInfo.getMbid());
					trackDataDao.insertTrackData(trackData);
					Log.d(TAG, String.format("updated track Info for %s %s", trackData.getName(), albumData.getName()));
				}
				Log.d(TAG, String.format("updated album info for %s %s", albumData.getName(), albumData.getMbid()));
			}
		});

	}

	private void downloadArtistInfoToStorage(@NonNull String artistName) {
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<ArtistWrapper> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
				API_KEY, DEFAULT_FORMAT);
		artistInfoCall.enqueue(new Callback<ArtistWrapper>() {
			@Override
			public void onFailure(Call<ArtistWrapper> call, Throwable t) {
				Log.e(TAG, "get artist info for " + artistName + " failed.");
			}

			@Override
			public void onResponse(Call<ArtistWrapper> call, Response<ArtistWrapper> response) {
				final ArtistWrapper artistWrapper = response.body();
				if (artistWrapper == null) {
					Log.e(TAG, "get artist info for " + artistName + " failed. no data was returned");
					return;
				}
				final ArtistData artistData = new ArtistData(artistWrapper.getArtist());
				artistDataDao.insertArtist(artistData);
				Log.d(TAG, String.format("updated artist info for %s %s", artistData.getName(), artistData.getMbid()));
			}
		});

	}

	private void downloadTagDataToStorage() {
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<TopTagsWrapper> artistInfoCall = service.getTopTags("tag.getTopTags", API_KEY, DEFAULT_FORMAT);
		artistInfoCall.enqueue(new Callback<TopTagsWrapper>() {
			@Override
			public void onFailure(Call<TopTagsWrapper> call, Throwable t) {
				Log.e(TAG, "get top tags failed.");
			}

			@Override
			public void onResponse(Call<TopTagsWrapper> call, Response<TopTagsWrapper> response) {
				final TopTagsWrapper topTagsWrapper = response.body();
				if (topTagsWrapper == null) {
					Log.e(TAG, "get top tags failed - no data was returned.");
					return;
				}
				for (Tag tag : topTagsWrapper.getToptags().getTags()) {
					final TagData tagData = new TagData(tag);
					tagDataDao.insertTagData(tagData);
					Log.d(TAG, String.format("updated tag info for %s", tag.getName()));
				}
			}
		});

	}

	@Override
	protected void onPostExecute(ServiceStatus serviceStatus) {
		super.onPostExecute(serviceStatus);
		if (serviceStatus == ServiceStatus.FAILURE) {
//			final String failureMessage = context.getString(R.string.updateDataServiceFailedCheckConnection);
//			Log.e(TAG, failureMessage);
//			Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show();
			return;
		}
//		Log.d(TAG, context.getString(R.string.updateDataServiceSuccessful));
	}
}