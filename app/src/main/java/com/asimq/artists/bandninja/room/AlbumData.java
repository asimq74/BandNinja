package com.asimq.artists.bandninja.room;

import java.util.ArrayList;
import java.util.List;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.json.Wiki;
import com.asimq.artists.bandninja.utils.Util;

@Entity(tableName = "albums", primaryKeys = {"name", "mbid"})
public class AlbumData implements Comparable<AlbumData> {

	@ColumnInfo(name = "artist")
	private String artist = "";
	@ColumnInfo(name = "image")
	private String image = "";
	@NonNull
	@ColumnInfo(name = "mbid")
	private String mbid = "";
	@NonNull
	@ColumnInfo(name = "name")
	private String name = "";
	@ColumnInfo(name = "playcount")
	private String playcount = "";
	@ColumnInfo(name = "releasedate")
	private String releaseDate = "";
	@ColumnInfo(name = "summary")
	private String summary = "";
	@ColumnInfo(name = "tags")
	private String tags = "";
	@Ignore
	public List<TrackData> trackDatas = new ArrayList<>();
	@ColumnInfo(name = "url")
	private String url = "";
	@ColumnInfo(name = "wiki")
	private String wiki = "";

	public AlbumData() {
	}

	public AlbumData(@NonNull AlbumInfo albumInfo) {
		this.artist = albumInfo.getArtist();
		this.image = Util.getImageUrl(albumInfo);
		this.mbid = albumInfo.getMbid();
		this.name = albumInfo.getName();
		this.playcount = albumInfo.getPlaycount();
		this.releaseDate = albumInfo.getReleaseDate();
		this.tags = Util.getTagsAsString(albumInfo.getTagWrapper().getTags());
		this.url = albumInfo.getUrl();
		this.wiki = getWiki(albumInfo).getContent();
		this.summary = getWiki(albumInfo).getSummary();
		for (Track track : albumInfo.getTrackWrapper().getTracks()) {
			this.trackDatas.add(new TrackData(track));
		}
	}

	@Override
	public int compareTo(@NonNull AlbumData albumData) {
		return Util.stringToInt(albumData.getPlaycount()) - Util.stringToInt(getPlaycount());
	}

	public String getArtist() {
		return artist;
	}

	public String getImage() {
		return image;
	}

	@NonNull
	public String getMbid() {
		return mbid;
	}

	@NonNull
	public String getName() {
		return name;
	}

	public String getPlaycount() {
		return playcount;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public String getSummary() {
		return summary;
	}

	public String getTags() {
		return tags;
	}

	public List<TrackData> getTrackDatas() {
		return trackDatas;
	}

	public String getUrl() {
		return url;
	}

	private Wiki getWiki(@NonNull AlbumInfo albumInfo) {
		return albumInfo.getWiki();
	}

	public String getWiki() {
		return wiki;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setMbid(@NonNull String mbid) {
		this.mbid = mbid;
	}

	public void setName(@NonNull String name) {
		this.name = name;
	}

	public void setPlaycount(String playcount) {
		this.playcount = playcount;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setTrackDatas(List<TrackData> trackDatas) {
		this.trackDatas = trackDatas;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWiki(String wiki) {
		this.wiki = wiki;
	}

	@Override
	public String toString() {
		return "AlbumData{" +
				"artist='" + artist + '\'' +
				", image='" + image + '\'' +
				", mbid='" + mbid + '\'' +
				", name='" + name + '\'' +
				", playcount='" + playcount + '\'' +
				", releaseDate='" + releaseDate + '\'' +
				", tags='" + tags + '\'' +
				", url='" + url + '\'' +
				", summary='" + summary + '\'' +
				", wiki='" + wiki + '\'' +
				'}';
	}
}
