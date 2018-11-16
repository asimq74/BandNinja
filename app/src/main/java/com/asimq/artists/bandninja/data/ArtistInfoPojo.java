package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

public class ArtistInfoPojo {
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
        return "ArtistInfoPojo{" +
                "artist=" + artist +
                '}';
    }
}
