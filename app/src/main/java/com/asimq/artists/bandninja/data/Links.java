package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

public class Links
{
    @SerializedName("link")
    private Link link;

    public Link getLink ()
    {
        return link;
    }

    public void setLink (Link link)
    {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Links{" +
                "link=" + link +
                '}';
    }
}
