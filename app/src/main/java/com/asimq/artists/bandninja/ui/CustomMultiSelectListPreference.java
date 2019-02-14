package com.asimq.artists.bandninja.ui;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.asynctasks.tags.SaveTagDataTask;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TopTagsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.TagData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomMultiSelectListPreference extends MultiSelectListPreference {

    public static final String API_KEY = BuildConfig.LastFMApiKey;
    public static final String DEFAULT_FORMAT = "json";
    final String TAG = this.getClass().getSimpleName();

    @Inject
    BandItemRepository bandItemRepository;
    private ApplicationComponent applicationComponent;

    public CustomMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        final MyApplication application = (MyApplication) context.getApplicationContext();
        applicationComponent = application.getApplicationComponent();
        applicationComponent.inject(this);
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
                List<Tag> tags = topTagsWrapper.getToptags().getTags();
                if (tags.isEmpty()) {
                    return;
                }
                tagsContainer.getTags().addAll(tags);
                List<TagData> tagDatas = new ArrayList<>();
                for (Tag tag: tags) {
                    tagDatas.add(new TagData(tag));
                }
                new SaveTagDataTask(bandItemRepository).executeOnExecutor(Executors.newSingleThreadExecutor(), tagDatas);
                List<CharSequence> tagCharSequences = new ArrayList<>();
                for (Tag tag : tags) {
                    tagCharSequences.add(tag.getName());
                }
                String[] tagCharSequencesArray = new String[tagCharSequences.size()];
                tagCharSequencesArray = tagCharSequences.toArray(tagCharSequencesArray);
                setEntries(tagCharSequencesArray);
                setEntryValues(tagCharSequencesArray);
            }
        });
    }


    static class TagsContainer {
        final List<Tag> tags;

        public TagsContainer(List<Tag> tags) {
            this.tags = tags;
        }

        public List<Tag> getTags() {
            return tags;
        }
    }


}
