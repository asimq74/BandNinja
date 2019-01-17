package com.asimq.artists.bandninja.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TopTagsWrapper {

	@SerializedName("toptags")
	@Expose
	private TopTags toptags = new TopTags();

	public TopTags getToptags() {
		return toptags;
	}

	public void setToptags(TopTags toptags) {
		this.toptags = toptags;
	}

	@Override
	public String toString() {
		return "TagWrapper{" +
				"toptags=" + toptags.toString() +
				'}';
	}
}
