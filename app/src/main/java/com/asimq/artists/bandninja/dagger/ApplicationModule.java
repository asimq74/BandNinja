package com.asimq.artists.bandninja.dagger;

import javax.inject.Singleton;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.repositories.SearchResultsModelRepositoryDao;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.database.BandItemDataSource;
import com.asimq.artists.bandninja.room.database.BandItemDatabase;
import com.asimq.artists.bandninja.viewmodelfactories.ArtistDetailViewModelFactory;
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

	private static final String DATABASE_NAME = "banditems_db";
	private BandItemDatabase bandItemDatabase;
	private final Application mApplication;

	public ApplicationModule(Application app) {
		mApplication = app;
		bandItemDatabase = Room.databaseBuilder(mApplication, BandItemDatabase.class, DATABASE_NAME)
				.fallbackToDestructiveMigration().build();
	}

	@Provides
	@Singleton
	public BandItemRepository bookItemRepository(ArtistDataDao artistDataDao) {
		return new BandItemDataSource(artistDataDao);
	}

	@Provides
	public Application provideApplication() {
		return mApplication;
	}

	@Provides
	@Singleton
	public ArtistDataDao provideArtistDataDao(BandItemDatabase bandItemDatabase) {
		return bandItemDatabase.artistDataDao();
	}

	@Provides
	@Singleton
	ArtistDetailViewModelFactory provideArtistDetailViewModelFactory(BandItemRepository bandItemRepository) {
		return new ArtistDetailViewModelFactory(mApplication, bandItemRepository);
	}

	@Provides
	@Singleton
	public BandItemDatabase provideBandItemDatabase() {
		return bandItemDatabase;
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
