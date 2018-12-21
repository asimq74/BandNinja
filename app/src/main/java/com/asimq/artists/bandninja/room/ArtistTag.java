package com.asimq.artists.bandninja.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Tag;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "artistTags",
        indices = @Index("artistDataMbId"),
        foreignKeys = @ForeignKey(entity = ArtistData.class,
        parentColumns = "mbid",
        childColumns = "artistDataMbId",
        onDelete = CASCADE))
public class ArtistTag {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "artistDataMbId")
    public final String artistDataMbId;

    public ArtistTag(String name, String url, String artistDataMbId) {
        this.name = name;
        this.url = url;
        this.artistDataMbId = artistDataMbId;
    }

    public static List<ArtistTag> getTags(Artist artist) {
        List<ArtistTag> artistTags = new ArrayList<>();
        for (Tag tag : artist.getTagWrapper().getTags()) {
            ArtistTag artistTag = new ArtistTag(tag.getName(), tag.getUrl(), artist.getMbid());
            artistTags.add(artistTag);
        }
        return artistTags;
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

    public String getArtistDataMbId() {
        return artistDataMbId;
    }

    @Override
    public String toString() {
        return "ArtistTag{" +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", artistDataMbId=" + artistDataMbId +
                '}';
    }
}
