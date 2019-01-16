package com.asimq.artists.bandninja.json;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackWrapper {

    @SerializedName("track")
    @Expose
    private List<Track> tracks = new ArrayList<Track>();

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
    @Override
    public String toString() {
        return "TrackWrapper {" +
                "tracks='" + tracks + '\'' +
                "}\n";
    }

}
