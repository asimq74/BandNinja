package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopAlbums {
    @SerializedName("album")
    private List<Album> albums;

    public List<Album> getAlbums()
    {
        return albums;
    }

    public void setAlbums(List<Album> albums)
    {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "\n\tTopAlbums{" +
                "\n\talbums=" + albums +
                '}';
    }
}
