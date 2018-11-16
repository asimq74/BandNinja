package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Tags {
    @SerializedName("tags")
    private Tag[] tags;

    public Tag[] getTags()
    {
        return tags;
    }

    public void setTags(Tag[] tags)
    {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Tags{" +
                "tags=" + Arrays.toString(tags) +
                '}';
    }
}
