package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Artist {
    @SerializedName("listeners")
    private String listeners;
    @SerializedName("mbid")
    private String mbid;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private Image[] images;
    @SerializedName("streamable")
    private String streamable;
    @SerializedName("url")
    private String url;
    @SerializedName("tags")
    private Tags tags;
    @SerializedName("ontour")
    private String ontour;
    @SerializedName("bio")
    private Bio bio;
    @SerializedName("stats")
    private Stats stats;
    @SerializedName("similar")
    private Similar similar;

    public Similar getSimilar() {
        return similar;
    }

    public void setSimilar(Similar similar) {
        this.similar = similar;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public Bio getBio() {
        return bio;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public String getOntour() {
        return ontour;
    }

    public void setOntour(String ontour) {
        this.ontour = ontour;
    }

    public String getListeners() {
        return listeners;
    }

    public void setListeners(String listeners) {
        this.listeners = listeners;
    }

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public String getStreamable() {
        return streamable;
    }

    public void setStreamable(String streamable) {
        this.streamable = streamable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "listeners='" + listeners + '\'' +
                ", mbid='" + mbid + '\'' +
                ", name='" + name + '\'' +
                ", images=" + Arrays.toString(images) +
                ", streamable='" + streamable + '\'' +
                ", url='" + url + '\'' +
                ", tags=" + tags +
                ", ontour='" + ontour + '\'' +
                ", bio=" + bio +
                ", stats=" + stats +
                ", similar=" + similar +
                '}';
    }
}
