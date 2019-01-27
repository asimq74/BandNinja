package com.asimq.artists.bandninja.asynctasks;

public class AlbumArtistTuple {
    private final String artist;
    private final String album;

    public AlbumArtistTuple(String artist, String album) {
        this.artist = artist;
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }
}