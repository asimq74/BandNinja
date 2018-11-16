package com.asimq.artists.bandninja.data;

import com.google.gson.annotations.SerializedName;

public class Stats {
    @SerializedName("listeners")
    private String listeners;
    @SerializedName("playcount")
    private String playcount;

    public String getListeners() {
        return listeners;
    }

    public void setListeners(String listeners) {
        this.listeners = listeners;
    }

    public String getPlaycount() {
        return playcount;
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "listeners='" + listeners + '\'' +
                ", playcount='" + playcount + '\'' +
                '}';
    }
}
