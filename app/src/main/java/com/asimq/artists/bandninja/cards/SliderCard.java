package com.asimq.artists.bandninja.cards;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Image;
import com.asimq.artists.bandninja.json.MusicItem;
import com.asimq.artists.bandninja.utils.DecodeBitmapTask;
import com.squareup.picasso.Picasso;

public class SliderCard extends RecyclerView.ViewHolder implements DecodeBitmapTask.Listener {

	private static int viewHeight = 0;
	private static int viewWidth = 0;
	final String TAG = this.getClass().getSimpleName();
	private final ImageView imageView;
	private DecodeBitmapTask task;

	public SliderCard(View itemView) {
		super(itemView);
		imageView = (ImageView) itemView.findViewById(R.id.image);
	}

	void clearContent() {
		if (task != null) {
			task.cancel(true);
		}
	}

	private String getImageUrl(@NonNull MusicItem musicItem) {
		final List<Image> images = musicItem.getImages();
		if (null == images || images.isEmpty()) {
			return "";
		}
		return images.get(images.size() - 1).getText();
	}

	private void loadBitmap(@DrawableRes int resId) {
		task = new DecodeBitmapTask(itemView.getResources(), resId, viewWidth, viewHeight, this);
		task.execute();
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

	@Override
	public void onPostExecuted(Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
	}

	void setContent(Context context, MusicItem musicItem) {
		final String imageUrl = getImageUrl(musicItem);
		if (viewWidth == 0) {
			itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					viewWidth = itemView.getWidth();
					viewHeight = itemView.getHeight();
					loadImageUrl(context, imageUrl);
				}
			});
		} else {
			loadImageUrl(context, imageUrl);
		}
	}

	void setContent(@DrawableRes final int resId) {
		if (viewWidth == 0) {
			itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

					viewWidth = itemView.getWidth();
					viewHeight = itemView.getHeight();
					loadBitmap(resId);
				}
			});
		} else {
			loadBitmap(resId);
		}
	}

}