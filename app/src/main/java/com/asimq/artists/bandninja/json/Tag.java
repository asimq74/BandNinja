package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tag {

	@SerializedName("name")
	@Expose
	private String name = "";
	@SerializedName("url")
	@Expose
	private String url = "";

	public Tag(String name) {
		this.name = name;
	}

	public Tag() {
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Tag{" +
				"name='" + name + '\'' +
				", url='" + url + '\'' +
				'}';
	}
}
