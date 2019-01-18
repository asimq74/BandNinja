package com.asimq.artists.bandninja.repositories;

import java.util.ArrayList;
import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TopTagsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagModelRepositoryDao implements TagModelRepository {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";

	final String TAG = this.getClass().getSimpleName();

	static class TagsContainer {
		final List<Tag> tags;

		public TagsContainer(List<Tag> tags) {
			this.tags = tags;
		}

		public List<Tag> getTags() {
			return tags;
		}
	}

	@Override
	public List<Tag> getTopTags() {
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		final TagsContainer tagsContainer = new TagsContainer(new ArrayList<>());
		Call<TopTagsWrapper> artistInfoCall = service.getTopTags("tag.getTopTags", API_KEY, DEFAULT_FORMAT);
		artistInfoCall.enqueue(new Callback<TopTagsWrapper>() {
			@Override
			public void onFailure(Call<TopTagsWrapper> call, Throwable t) {
				return;
			}

			@Override
			public void onResponse(Call<TopTagsWrapper> call, Response<TopTagsWrapper> response) {
				final TopTagsWrapper topTagsWrapper = response.body();
				if (topTagsWrapper == null || topTagsWrapper.getToptags().getTags().isEmpty()) {
					return;
				}
				tagsContainer.getTags().addAll(topTagsWrapper.getToptags().getTags());
			}
		});
		return tagsContainer.getTags();
	}

	@Override
	public LiveData<List<Tag>> getTopTagsLiveData() {
		final GetMusicInfo service = RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		final MutableLiveData<List<Tag>> topTagsMutableLiveData = new MutableLiveData<>();
		Call<TopTagsWrapper> artistInfoCall = service.getTopTags("tag.getTopTags", API_KEY, DEFAULT_FORMAT);
		artistInfoCall.enqueue(new Callback<TopTagsWrapper>() {
			@Override
			public void onFailure(Call<TopTagsWrapper> call, Throwable t) {
				topTagsMutableLiveData.setValue(new ArrayList<>());
			}

			@Override
			public void onResponse(Call<TopTagsWrapper> call, Response<TopTagsWrapper> response) {
				final TopTagsWrapper topTagsWrapper = response.body();
				if (topTagsWrapper == null || topTagsWrapper.getToptags().getTags().isEmpty()) {
					return;
				}
				topTagsMutableLiveData.setValue(topTagsWrapper.getToptags().getTags());
			}
		});
		return topTagsMutableLiveData;
	}

}
