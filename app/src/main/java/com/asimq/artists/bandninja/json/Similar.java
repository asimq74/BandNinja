package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Similar {
    @SerializedName("artist")
    private List<Artist> artists = new ArrayList<>();

    public List<Artist> getArtists()
    {
        return artists;
    }

    public void setArtists(List<Artist> artists)
    {
        this.artists = artists;
    }

    @Override
    public String toString() {
        return "Similar{" +
                "artists=" + artists +
                '}';
    }
}
