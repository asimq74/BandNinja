package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

public class Bio
{
    @SerializedName("content")
    private String content;
    @SerializedName("summary")
    private String summary;
    @SerializedName("links")
    private Links links;
    @SerializedName("published")
    private String published;

    public String getContent ()
    {
        return content;
    }

    public void setContent (String content)
    {
        this.content = content;
    }

    public String getSummary ()
    {
        return summary;
    }

    public void setSummary (String summary)
    {
        this.summary = summary;
    }

    public Links getLinks ()
    {
        return links;
    }

    public void setLinks (Links links)
    {
        this.links = links;
    }

    public String getPublished ()
    {
        return published;
    }

    public void setPublished (String published)
    {
        this.published = published;
    }

    @Override
    public String toString() {
        return "Bio{" +
                "content='" + content + '\'' +
                ", summary='" + summary + '\'' +
                ", links=" + links +
                ", published='" + published + '\'' +
                '}';
    }
}
