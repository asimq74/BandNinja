package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class ResultsWrapper
{
    @SerializedName("results")
    private Result result = new Result();

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
        return "ResultsWrapper{" +
                "result=" + result +
                '}';
    }
}
