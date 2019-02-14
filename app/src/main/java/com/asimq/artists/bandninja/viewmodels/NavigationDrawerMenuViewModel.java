package com.asimq.artists.bandninja.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.NavigationDrawerMenuRepository;

public class NavigationDrawerMenuViewModel extends AndroidViewModel {

	private final NavigationDrawerMenuRepository navigationDrawerMenuRepository;

	public NavigationDrawerMenuViewModel(@NonNull Application application, @NonNull NavigationDrawerMenuRepository navigationDrawerMenuRepository) {
		super(application);
		this.navigationDrawerMenuRepository = navigationDrawerMenuRepository;
	}

	public void addDataSource(LiveData<Boolean> data) {
		navigationDrawerMenuRepository.addDataSource(data);
	}

	public void removeDataSource(LiveData<Boolean> data) {
		navigationDrawerMenuRepository.removeDataSource(data);
	}

	public LiveData<Boolean> shouldRefreshNavigationDrawerObservable() {
		return navigationDrawerMenuRepository.shouldRefreshNavigationDrawerObservable();
	}
}
