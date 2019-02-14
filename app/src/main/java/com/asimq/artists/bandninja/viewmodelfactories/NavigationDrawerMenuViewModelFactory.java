package com.asimq.artists.bandninja.viewmodelfactories;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.repositories.NavigationDrawerMenuRepository;
import com.asimq.artists.bandninja.viewmodels.NavigationDrawerMenuViewModel;

@Singleton
public class NavigationDrawerMenuViewModelFactory implements ViewModelProvider.Factory {

	private final Application application;
	private final NavigationDrawerMenuRepository navigationDrawerMenuRepository;

	@Inject
	public NavigationDrawerMenuViewModelFactory(@NonNull Application application,
			@NonNull NavigationDrawerMenuRepository navigationDrawerMenuRepository) {
		this.application = application;
		this.navigationDrawerMenuRepository = navigationDrawerMenuRepository;
	}

	@Override
	public NavigationDrawerMenuViewModel create(@NonNull Class modelClass) {
		return new NavigationDrawerMenuViewModel(application, navigationDrawerMenuRepository);
	}
}
