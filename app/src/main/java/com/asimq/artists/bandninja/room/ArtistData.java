package com.asimq.artists.bandninja.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.utils.Util;

@Entity(tableName = "artists", primaryKeys = {"name", "mbid"})
public class ArtistData {

	@ColumnInfo(name = "bio")
	private String bio = "";
	@ColumnInfo(name = "image")
	private String image = "";
	@ColumnInfo(name = "listeners")
	private int listeners = 0;
	@NonNull
	@ColumnInfo(name = "mbid")
	private String mbid = "";
	@NonNull
	@ColumnInfo(name = "name")
	private String name = "";
	@ColumnInfo(name = "tagsText")
	private String tagsText = "";

	public ArtistData() {
	}

	public ArtistData(@NonNull Artist artist) {
		this.mbid = artist.getMbid();
		this.name = artist.getName();
		this.image = Util.getImageUrl(artist);
		this.bio = artist.getBio().getContent();
		this.tagsText = Util.getTagsAsString(artist.getTagWrapper().getTags());
		this.listeners = Util.stringToInt(artist.getStats().getListeners());
	}

	public String getBio() {
		return bio;
	}

	public String getImage() {
		return image;
	}

	public int getListeners() {
		return listeners;
	}

	@NonNull
	public String getMbid() {
		return mbid;
	}

	public String getName() {
		return name;
	}

	public String getTagsText() {
		return tagsText;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setListeners(int listeners) {
		this.listeners = listeners;
	}

	public void setMbid(@NonNull String mbid) {
		this.mbid = mbid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTagsText(String tagsText) {
		this.tagsText = tagsText;
	}

	@Override
	public String toString() {
		return "ArtistData {" +
				"mbid='" + mbid + '\'' +
				", name='" + name + '\'' +
				", images=" + image +
				", tagsText=" + tagsText +
				", listeners=" + listeners +
				", bio=" + bio +
				'}';
	}
}
