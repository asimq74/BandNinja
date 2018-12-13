package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class TopAlbumsWrapper {
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
        return "TopAlbumsWrapper{" +
                "\n\ttopalbums=" + topalbums +
                '}';
    }
}
