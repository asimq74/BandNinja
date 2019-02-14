package com.asimq.artists.bandninja.asynctasks.tags;

import javax.inject.Inject;

import android.os.AsyncTask;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TopTagsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.TagData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAllTagsTask extends AsyncTask<Void, Void, Void> {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	private final String TAG = this.getClass().getSimpleName();
	@Inject
	BandItemRepository bandItemRepository;

	public UpdateAllTagsTask(ApplicationComponent applicationComponent) {
		applicationComponent.inject(this);
	}

	@Override
	protected Void doInBackground(Void... params) {
		downloadTagDataToStorage();
		return null;
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
					bandItemRepository.saveTag(tagData);
					Log.d(TAG, String.format("updated tag info for %s", tag.getName()));
				}
			}
		});

	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		Log.d(TAG, "update tags task finished!");
	}
}