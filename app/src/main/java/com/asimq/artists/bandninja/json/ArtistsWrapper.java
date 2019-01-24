package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistsWrapper
{
    @SerializedName("artists")
    @Expose
    private TopArtists topArtists = new TopArtists();

    public TopArtists getTopArtists() {
        return topArtists;
    }

    public void setTopArtists(TopArtists topArtists) {
        this.topArtists = topArtists;
    }

    @Override
    public String toString() {
        return "ArtistsWrapper{" +
                "topArtists=" + topArtists +
                '}';
    }
}
