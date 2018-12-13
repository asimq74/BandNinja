package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class LinkWrapper
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
        return "LinkWrapper{" +
                "link=" + link +
                '}';
    }
}
