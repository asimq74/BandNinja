package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.SerializedName;

public class AlbumInfoWrapper {
    @SerializedName("album")
    private AlbumInfo albumInfo;

    public AlbumInfo getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(AlbumInfo albumInfo) {
        this.albumInfo = albumInfo;
    }

    @Override
    public String toString() {
        return "AlbumInfoWrapper {" +
                "albumInfo=" + albumInfo +
                '}';
    }
}
