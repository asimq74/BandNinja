package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class TopAlbums {
    @SerializedName("album")
    private Album[] albums;

    public Album[] getAlbums()
    {
        return albums;
    }

    public void setAlbums(Album[] albums)
    {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "\n\tTopAlbums{" +
                "\n\talbums=" + Arrays.toString(albums) +
                '}';
    }
}
