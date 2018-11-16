package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Similar {
    @SerializedName("artist")
    private Artist[] artists;

    public Artist[] getArtists()
    {
        return artists;
    }

    public void setArtists(Artist[] artists)
    {
        this.artists = artists;
    }

    @Override
    public String toString() {
        return "Similar{" +
                "artists=" + Arrays.toString(artists) +
                '}';
    }
}
