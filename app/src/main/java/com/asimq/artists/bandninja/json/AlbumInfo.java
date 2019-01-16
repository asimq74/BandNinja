package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlbumInfo extends BaseMusicItem {

    @SerializedName("artist")
    private String artist;
    @SerializedName("image")
    private List<Image> images;
    @SerializedName("mbid")
    private String mbid;
    @SerializedName("name")
    private String name;
    @SerializedName("playcount")
    private String playcount;
    @SerializedName("url")
    private String url;
    @SerializedName("tracks")
    private TrackWrapper trackWrapper;
    @SerializedName("tags")
    private TagWrapper tagWrapper;
    @SerializedName("wiki")
    private Wiki wiki;

    public TrackWrapper getTrackWrapper() {
        return trackWrapper;
    }

    public void setTrackWrapper(TrackWrapper trackWrapper) {
        this.trackWrapper = trackWrapper;
    }

    public TagWrapper getTagWrapper() {
        return tagWrapper;
    }

    public void setTagWrapper(TagWrapper tagWrapper) {
        this.tagWrapper = tagWrapper;
    }

    public Wiki getWiki() {
        return wiki;
    }

    public void setWiki(Wiki wiki) {
        this.wiki = wiki;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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

    public String getPlaycount() {
        return playcount;
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Album{" +
                "mbid='" + mbid + '\'' +
                ", name='" + name + '\'' +
                ", artist=" + artist +
                ", images=" + images +
                ", playcount='" + playcount + '\'' +
                ", url='" + url + '\'' +
                ", tagWrapper='" + tagWrapper + '\'' +
                ", trackWrapper='" + trackWrapper + '\'' +
                ", wiki='" + wiki + '\'' +
                "}\n";
    }
}
