package com.asimq.artists.bandninja.dagger;

import javax.inject.Singleton;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.asimq.artists.bandninja.repositories.AlbumInfoRepository;
import com.asimq.artists.bandninja.repositories.AlbumInfoRepositoryDao;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.repositories.SearchResultsModelRepositoryDao;
import com.asimq.artists.bandninja.repositories.SearchResultsRepository;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;
import com.asimq.artists.bandninja.room.database.BandItemDataSource;
import com.asimq.artists.bandninja.room.database.BandItemDatabase;
import com.asimq.artists.bandninja.viewmodelfactories.AlbumDetailViewModelFactory;
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
	public AlbumInfoRepository albumInfoRepository() {
		return new AlbumInfoRepositoryDao();
	}

	@Provides
	@Singleton
	public BandItemRepository bandItemRepository(ArtistDataDao artistDataDao, ArtistTagDao artistTagDao) {
		return new BandItemDataSource(artistDataDao, artistTagDao);
	}

	@Provides
	@Singleton
	AlbumDetailViewModelFactory provideAlbumDetailViewModelFactory(
			AlbumInfoRepository albumInfoRepository) {
		return new AlbumDetailViewModelFactory(mApplication, albumInfoRepository);
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
	public ArtistTagDao provideArtistTagDao(BandItemDatabase bandItemDatabase) {
		return bandItemDatabase.artistTagDao();
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
	SearchResultsViewModelFactory provideSearchResultsViewModelFactory(
			SearchResultsRepository searchResultsRepository, BandItemRepository bandItemRepository) {
		return new SearchResultsViewModelFactory(mApplication, searchResultsRepository, bandItemRepository);
	}

	@Provides
	@Singleton
	public SearchResultsRepository searchResultsRepository() {
		return new SearchResultsModelRepositoryDao();
	}

}
