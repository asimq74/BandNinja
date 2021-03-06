package com.asimq.artists.bandninja.dagger;

import javax.inject.Singleton;

import android.app.Application;
import android.content.Context;

import com.asimq.artists.bandninja.ArticleDetailActivity;
import com.asimq.artists.bandninja.DetailsActivity;
import com.asimq.artists.bandninja.MainActivity;
import com.asimq.artists.bandninja.MusicItemsListFragment;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.asynctasks.albums.FetchAllSavedAlbumDataTask;
import com.asimq.artists.bandninja.asynctasks.albums.UpdateAllAlbumsAndTracksTask;
import com.asimq.artists.bandninja.asynctasks.artists.FetchAllSavedArtistDataTask;
import com.asimq.artists.bandninja.asynctasks.artists.UpdateAllArtistsTask;
import com.asimq.artists.bandninja.asynctasks.tags.UpdateAllTagsTask;
import com.asimq.artists.bandninja.repositories.SearchResultsModelRepositoryDao;
import com.asimq.artists.bandninja.service.DataSyncJobService;
import com.asimq.artists.bandninja.ui.CustomMultiSelectListPreference;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.ArtistDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;
import com.asimq.artists.bandninja.widget.TopArtistsAppWidgetRemoteViewsFactory;

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

	void inject(MainActivity mainActivity);

	void inject(MusicItemsListFragment musicItemsListFragment);

	void inject(SearchResultsViewModel searchResultsViewModel);

	void inject(AlbumDetailViewModel albumDetailViewModel);

	void inject(ArtistDetailViewModel artistDetailViewModel);

	void inject(DataSyncJobService dataSyncJobService);

	void inject(CustomMultiSelectListPreference customMultiSelectListPreference);

	void inject(ArticleDetailActivity articleDetailActivity);

	void inject(TopArtistsAppWidgetRemoteViewsFactory topArtistsAppWidgetRemoteViewsFactory);

	void inject(SearchResultsModelRepositoryDao searchResultsModelRepositoryDao);

	void inject(FetchAllSavedAlbumDataTask fetchAllSavedAlbumDataTask);

	void inject(DetailsActivity detailsActivity);

	void inject(UpdateAllArtistsTask updateAllArtistsTask);

	void inject(UpdateAllAlbumsAndTracksTask updateAllAlbumsAndTracksTask);

	void inject(UpdateAllTagsTask updateAllTagsTask);

	void inject(FetchAllSavedArtistDataTask fetchAllSavedArtistDataTask);
}
