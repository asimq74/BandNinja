package com.asimq.artists.bandninja.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.utils.Util;

@Entity(tableName = "artists")
public class ArtistData {

	@ColumnInfo(name = "bio")
	private String bio = "";
	@ColumnInfo(name = "image")
	private String image = "";
	@NonNull
	@PrimaryKey
	@ColumnInfo(name = "mbid")
	private String mbid = "";
	@ColumnInfo(name = "name")
	private String name = "";

	public ArtistData() {
	}

	public ArtistData(@NonNull Artist artist) {
		this.mbid = artist.getMbid();
		this.name = artist.getName();
		this.image = Util.getImageUrl(artist);
		this.bio = artist.getBio().getSummary();
	}

	public String getBio() {
		return bio;
	}

	public String getImage() {
		return image;
	}

	@NonNull
	public String getMbid() {
		return mbid;
	}

	public String getName() {
		return name;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setMbid(@NonNull String mbid) {
		this.mbid = mbid;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ArtistData {" +
				"mbid='" + mbid + '\'' +
				", name='" + name + '\'' +
				", images=" + image +
				", bio=" + bio +
				'}';
	}
}
