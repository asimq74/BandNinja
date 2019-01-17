package com.asimq.artists.bandninja.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TopTags {

	@SerializedName("tag")
	@Expose
	private List<Tag> tags = new ArrayList<>();


	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tag) {
		this.tags = tag;
	}

	@Override
	public String toString() {
		return "TopTags{" +
				"tags=" + tags.toString() +
				'}';
	}

}
