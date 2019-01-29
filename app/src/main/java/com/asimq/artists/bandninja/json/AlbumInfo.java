package com.asimq.artists.bandninja.json;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.utils.Util;
import com.google.gson.annotations.SerializedName;

public class AlbumInfo extends BaseMusicItem implements Comparable<AlbumInfo>{

	@SerializedName("artist")
	private String artist = "";
	@SerializedName("image")
	private List<Image> images = new ArrayList<>();
	@SerializedName("mbid")
	private String mbid = "";
	@SerializedName("name")
	private String name = "";
	@SerializedName("playcount")
	private String playcount = "";
	@SerializedName("releasedate")
	private String releaseDate = "";
	@SerializedName("tags")
	private TagWrapper tagWrapper = new TagWrapper();
	@SerializedName("tracks")
	private TrackWrapper trackWrapper = new TrackWrapper();
	@SerializedName("url")
	private String url = "";
	@SerializedName("wiki")
	private Wiki wiki = new Wiki();

	@Override
	public int compareTo(@NonNull AlbumInfo albumInfo) {
		int playcount = Util.stringToInt(albumInfo.getPlaycount());
		int playcount1 = Util.stringToInt(getPlaycount());
		return playcount - playcount1;
	}

	public AlbumInfo() {
	}

	public AlbumInfo(@NonNull AlbumData albumData) {
		this.mbid = albumData.getMbid();
		this.name = albumData.getName();
		this.artist = albumData.getArtist();
		Image image = new Image();
		image.setText(albumData.getImage());
		this.images.add(image);
		this.url = albumData.getUrl();
		this.wiki.setContent(albumData.getWiki());
		this.wiki.setSummary(albumData.getSummary());
		this.playcount = albumData.getPlaycount();
		this.tagWrapper.setTags(Util.getTagsFromString(albumData.getTags()));
		this.releaseDate = albumData.getReleaseDate();
	}

	public AlbumInfo(@NonNull Album album) {
		this.mbid = album.getMbid();
		this.name = album.getName();
		this.artist = album.getArtist().getName();
		this.images = album.getImages();
		this.url = album.getUrl();
		this.wiki.setContent(album.getWiki().getContent());
		this.wiki.setSummary(album.getWiki().getSummary());
		this.playcount = album.getPlaycount() +"";
		this.tagWrapper.setTags(album.getTagWrapper().getTags());
	}

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
