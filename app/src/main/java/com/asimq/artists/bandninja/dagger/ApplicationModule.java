package com.asimq.artists.bandninja.dagger;

import javax.inject.Singleton;

import android.app.Application;
import android.content.Context;

import com.asimq.artists.bandninja.repositories.SearchResultsModelRepositoryDao;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 Module that provides application resources
 *
 * @author Asim Qureshi
 */
@Module
public class ApplicationModule {

	private final Application mApplication;

	public ApplicationModule(Application app) {
		mApplication = app;
	}

	@Provides
	public Application provideApplication() {
		return mApplication;
	}

	@Provides
	public Context provideContext() {
		return mApplication;
	}

	@Provides
	@Singleton
	SearchResultsViewModelFactory provideSearchResultsViewModelFactory(SearchResultsRepository searchResultsRepository) {
		return new SearchResultsViewModelFactory(mApplication, searchResultsRepository);
	}

	@Provides
	@Singleton
	public SearchResultsRepository searchResultsRepository() {
		return new SearchResultsModelRepositoryDao();
	}

}
