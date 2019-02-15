package com.asimq.artists.bandninja.asynctasks.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import android.os.AsyncTask;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TopTagsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.TagData;
import com.asimq.artists.bandninja.ui.CustomMultiSelectListPreference;
import com.google.gson.internal.LinkedHashTreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagsFromServerTask extends AsyncTask<Void, Void, Void> {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	private static Map<String, Boolean> mapOfAttachmentTasks = new LinkedHashTreeMap<>();

	public static synchronized void addTask(String taskQueryString) {
		mapOfAttachmentTasks.put(taskQueryString, true);
	}

	public static synchronized boolean isTasksEmpty() {
		return mapOfAttachmentTasks.isEmpty();
	}

	public static synchronized void removeTask(String taskQueryString) {
		mapOfAttachmentTasks.remove(taskQueryString);
	}

	final String TAG = this.getClass().getSimpleName();
	private final BandItemRepository bandItemRepository;
	private final CustomMultiSelectListPreference customMultiSelectListPreference;

	public TagsFromServerTask(BandItemRepository bandItemRepository, CustomMultiSelectListPreference customMultiSelectListPreference) {
		this.bandItemRepository = bandItemRepository;
		this.customMultiSelectListPreference = customMultiSelectListPreference;

	}

	@Override
	protected Void doInBackground(Void... params) {
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Call<TopTagsWrapper> artistInfoCall = service.getTopTags("tag.getTopTags", API_KEY, DEFAULT_FORMAT);
		artistInfoCall.enqueue(new Callback<TopTagsWrapper>() {
			@Override
			public void onFailure(Call<TopTagsWrapper> call, Throwable t) {
			}

			@Override
			public void onResponse(Call<TopTagsWrapper> call, Response<TopTagsWrapper> response) {
				final TopTagsWrapper topTagsWrapper = response.body();
				List<Tag> tags = topTagsWrapper.getToptags().getTags();
				if (tags.isEmpty()) {
					return;
				}
				List<TagData> tagDatas = new ArrayList<>();
				for (Tag tag : tags) {
					tagDatas.add(new TagData(tag));
				}
				customMultiSelectListPreference.setEntries(tags);
				new SaveTagDataTask(bandItemRepository).executeOnExecutor(Executors.newSingleThreadExecutor(), tagDatas);
			}
		});
		return null;
	}
}
