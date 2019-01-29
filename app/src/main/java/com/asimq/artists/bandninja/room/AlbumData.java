package com.asimq.artists.bandninja.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Wiki;
import com.asimq.artists.bandninja.utils.Util;

@Entity(tableName = "albums", primaryKeys = {"name", "mbid"})
public class AlbumData implements Comparable<AlbumData> {

    @ColumnInfo(name = "artist")
    private String artist = "";
    @ColumnInfo(name = "image")
    private String image = "";
    @NonNull
    @ColumnInfo(name = "mbid")
    private String mbid = "";
    @NonNull
    @ColumnInfo(name = "name")
    private String name = "";
    @ColumnInfo(name = "playcount")
    private String playcount = "";
    @ColumnInfo(name = "releasedate")
    private String releaseDate = "";
    @ColumnInfo(name = "tags")
    private String tags = "";
    @ColumnInfo(name = "url")
    private String url = "";
    @ColumnInfo(name = "wiki")
    private String wiki = "";
    @ColumnInfo(name = "summary")
    private String summary = "";

    public AlbumData() {
    }


    public AlbumData(@NonNull AlbumInfo albumInfo) {
        this.artist = albumInfo.getArtist();
        this.image = Util.getImageUrl(albumInfo);
        this.mbid = albumInfo.getMbid();
        this.name = albumInfo.getName();
        this.playcount = albumInfo.getPlaycount();
        this.releaseDate = albumInfo.getReleaseDate();
        this.tags = Util.getTagsAsString(albumInfo.getTagWrapper().getTags());
        this.url = albumInfo.getUrl();
        this.wiki = getWiki(albumInfo).getContent();
        this.summary = getWiki(albumInfo).getSummary();
    }

    @Override
    public int compareTo(@NonNull AlbumData albumData) {
        int playcount = Util.stringToInt(albumData.getPlaycount());
        int playcount1 = Util.stringToInt(getPlaycount());
        return playcount - playcount1;
    }

    private Wiki getWiki(@NonNull AlbumInfo albumInfo) {
        return albumInfo.getWiki();
    }

    @Override
    public String toString() {
        return "AlbumData{" +
                "artist='" + artist + '\'' +
                ", image='" + image + '\'' +
                ", mbid='" + mbid + '\'' +
                ", name='" + name + '\'' +
                ", playcount='" + playcount + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", tags='" + tags + '\'' +
                ", url='" + url + '\'' +
                ", summary='" + summary + '\'' +
                ", wiki='" + wiki + '\'' +
                '}';
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @NonNull
    public String getMbid() {
        return mbid;
    }

    public void setMbid(@NonNull String mbid) {
        this.mbid = mbid;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPlaycount() {
        return playcount;
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }
}
