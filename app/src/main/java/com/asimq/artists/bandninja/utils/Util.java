package com.asimq.artists.bandninja.utils;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextSwitcher;
import android.widget.TextView;

public class Util {

	public static void populateHTMLForSwitcher(@NonNull TextSwitcher switcher, @NonNull String content) {
		switcher.setCurrentText(Html.fromHtml(content
				.replaceAll("(\n)", "<br />")
				.replaceAll("(\r)", "<br />")));
		TextView tv = (TextView) switcher.getCurrentView();
		tv.setMovementMethod(LinkMovementMethod.getInstance());
	}

}
