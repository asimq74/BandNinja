package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class Bio
{
    @SerializedName("content")
    private String content = "";
    @SerializedName("summary")
    private String summary = "";
    @SerializedName("links")
    private LinkWrapper linkWrapper = new LinkWrapper();
    @SerializedName("published")
    private String published = "";

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

    public LinkWrapper getLinkWrapper()
    {
        return linkWrapper;
    }

    public void setLinkWrapper(LinkWrapper linkWrapper)
    {
        this.linkWrapper = linkWrapper;
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
                ", linkWrapper=" + linkWrapper +
                ", published='" + published + '\'' +
                '}';
    }
}
