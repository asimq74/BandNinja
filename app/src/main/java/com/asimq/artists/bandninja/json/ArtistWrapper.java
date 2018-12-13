package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class ArtistWrapper {
    @SerializedName("artist")
    private Artist artist;

    public Artist getArtist ()
    {
        return artist;
    }

    public void setArtist (Artist artist)
    {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "ArtistWrapper{" +
                "artist=" + artist +
                '}';
    }
}
