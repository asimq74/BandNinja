package com.asimq.artists.bandninja.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Track;

@Entity(tableName = "tracks", primaryKeys = {"name", "albumName", "albumId"})
public class TrackData {

	@NonNull
	@ColumnInfo(name = "albumId")
	private String albumId = "";
	@NonNull
	@ColumnInfo(name = "albumName")
	private String albumName = "";
	@ColumnInfo(name = "artistName")
	private String artistName = "";
	@ColumnInfo(name = "duration")
	private String duration = "";
	@NonNull
	@ColumnInfo(name = "name")
	private String name = "";
	@ColumnInfo(name = "number")
	private Integer number = 0;
	@ColumnInfo(name = "url")
	private String url = "";

	public TrackData(@NonNull Track track) {
		this.name = track.getName();
		this.artistName = track.getArtist().getName();
		this.duration = track.getDuration();
		this.url = track.getUrl();
	}

	public TrackData() {
	}

	public String getAlbumId() {
		return albumId;
	}

	public String getAlbumName() {
		return albumName;
	}

	public String getArtistName() {
		return artistName;
	}

	public String getDuration() {
		return duration;
	}

	public String getName() {
		return name;
	}

	@NonNull
	public Integer getNumber() {
		return number;
	}

	public String getUrl() {
		return url;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumber(@NonNull Integer number) {
		this.number = number;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Track {" +
				"name='" + name + '\'' +
				", url='" + url + '\'' +
				", duration='" + duration + '\'' +
				", artistName='" + artistName + '\'' +
				", albumName='" + albumName + '\'' +
				", albumId='" + albumId + '\'' +
				", number='" + number + '\'' +
				"}\n";
	}

	public void updateAlbumInfo(@NonNull AlbumInfo albumInfo) {
		this.albumId = albumInfo.getMbid();
		this.albumName = albumInfo.getName();
	}
}
