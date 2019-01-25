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
import com.asimq.artists.bandninja.repositories.TagModelRepository;
import com.asimq.artists.bandninja.repositories.TagModelRepositoryDao;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.TagDataDao;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;
import com.asimq.artists.bandninja.room.database.BandItemDataSource;
import com.asimq.artists.bandninja.room.database.BandItemDatabase;
import com.asimq.artists.bandninja.viewmodelfactories.AlbumDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.ArtistDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.TagDetailViewModelFactory;

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
	public BandItemRepository bandItemRepository(ArtistDataDao artistDataDao,
			TrackDataDao trackDataDao,
			AlbumDataDao albumDataDao,
			TagDataDao tagDataDao) {
		return new BandItemDataSource(artistDataDao, trackDataDao, albumDataDao, tagDataDao);
	}

	@Provides
	@Singleton
	public AlbumDataDao provideAlbumDataDao(BandItemDatabase bandItemDatabase) {
		return bandItemDatabase.albumDataDao();
	}

	@Provides
	@Singleton
	AlbumDetailViewModelFactory provideAlbumDetailViewModelFactory(
			AlbumInfoRepository albumInfoRepository, TrackDataDao trackDataDao,
			AlbumDataDao albumDataDao) {
		return new AlbumDetailViewModelFactory(mApplication, albumInfoRepository, trackDataDao, albumDataDao);
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
	SearchResultsViewModelFactory provideSearchResultsViewModelFactory(
			SearchResultsRepository searchResultsRepository,
			BandItemRepository bandItemRepository) {
		return new SearchResultsViewModelFactory(mApplication, searchResultsRepository, bandItemRepository);
	}

	@Provides
	@Singleton
	public TagDataDao provideTagDataDao(BandItemDatabase bandItemDatabase) {
		return bandItemDatabase.tagDataDao();
	}

	@Provides
	@Singleton
	TagDetailViewModelFactory provideTagDetailViewModelFactory(TagModelRepository tagModelRepository) {
		return new TagDetailViewModelFactory(mApplication, tagModelRepository);
	}

	@Provides
	@Singleton
	public TrackDataDao provideTrackDataDao(BandItemDatabase bandItemDatabase) {
		return bandItemDatabase.trackDataDao();
	}

	@Provides
	@Singleton
	public SearchResultsRepository searchResultsRepository() {
		return new SearchResultsModelRepositoryDao();
	}

	@Provides
	@Singleton
	public TagModelRepository tagModelRepository() {
		return new TagModelRepositoryDao();
	}

}
