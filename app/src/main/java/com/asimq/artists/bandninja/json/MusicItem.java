package com.asimq.artists.bandninja.json;

import java.util.List;


public interface MusicItem {

	String getMbid();

	String getName();

	List<Image> getImages();
}
