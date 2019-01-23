package com.asimq.artists.bandninja.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Tag;

@Entity(tableName = "tags")
public class TagData {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "name")
    private String name = "";
    @ColumnInfo(name = "url")
    private String url = "";

    public TagData(@NonNull Tag tag) {
        this.name = tag.getName();
        this.url = tag.getUrl();
    }

    public TagData() {
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TagData{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
