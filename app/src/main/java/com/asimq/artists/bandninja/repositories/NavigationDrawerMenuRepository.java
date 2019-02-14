package com.asimq.artists.bandninja.repositories;

import android.arch.lifecycle.LiveData;

public interface NavigationDrawerMenuRepository {

	void addDataSource(LiveData<Boolean> data);

	void removeDataSource(LiveData<Boolean> data);

	LiveData<Boolean> shouldRefreshNavigationDrawerObservable();
}
