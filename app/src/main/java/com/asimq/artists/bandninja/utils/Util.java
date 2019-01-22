package com.asimq.artists.bandninja.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.asimq.artists.bandninja.json.Image;
import com.asimq.artists.bandninja.json.MusicItem;

public class Util {

	public static String getPostalCode(Context context, double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
//                result.append(address.getLocality()).append("\n");
//                result.append(address.getCountryName());
				result.append(address.getPostalCode());
			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}

	public static boolean containsImageUrls(List<Image> images) {
		if (null == images || images.isEmpty()) {
			return false;
		}
		for (Image image : images) {
			if (!image.getText().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public static String getImageUrl(@NonNull MusicItem musicItem) {
		final List<Image> images = musicItem.getImages();
		if (null == images || images.isEmpty()) {
			return "";
		}
		return images.get(images.size() - 1).getText();
	}

	public static void populateHTMLForSwitcher(@NonNull TextSwitcher switcher, @NonNull String content) {
		switcher.setCurrentText(Html.fromHtml(content
				.replaceAll("(\n)", "<br />")
				.replaceAll("(\r)", "<br />")));
		TextView tv = (TextView) switcher.getCurrentView();
		tv.setMovementMethod(LinkMovementMethod.getInstance());
	}


	public static List removeAllItemsWithoutMbidOrImages(List<? extends MusicItem> musicItems) {
		if (android.os.Build.VERSION.SDK_INT < 24) {
			// Create an iterator from the l
			Iterator<? extends MusicItem> itr = musicItems.iterator();

			while (itr.hasNext()) {
				MusicItem item = itr.next();
				// Checking for Predicate condition
				if ((null == item.getMbid() || item.getMbid().isEmpty())
						&& (!containsImageUrls(item.getImages()))) {
					itr.remove();
				}
			}
			return musicItems;
		}
		return Util.removeUsingIterator(musicItems, item ->
				(null == item.getMbid() || item.getMbid().isEmpty())
						&& (!containsImageUrls(item.getImages()))
		);
	}

	// Generic function to remove Null Using Iterator
	public static <T> List<T> removeUsingIterator(List<T> l, Predicate<T> p) {

		// Create an iterator from the l
		Iterator<T> itr = l.iterator();

		// Find and remove all null
		while (itr.hasNext()) {

			// Fetching the next element
			T t = itr.next();

			// Checking for Predicate condition
			if (p.test(t)) {

				// If the condition matches,
				// remove that element
				itr.remove();
			}
		}

		// Return the null
		return l;
	}

}
