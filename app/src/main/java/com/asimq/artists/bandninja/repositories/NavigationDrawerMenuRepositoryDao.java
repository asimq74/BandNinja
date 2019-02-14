package com.asimq.artists.bandninja.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

public class NavigationDrawerMenuRepositoryDao implements NavigationDrawerMenuRepository {

	MediatorLiveData<Boolean> shouldRefreshNavigationDrawerObservable = new MediatorLiveData<>();

	public void addDataSource(LiveData<Boolean> data) {
		shouldRefreshNavigationDrawerObservable.addSource(data, shouldRefreshNavigationDrawerObservable::setValue);
	}

	public void removeDataSource(LiveData<Boolean> data) {
		shouldRefreshNavigationDrawerObservable.removeSource(data);
	}

	public LiveData<Boolean> shouldRefreshNavigationDrawerObservable() {
		return shouldRefreshNavigationDrawerObservable;
	}
}
