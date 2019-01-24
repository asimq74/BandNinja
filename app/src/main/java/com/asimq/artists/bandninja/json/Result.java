package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class Result
{
    @SerializedName("artistmatches")
    private ArtistMatches artistmatches = new ArtistMatches();

    public ArtistMatches getArtistmatches ()
    {
        return artistmatches;
    }

    public void setArtistmatches (ArtistMatches artistmatches)
    {
        this.artistmatches = artistmatches;
    }

    @Override
    public String toString() {
        return "Result{" +
                "artistmatches=" + artistmatches +
                '}';
    }
}
