package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Album
{

    @SerializedName("mbid")
    private String mbid;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private Image[] images;
    @SerializedName("playcount")
    private String playcount;
    @SerializedName("artist")
    private Artist artist;
    @SerializedName("url")
    private String url;

    public String getMbid ()
    {
        return mbid;
    }

    public void setMbid (String mbid)
    {
        this.mbid = mbid;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Image[] getImages()
    {
        return images;
    }

    public void setImages(Image[] images)
    {
        this.images = images;
    }

    public String getPlaycount ()
    {
        return playcount;
    }

    public void setPlaycount (String playcount)
    {
        this.playcount = playcount;
    }

    public Artist getArtist ()
    {
        return artist;
    }

    public void setArtist (Artist artist)
    {
        this.artist = artist;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Album{" +
                "mbid='" + mbid + '\'' +
                ", name='" + name + '\'' +
                ", images=" + Arrays.toString(images) +
                ", playcount='" + playcount + '\'' +
                ", artist=" + artist +
                ", url='" + url + '\'' +
                '}';
    }
}
