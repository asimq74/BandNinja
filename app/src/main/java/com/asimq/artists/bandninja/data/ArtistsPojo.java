package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

public class ArtistsPojo
{
    @SerializedName("results")
    private Result result;

    public Result getResult ()
    {
        return result;
    }

    public void setResults (Result results)
    {
        this.result = results;
    }

    @Override
    public String toString() {
        return "ArtistsPojo{" +
                "result=" + result +
                '}';
    }
}
