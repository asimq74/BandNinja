package com.asimq.artists.bandninja.repositories;

import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.ui.CustomMultiSelectListPreference;

public interface TagModelRepository {

	void searchForTags(@NonNull BandItemRepository bandItemRepository, @NonNull CustomMultiSelectListPreference customMultiSelectListPreference);
}
