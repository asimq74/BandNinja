package com.asimq.artists.bandninja.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Image;

@Entity(tableName = "artists")
public class ArtistData {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "mbid")
    private String mbid = "";
    @ColumnInfo(name = "name")
    private String name = "";
    @ColumnInfo(name = "image")
    private String image = "";
    @ColumnInfo(name = "bio")
    private String bio = "";

    public ArtistData() {
    }

    public ArtistData(@NonNull Artist artist) {
        this.mbid = artist.getMbid();
        this.name = artist.getName();
        this.image = getImageUrl(artist);
        this.bio = artist.getBio().getSummary();
    }

    private String getImageUrl(@NonNull Artist artist) {
        for (Image image : artist.getImages()) {
            if ("large".equals(image.getSize())) {
                return image.getText();
            }
        }
        return "";
    }

    @NonNull
    public String getMbid() {
        return mbid;
    }

    public void setMbid(@NonNull String mbid) {
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
