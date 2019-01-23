package com.asimq.artists.bandninja.asynctasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;

public class SaveAlbumDataTask extends AsyncTask<AlbumData, Void, AlbumData> {

    final String TAG = this.getClass().getSimpleName();
    private final AlbumDataDao albumDataDao;

    public SaveAlbumDataTask(@NonNull AlbumDataDao albumDataDao) {
        this.albumDataDao = albumDataDao;
    }

    @Override
    protected AlbumData doInBackground(AlbumData... params) {
        final AlbumData data = params[0];
        albumDataDao.insertAlbumData(data);
        return data;
    }

    @Override
    protected void onPostExecute(AlbumData output) {
        super.onPostExecute(output);
        Log.d(TAG, "inserted album: " + output);
    }
}
