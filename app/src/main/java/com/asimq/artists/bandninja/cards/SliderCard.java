package com.asimq.artists.bandninja.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.json.MusicItem;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.utils.Util;
import com.squareup.picasso.Picasso;

public class SliderCard extends RecyclerView.ViewHolder {

	private static int viewWidth = 0;
	final String TAG = this.getClass().getSimpleName();
	private final ImageView imageView;

	public SliderCard(View itemView) {
		super(itemView);
		imageView = itemView.findViewById(R.id.image);
	}

	protected void loadImageUrl(Context context, String url) {
		if (url.isEmpty()) {
			return;
		}
		Picasso.with(context).load(url).resize(viewWidth, viewWidth).into(
				imageView, new com.squareup.picasso.Callback() {
					@Override
					public void onError() {
						Log.i(TAG, "image is empty");
					}

					@Override
					public void onSuccess() {

					}
				});
	}

	void setContent(Context context, MusicItem musicItem) {
		final String imageUrl = Util.getImageUrl(musicItem);
		if (viewWidth == 0) {
			itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					viewWidth = itemView.getWidth();
					loadImageUrl(context, imageUrl);
				}
			});
		} else {
			loadImageUrl(context, imageUrl);
		}
	}

	void setContent(Context context, AlbumData albumData) {
		final String imageUrl = albumData.getImage();
		if (viewWidth == 0) {
			itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					viewWidth = itemView.getWidth();
					loadImageUrl(context, imageUrl);
				}
			});
		} else {
			loadImageUrl(context, imageUrl);
		}
	}

}