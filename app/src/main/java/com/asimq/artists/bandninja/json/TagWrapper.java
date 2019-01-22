package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TagWrapper {
    @SerializedName("tag")
    @Expose
    private List<Tag> tags = new ArrayList<>();

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
        return "TagWrapper{" +
                "tags=" + tags.toString() +
                '}';
    }
}
