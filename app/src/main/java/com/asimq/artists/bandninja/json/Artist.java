package com.asimq.artists.bandninja.json;

import java.util.List;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Artist implements Comparable<Artist> {

	@SerializedName("bio")
	@Expose
	private Bio bio;
	@SerializedName("image")
	private List<Image> images;
	@SerializedName("listeners")
	private int listeners;
	@SerializedName("mbid")
	private String mbid;
	@SerializedName("name")
	private String name;
	@SerializedName("ontour")
	private String ontour;
	@SerializedName("similar")
	@Expose
	private Similar similar;
	@SerializedName("stats")
	@Expose
	private Stats stats;
	@SerializedName("streamable")
	private String streamable;
	@SerializedName("tags")
	@Expose
	private TagWrapper tagWrapper;
	@SerializedName("url")
	private String url;

	@Override
	public int compareTo(@NonNull Artist artist) {
		return artist.getListeners() - getListeners();
	}

	public Bio getBio() {
		return bio;
	}

	public List<Image> getImages() {
		return images;
	}

	public int getListeners() {
		return listeners;
	}

	public String getMbid() {
		return mbid;
	}

	public String getName() {
		return name;
	}

	public String getOntour() {
		return ontour;
	}

	public Similar getSimilar() {
		return similar;
	}

	public Stats getStats() {
		return stats;
	}

	public String getStreamable() {
		return streamable;
	}

	public TagWrapper getTagWrapper() {
		return tagWrapper;
	}

	public String getUrl() {
		return url;
	}

	public void setBio(Bio bio) {
		this.bio = bio;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public void setListeners(int listeners) {
		this.listeners = listeners;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOntour(String ontour) {
		this.ontour = ontour;
	}

	public void setSimilar(Similar similar) {
		this.similar = similar;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public void setStreamable(String streamable) {
		this.streamable = streamable;
	}

	public void setTagWrapper(TagWrapper tagWrapper) {
		this.tagWrapper = tagWrapper;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Artist{" +
				"listeners='" + listeners + '\'' +
				", mbid='" + mbid + '\'' +
				", name='" + name + '\'' +
				", images=" + images +
				", streamable='" + streamable + '\'' +
				", url='" + url + '\'' +
				", tagWrapper=" + tagWrapper +
				", ontour='" + ontour + '\'' +
				", bio=" + bio +
				", stats=" + stats +
				", similar=" + similar +
				'}';
	}
}
