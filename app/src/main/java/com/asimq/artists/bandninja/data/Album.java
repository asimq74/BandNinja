package com.asimq.artists.bandninja.data;

import java.util.Arrays;
import java.util.List;

import android.provider.MediaStore.Images;

import com.google.gson.annotations.SerializedName;

public class Album {

	@SerializedName("artist")
	private Artist artist;
	@SerializedName("image")
	private List<Images> images;
	@SerializedName("mbid")
	private String mbid;
	@SerializedName("name")
	private String name;
	@SerializedName("playcount")
	private String playcount;
	@SerializedName("url")
	private String url;

	public Artist getArtist() {
		return artist;
	}

	public List<Images> getImages() {
		return images;
	}

	public String getMbid() {
		return mbid;
	}

	public String getName() {
		return name;
	}

	public String getPlaycount() {
		return playcount;
	}

	public String getUrl() {
		return url;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public void setImages(List<Images> images) {
		this.images = images;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlaycount(String playcount) {
		this.playcount = playcount;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Album{" +
				"mbid='" + mbid + '\'' +
				", name='" + name + '\'' +
				", artist=" + artist +
				", images=" + images +
				", playcount='" + playcount + '\'' +
				", url='" + url + '\'' +
				"}\n";
	}
}
