package com.asimq.artists.bandninja.asynctasks.main;

import java.util.concurrent.Executors;

import android.os.AsyncTask;

import com.asimq.artists.bandninja.asynctasks.albums.UpdateAllAlbumsAndTracksTask;
import com.asimq.artists.bandninja.asynctasks.artists.UpdateAllArtistsTask;
import com.asimq.artists.bandninja.asynctasks.tags.UpdateAllTagsTask;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;

public class RefreshBandDataSyncAsyncTask extends AsyncTask<Void, Void, Void> {

	private final ApplicationComponent applicationComponent;

	public RefreshBandDataSyncAsyncTask(ApplicationComponent applicationComponent) {
		this.applicationComponent = applicationComponent;
	}

	@Override
	protected Void doInBackground(Void... voids) {
		doWork();
		return null;
	}

	private void doWork() {
		new UpdateAllArtistsTask(applicationComponent).executeOnExecutor(Executors.newSingleThreadExecutor());
		new UpdateAllAlbumsAndTracksTask(applicationComponent).executeOnExecutor(Executors.newSingleThreadExecutor());
		new UpdateAllTagsTask(applicationComponent).executeOnExecutor(Executors.newSingleThreadExecutor());
	}

}