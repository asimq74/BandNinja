package com.asimq.artists.bandninja.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.asimq.artists.bandninja.json.Image;
import com.asimq.artists.bandninja.json.MusicItem;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.TrackData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class Util {

	public enum Entities {
		ARTIST, ALBUM, GENRE
	}

	public static class MySpannable extends ClickableSpan {

		private boolean isUnderline = true;

		/**
		 * Constructor
		 */
		public MySpannable(boolean isUnderline) {
			this.isUnderline = isUnderline;
		}

		@Override
		public void onClick(View widget) {

		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(isUnderline);
			ds.setColor(Color.parseColor("#1b76d3"));
		}
	}

	private static List<String> BLOCKED_TAGS = Arrays.asList("albums I own", "favorite albums");

	private static boolean containsImageUrls(List<Image> images) {
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

	public static String getKeyFromAlbumData(@NonNull AlbumData albumData) {
		return getKeyFromArtistAndAlbum(albumData.getArtist(), albumData.getName());
	}

	public static String getKeyFromArtistAndAlbum(@NonNull String artist, @NonNull String album) {
		return String.format("%s|%s", artist, album);
	}

	public static String getKeyFromTrackData(@NonNull TrackData trackData) {
		return getKeyFromArtistAndAlbum(trackData.getArtistName(), trackData.getAlbumName());
	}

	public static String getLocalityAndPostalCode(Context context, double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			if (!addresses.isEmpty()) {
				Address address = addresses.get(0);
				result.append(address.getLocality()).append(", ");
				result.append(address.getAdminArea()).append(" ");
				result.append(address.getPostalCode());
			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}

	public static String getPostalCode(Context context, double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			if (!addresses.isEmpty()) {
				Address address = addresses.get(0);
				result.append(address.getPostalCode());
			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}

	@NonNull
	public static String getTagsAsString(@NonNull List<Tag> tags) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (Tag tag : tags) {
			if (!BLOCKED_TAGS.contains(tag.getName())) {
				sb.append(tag.getName()).append(count++ < (tags.size() - 1) ? ", " : "");
			}
		}
		return sb.toString();
	}

	@NonNull
	public static List<Tag> getTagsFromString(@NonNull String tagString) {
		List<Tag> tags = new ArrayList<>();
		if (tagString.isEmpty()) {
			return tags;
		}
		if (tagString.indexOf(',') < 0) {
			tags.add(new Tag(tagString));
			return tags;
		}
		List<String> tagStringList = Arrays.asList(tagString.split("\\s*,\\s*"));
		for (String tagText : tagStringList) {
			tags.add(new Tag(tagText));
		}
		return tags;
	}

	public static boolean isConnected(@NonNull Context context) {
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();
	}

	public static boolean isGooglePlayServicesAvailable(@NonNull Context context) {
		return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
				== ConnectionResult.SUCCESS;
	}

	public static void populateHTMLForTextView(@NonNull TextView textView, @NonNull String content) {
		Spanned text = Html.fromHtml(content
				.replaceAll("(\n)", "<br />")
				.replaceAll("(\r)", "<br />"));
		textView.setText(text);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
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
	private static <T> List<T> removeUsingIterator(List<T> l, Predicate<T> p) {

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

	public static int stringToInt(String param) {
		try {
			return Integer.valueOf(param);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static String toMinsAndSeconds(String secondsString) {
		int milliseconds = stringToInt(secondsString);
		if (milliseconds < 0) {
			return milliseconds + "";
		}
		long minutes = milliseconds / 60;
		long seconds = milliseconds % 60;
		final String secondsFormattedString = seconds < 10 ? "0" + seconds : seconds + "";

		return String.format("%d:%s", minutes, secondsFormattedString);

	}
}
