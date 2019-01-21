package com.asimq.artists.bandninja.json;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TopArtists
{
    @SerializedName("artist")
    @Expose
    private List<Artist> artists = null;


    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtist(List<Artist> artists) {
        this.artists = artists;
    }


    @Override
    public String toString() {
        return "TopArtists{" +
                "artists=" + artists +
                '}';
    }
}
