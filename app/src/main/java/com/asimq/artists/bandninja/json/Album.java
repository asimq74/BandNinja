package com.asimq.artists.bandninja.json;

import java.util.List;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Album extends BaseMusicItem implements Comparable<Album> {

	@SerializedName("artist")
	private Artist artist;
	@SerializedName("image")
	private List<Image> images;
	@SerializedName("mbid")
	private String mbid;
	@SerializedName("name")
	private String name;
	@SerializedName("playcount")
	private int playcount;
	@SerializedName("tags")
	private TagWrapper tagWrapper;
	@SerializedName("tracks")
	private TrackWrapper trackWrapper;
	@SerializedName("url")
	private String url;
	@SerializedName("wiki")
	private Wiki wiki;

	@Override
	public int compareTo(@NonNull Album album) {
		return album.getPlaycount() - getPlaycount();
	}

	public Artist getArtist() {
		return artist;
	}

	public List<Image> getImages() {
		return images;
	}

	public String getMbid() {
		return mbid;
	}

	public String getName() {
		return name;
	}

	public int getPlaycount() {
		return playcount;
	}

	public TagWrapper getTagWrapper() {
		return tagWrapper;
	}

	public TrackWrapper getTrackWrapper() {
		return trackWrapper;
	}

	public String getUrl() {
		return url;
	}

	public Wiki getWiki() {
		return wiki;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlaycount(int playcount) {
		this.playcount = playcount;
	}

	public void setTagWrapper(TagWrapper tagWrapper) {
		this.tagWrapper = tagWrapper;
	}

	public void setTrackWrapper(TrackWrapper trackWrapper) {
		this.trackWrapper = trackWrapper;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWiki(Wiki wiki) {
		this.wiki = wiki;
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
				", tagWrapper='" + tagWrapper + '\'' +
				", trackWrapper='" + trackWrapper + '\'' +
				", wiki='" + wiki + '\'' +
				"}\n";
	}
}
