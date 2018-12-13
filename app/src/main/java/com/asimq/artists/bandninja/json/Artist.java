package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Artist {
    @SerializedName("listeners")
    private String listeners;
    @SerializedName("mbid")
    private String mbid;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private List<Image> images;
    @SerializedName("streamable")
    private String streamable;
    @SerializedName("url")
    private String url;
    @SerializedName("tagWrapper")
    @Expose
    private TagWrapper tagWrapper;
    @SerializedName("ontour")
    private String ontour;
    @SerializedName("bio")
    @Expose
    private Bio bio;
    @SerializedName("stats")
    @Expose
    private Stats stats;
    @SerializedName("similar")
    @Expose
    private Similar similar;

    public Similar getSimilar() {
        return similar;
    }

    public void setSimilar(Similar similar) {
        this.similar = similar;
    }

    public TagWrapper getTagWrapper() {
        return tagWrapper;
    }

    public void setTagWrapper(TagWrapper tagWrapper) {
        this.tagWrapper = tagWrapper;
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
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
                ", images=" + images +
                ", streamable='" + streamable + '\'' +
                ", url='" + url + '\'' +
                ", tagWrapper=" + tagWrapper +
                ", ontour='" + ontour + '\'' +
                ", bio=" + bio +
                ", stats=" + stats +
                ", similar=" + similar +
                '}';
    }
}
