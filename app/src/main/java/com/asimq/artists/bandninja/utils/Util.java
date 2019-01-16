package com.asimq.artists.bandninja.utils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.asimq.artists.bandninja.json.Image;
import com.asimq.artists.bandninja.json.MusicItem;

public class Util {

	public static void populateHTMLForSwitcher(@NonNull TextSwitcher switcher, @NonNull String content) {
		switcher.setCurrentText(Html.fromHtml(content
				.replaceAll("(\n)", "<br />")
				.replaceAll("(\r)", "<br />")));
		TextView tv = (TextView) switcher.getCurrentView();
		tv.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public static String getImageUrl(@NonNull MusicItem musicItem) {
		final List<Image> images = musicItem.getImages();
		if (null == images || images.isEmpty()) {
			return "";
		}
		return images.get(images.size() - 1).getText();
	}

	public static List removeAllItemsWithoutMbid(List<? extends MusicItem> musicItems) {
		return Util.removeNullUsingIterator(musicItems, item -> (null == item.getMbid() || item.getMbid().isEmpty()));
	}

	// Generic function to remove Null Using Iterator
	public static <T> List<T> removeNullUsingIterator(List<T> l, Predicate<T> p)
	{

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
