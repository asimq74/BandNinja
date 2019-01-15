package com.asimq.artists.bandninja;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.transition.Slide;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.asimq.artists.bandninja.MusicItemsListFragment.OnDetailsInteractionListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity implements OnDetailsInteractionListener {

	@Override
	public void onDisplayAlbumsByArtist(@NonNull String artistName) {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			musicItemsListFragment.displayAlbumsByArtist(artistName);
		}
	}

	public static final String EXTRA_IMAGE = "extraImage";
	public static final String EXTRA_TITLE = "extraTitle";
	private CollapsingToolbarLayout collapsingToolbarLayout;

	private void applyPalette(Palette palette) {
		int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
		int primary = getResources().getColor(R.color.colorPrimary);
		collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
		collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
		updateBackground((FloatingActionButton) findViewById(R.id.fab), palette);
		supportStartPostponedEnterTransition();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent motionEvent) {
		try {
			return super.dispatchTouchEvent(motionEvent);
		} catch (NullPointerException e) {
			return false;
		}
	}

	private void initActivityTransitions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Slide transition = new Slide();
			transition.excludeTarget(android.R.id.statusBarBackground, true);
			getWindow().setEnterTransition(transition);
			getWindow().setReturnTransition(transition);
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActivityTransitions();
		setContentView(R.layout.activity_details);

//		ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
//		supportPostponeEnterTransition();

		setSupportActionBar(findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String itemTitle = getIntent().getStringExtra(EXTRA_TITLE);
		collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbarLayout.setTitle(itemTitle);
		collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

		final ImageView image = (ImageView) findViewById(R.id.image);
		String extraImageUrl = getIntent().getStringExtra(EXTRA_IMAGE);
		Picasso.with(this).load(extraImageUrl).into(image, new Callback() {
			@Override
			public void onError() {

			}

			@Override
			public void onSuccess() {
				Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
				Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
					public void onGenerated(Palette palette) {
						applyPalette(palette);
					}
				});
			}
		});

		TextView title = findViewById(R.id.title);
		title.setText(itemTitle);
		onDisplayAlbumsByArtist(itemTitle);
	}

	private void updateBackground(FloatingActionButton fab, Palette palette) {
		int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
		int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorAccent));
		fab.setRippleColor(lightVibrantColor);
		fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
	}
}