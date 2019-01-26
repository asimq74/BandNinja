package com.asimq.artists.bandninja.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextSwitcher;
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

	public enum ServiceStatus {
		SUCCESS, FAILURE
	}

	private static List<String> BLOCKED_TAGS = Arrays.asList("albums I own", "favorite albums");
	public static final String PREFS_WIDGET_TITLE = "PREFS_WIDGET_TITLE";

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

	public static String getKeyFromAlbumData(@NonNull AlbumData albumData) {
		return getKeyFromArtistAndAlbum(albumData.getArtist(), albumData.getName());
	}

	public static String getKeyFromTrackData(@NonNull TrackData trackData) {
		return getKeyFromArtistAndAlbum(trackData.getArtistName(), trackData.getAlbumName());
	}

	public static String getKeyFromArtistAndAlbum(@NonNull String artist, @NonNull String album) {
		return String.format("%s|%s", artist, album);
	}

	@NonNull
	public static List<Tag> getTagsFromString(@NonNull String tagString) {
		List<Tag> tags = new ArrayList<>();
		if (tagString.isEmpty()) {
			return tags;
		}
		if (tagString.indexOf(",") < 0) {
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

	public static void populateHTMLForSwitcher(@NonNull TextSwitcher switcher, @NonNull String content) {
		Spanned text = Html.fromHtml(content
				.replaceAll("(\n)", "<br />")
				.replaceAll("(\r)", "<br />"));
		switcher.setText(text);
		TextView tv = (TextView) switcher.getCurrentView();
		tv.setText(text);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
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
