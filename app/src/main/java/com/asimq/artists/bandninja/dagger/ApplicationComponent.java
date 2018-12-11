package com.asimq.artists.bandninja.dagger;

import javax.inject.Singleton;

import android.app.Application;
import android.content.Context;

import com.asimq.artists.bandninja.MainNavigationActivity;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;

import dagger.Component;

/**
 * Provides application resources module
 *
 * @author Asim Qureshi
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

	Application application();

	Context context();

	void inject(MyApplication application);

	void inject(MainNavigationActivity mainNavigationActivity);

	void inject(SearchResultsViewModel searchResultsViewModel);

}