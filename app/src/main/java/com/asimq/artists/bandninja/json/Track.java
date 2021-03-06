package com.asimq.artists.bandninja.json;

import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.room.TrackData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Track {

    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("url")
    @Expose
    private String url = "";
    @SerializedName("duration")
    @Expose
    private String duration = "";
    @SerializedName("artist")
    @Expose
    private Artist artist = new Artist();

    public Track() {
    }

    public Track(@NonNull TrackData trackData) {
        this.artist.setName(trackData.getArtistName());
        this.duration = trackData.getDuration();
        this.name = trackData.getName();
        this.url = trackData.getUrl();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Track {" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", duration='" + duration + '\'' +
                ", artist='" + artist + '\'' +
                "}\n";
    }
}
