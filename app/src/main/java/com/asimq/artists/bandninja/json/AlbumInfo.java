package com.asimq.artists.bandninja.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AlbumInfo extends BaseMusicItem {

	@SerializedName("artist")
	private String artist;
	@SerializedName("image")
	private List<Image> images;
	@SerializedName("mbid")
	private String mbid;
	@SerializedName("name")
	private String name;
	@SerializedName("playcount")
	private String playcount;
	@SerializedName("releasedate")
	private String releaseDate;
	@SerializedName("tags")
	private TagWrapper tagWrapper;
	@SerializedName("tracks")
	private TrackWrapper trackWrapper;
	@SerializedName("url")
	private String url;
	@SerializedName("wiki")
	private Wiki wiki;

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getArtist() {
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

	public String getPlaycount() {
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

	public void setArtist(String artist) {
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

	public void setPlaycount(String playcount) {
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
		return "AlbumInfo{" +
				"mbid='" + mbid + '\'' +
				", name='" + name + '\'' +
				", artist=" + artist +
				", images=" + images +
				", releaseDate=" + releaseDate +
				", playcount='" + playcount + '\'' +
				", url='" + url + '\'' +
				", tagWrapper='" + tagWrapper + '\'' +
				", trackWrapper='" + trackWrapper + '\'' +
				", wiki='" + wiki + '\'' +
				"}\n";
	}
}
