package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class TopAlbumsWrapper {
    @SerializedName("topalbums")
    private TopAlbums topAlbums = new TopAlbums();

    public TopAlbums getTopAlbums()
    {
        return topAlbums;
    }

    public void setTopAlbums(TopAlbums topAlbums)
    {
        this.topAlbums = topAlbums;
    }

    @Override
    public String toString() {
        return "TopAlbumsWrapper{" +
                "\n\ttopAlbums=" + topAlbums +
                '}';
    }
}
