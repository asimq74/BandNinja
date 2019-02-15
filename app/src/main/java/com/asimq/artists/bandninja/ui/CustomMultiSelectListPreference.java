package com.asimq.artists.bandninja.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.repositories.TagModelRepository;

public class CustomMultiSelectListPreference extends MultiSelectListPreference {

	final String TAG = this.getClass().getSimpleName();
	@Inject
	BandItemRepository bandItemRepository;
	@Inject
	TagModelRepository tagModelRepository;

	public CustomMultiSelectListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		final MyApplication application = (MyApplication) context.getApplicationContext();
		ApplicationComponent applicationComponent = application.getApplicationComponent();
		applicationComponent.inject(this);
		tagModelRepository.searchForTags(bandItemRepository, this);
	}

	public void setEntries(List<Tag> tags) {
		List<CharSequence> tagCharSequences = new ArrayList<>();
		for (Tag tag : tags) {
			tagCharSequences.add(tag.getName());
		}
		String[] tagCharSequencesArray = new String[tagCharSequences.size()];
		tagCharSequencesArray = tagCharSequences.toArray(tagCharSequencesArray);
		setEntries(tagCharSequencesArray);
		setEntryValues(tagCharSequencesArray);
	}

}
