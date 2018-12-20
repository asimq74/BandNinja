package com.asimq.artists.bandninja.asynctasks;

import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.NavigationListener;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

public class SaveArtistTask extends BaseSaveArtistTask {

    private final NavigationListener listener;

    public SaveArtistTask(@NonNull ArtistDataDao artistDataDao, NavigationListener listener) {
        super(artistDataDao);
        this.listener = listener;
    }


    @Override
    protected void onPostExecute(String output) {
        super.onPostExecute(output);
        listener.navigateTo(output);
    }
}
