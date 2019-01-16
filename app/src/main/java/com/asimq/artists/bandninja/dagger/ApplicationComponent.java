package com.asimq.artists.bandninja.dagger;

import javax.inject.Singleton;

import android.app.Application;
import android.content.Context;

import com.asimq.artists.bandninja.MainActivity;
import com.asimq.artists.bandninja.MainNavigationActivity;
import com.asimq.artists.bandninja.MusicItemsListFragment;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.ResultActivity;
import com.asimq.artists.bandninja.cards.SliderAdapter;
import com.asimq.artists.bandninja.service.DataSyncJobService;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.ArtistDetailViewModel;
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

	void inject(MainActivity mainActivity);

	void inject(MusicItemsListFragment musicItemsListFragment);

	void inject(ResultActivity resultActivity);

	void inject(SearchResultsViewModel searchResultsViewModel);

	void inject(AlbumDetailViewModel albumDetailViewModel);

	void inject(ArtistDetailViewModel artistDetailViewModel);

	void inject(DataSyncJobService dataSyncJobService);
}
