package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

public class TopAlbumsPojo {
    @SerializedName("topalbums")
    private TopAlbums topalbums;

    public TopAlbums getTopalbums ()
    {
        return topalbums;
    }

    public void setTopalbums (TopAlbums topalbums)
    {
        this.topalbums = topalbums;
    }

    @Override
    public String toString() {
        return "TopAlbumsPojo{" +
                "\n\ttopalbums=" + topalbums +
                '}';
    }
}
