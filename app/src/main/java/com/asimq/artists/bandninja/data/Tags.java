package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class Tags {
    @SerializedName("tag")
    @Expose
    private List<Tag> tags;

    public List<Tag> getTags()
    {
        return tags;
    }

    public void setTags(List<Tag> tags)
    {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Tags{" +
                "tags=" + tags.toString() +
                '}';
    }
}
