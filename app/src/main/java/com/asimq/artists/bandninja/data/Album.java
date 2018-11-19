package com.asimq.artists.bandninja.data;

import java.util.Arrays;

import com.google.gson.annotations.SerializedName;

public class Album {

	@SerializedName("artist")
	private Artist artist;
	@SerializedName("image")
	private Image[] images;
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

	public Image[] getImages() {
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

	public void setImages(Image[] images) {
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
				", images=" + Arrays.toString(images) +
				", playcount='" + playcount + '\'' +
				", url='" + url + '\'' +
				"}\n";
	}
}
