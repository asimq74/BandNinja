package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArtistMatches
{
    @SerializedName("artist")
    private List<Artist> artists;

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
        return "ArtistMatches{" +
                "artists=" + artists +
                '}';
    }
}
