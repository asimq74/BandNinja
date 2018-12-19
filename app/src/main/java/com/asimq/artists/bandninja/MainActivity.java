package com.asimq.artists.bandninja;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.asimq.artists.bandninja.cards.SliderAdapter;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.utils.DecodeBitmapTask;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	private class ImageViewFactory implements ViewSwitcher.ViewFactory {

		@Override
		public View makeView() {
			final ImageView imageView = new ImageView(MainActivity.this);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

			final LayoutParams lp = new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			imageView.setLayoutParams(lp);

			return imageView;
		}
	}

	private class OnCardClickListener implements View.OnClickListener {

		@Override
		public void onClick(View view) {
			final CardSliderLayoutManager lm = (CardSliderLayoutManager) recyclerView.getLayoutManager();

			if (lm.isSmoothScrolling()) {
				return;
			}

			final int activeCardPosition = lm.getActiveCardPosition();
			if (activeCardPosition == RecyclerView.NO_POSITION) {
				return;
			}

			final int clickedPosition = recyclerView.getChildAdapterPosition(view);
			if (clickedPosition == activeCardPosition) {
				final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
				intent.putExtra(DetailsActivity.BUNDLE_IMAGE_ID, 2);

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
					startActivity(intent);
				} else {
					final CardView cardView = (CardView) view;
					final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
					final ActivityOptions options = ActivityOptions
							.makeSceneTransitionAnimation(MainActivity.this, sharedView, "shared");
					startActivity(intent, options.toBundle());
				}
			} else if (clickedPosition > activeCardPosition) {
				recyclerView.smoothScrollToPosition(clickedPosition);
				onActiveCardChange(clickedPosition);
			}
		}
	}

	private class TextViewFactory implements ViewSwitcher.ViewFactory {

		final boolean center;
		@StyleRes
		final int styleId;

		TextViewFactory(@StyleRes int styleId, boolean center) {
			this.styleId = styleId;
			this.center = center;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View makeView() {
			final TextView textView = new TextView(MainActivity.this);

			if (center) {
				textView.setGravity(Gravity.CENTER);
			}

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
				textView.setTextAppearance(MainActivity.this, styleId);
			} else {
				textView.setTextAppearance(styleId);
			}

			return textView;
		}

	}
	public static final String MB_ID = "MB_ID";
	final String TAG = this.getClass().getSimpleName();
	@Inject
	ArtistDataDao artistDataDao;
	private TextSwitcher clockSwitcher;
	private final String[] countries = {"PARIS", "SEOUL", "LONDON", "BEIJING", "THIRA"};
	private TextView country1TextView;
	private TextView country2TextView;
	private long countryAnimDuration;
	private int countryOffset1;
	private int countryOffset2;
	private int currentPosition;
	private DecodeBitmapTask decodeMapBitmapTask;
	private final int[] descriptions = {R.string.text1, R.string.text2, R.string.text3, R.string.text4, R.string.text5};
	private TextSwitcher descriptionsSwitcher;
	private final int[][] dotCoords = new int[5][2];
	@BindView(R.id.fab)
	FloatingActionButton fab;
	private View greenDot;
	private CardSliderLayoutManager layoutManger;
	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	private DecodeBitmapTask.Listener mapLoadListener;
	private ImageSwitcher mapSwitcher;
	private final int[] maps = {R.drawable.map_paris, R.drawable.map_seoul, R.drawable.map_london, R.drawable.map_beijing};
//	private final int[] pics = {R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4};
//	private final String[] imageUrls = {"https://lastfm-img2.akamaized.net/i/u/174s/e909c183889102c07ac45faba7b3ff0a.png",
//			"https://lastfm-img2.akamaized.net/i/u/174s/e909c183889102c07ac45faba7b3ff0a.png",
//			"https://lastfm-img2.akamaized.net/i/u/174s/e909c183889102c07ac45faba7b3ff0a.png",
//			"https://lastfm-img2.akamaized.net/i/u/174s/e909c183889102c07ac45faba7b3ff0a.png"};
	private TextSwitcher placeSwitcher;
//	private final String[] places = {"The Louvre", "Gwanghwamun", "Tower Bridge", "Temple of Heaven", "Aegeana Sea"};
	private RecyclerView recyclerView;
	private SearchResultsViewModel searchResultsViewModel;
	@Inject
	SearchResultsViewModelFactory searchResultsViewModelFactory;
	private SliderAdapter sliderAdapter;
	private TextSwitcher temperatureSwitcher;
//	private final String[] temperatures = {"21°C", "19°C", "17°C", "23°C"};
//	private final String[] times = {"Aug 1 - Dec 15    7:00-18:00", "Sep 5 - Nov 10    8:00-16:00", "Mar 8 - May 21    7:00-18:00"};
	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private void initCountryText() {
		countryAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
		countryOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
		countryOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
		country1TextView = (TextView) findViewById(R.id.tv_country_1);
		country2TextView = (TextView) findViewById(R.id.tv_country_2);

		country1TextView.setX(countryOffset1);
		country2TextView.setX(countryOffset2);
		country1TextView.setText(countries[0]);
		country2TextView.setAlpha(0f);
	}

	private void initGreenDot() {
		mapSwitcher.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mapSwitcher.getViewTreeObserver().removeOnGlobalLayoutListener(this);

				final int viewLeft = mapSwitcher.getLeft();
				final int viewTop = mapSwitcher.getTop() + mapSwitcher.getHeight() / 3;

				final int border = 100;
				final int xRange = Math.max(1, mapSwitcher.getWidth() - border * 2);
				final int yRange = Math.max(1, (mapSwitcher.getHeight() / 3) * 2 - border * 2);

				final Random rnd = new Random();

				for (int i = 0, cnt = dotCoords.length; i < cnt; i++) {
					dotCoords[i][0] = viewLeft + border + rnd.nextInt(xRange);
					dotCoords[i][1] = viewTop + border + rnd.nextInt(yRange);
				}

				greenDot = findViewById(R.id.green_dot);
				greenDot.setX(dotCoords[0][0]);
				greenDot.setY(dotCoords[0][1]);
			}
		});
	}

	private void initRecyclerView(@NonNull List<Artist> artists) {
		sliderAdapter = new SliderAdapter(this, artists, new OnCardClickListener());
		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setAdapter(sliderAdapter);
		recyclerView.setHasFixedSize(false);

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					onActiveCardChange();
				}
			}
		});

		layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

		new CardSnapHelper().attachToRecyclerView(recyclerView);
	}

	private void initSwitchers(@NonNull List<Artist> artists) {
		temperatureSwitcher = (TextSwitcher) findViewById(R.id.ts_temperature);
		temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
		temperatureSwitcher.setCurrentText(artists.get(0).getName());

		placeSwitcher = (TextSwitcher) findViewById(R.id.ts_place);
		placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
		placeSwitcher.setCurrentText(artists.get(0).getName());

		clockSwitcher = (TextSwitcher) findViewById(R.id.ts_clock);
		clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
		clockSwitcher.setCurrentText(artists.get(0).getName());

		descriptionsSwitcher = (TextSwitcher) findViewById(R.id.ts_description);
		descriptionsSwitcher.setInAnimation(this, android.R.anim.fade_in);
		descriptionsSwitcher.setOutAnimation(this, android.R.anim.fade_out);
		descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
		descriptionsSwitcher.setCurrentText(getString(descriptions[0]));

		mapSwitcher = (ImageSwitcher) findViewById(R.id.ts_map);
		mapSwitcher.setInAnimation(this, R.anim.fade_in);
		mapSwitcher.setOutAnimation(this, R.anim.fade_out);
		mapSwitcher.setFactory(new ImageViewFactory());
		mapSwitcher.setImageResource(maps[0]);

		mapLoadListener = new DecodeBitmapTask.Listener() {
			@Override
			public void onPostExecuted(Bitmap bitmap) {
				((ImageView) mapSwitcher.getNextView()).setImageBitmap(bitmap);
				mapSwitcher.showNext();
			}
		};
	}

	private void onActiveCardChange() {
		final int pos = layoutManger.getActiveCardPosition();
		if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
			return;
		}

		onActiveCardChange(pos);
	}

	List<Artist> artists = new ArrayList<>();

	private void onActiveCardChange(int pos) {
		int animH[] = new int[]{R.anim.slide_in_right, R.anim.slide_out_left};
		int animV[] = new int[]{R.anim.slide_in_top, R.anim.slide_out_bottom};

		final boolean left2right = pos < currentPosition;
		if (left2right) {
			animH[0] = R.anim.slide_in_left;
			animH[1] = R.anim.slide_out_right;

			animV[0] = R.anim.slide_in_bottom;
			animV[1] = R.anim.slide_out_top;
		}

		setCountryText(countries[pos % countries.length], left2right);

		temperatureSwitcher.setInAnimation(MainActivity.this, animH[0]);
		temperatureSwitcher.setOutAnimation(MainActivity.this, animH[1]);
		temperatureSwitcher.setText(artists.get(pos % artists.size()).getName());

		placeSwitcher.setInAnimation(MainActivity.this, animV[0]);
		placeSwitcher.setOutAnimation(MainActivity.this, animV[1]);
		placeSwitcher.setText(artists.get(pos % artists.size()).getName());

		clockSwitcher.setInAnimation(MainActivity.this, animV[0]);
		clockSwitcher.setOutAnimation(MainActivity.this, animV[1]);
		clockSwitcher.setText(artists.get(pos % artists.size()).getName());

		descriptionsSwitcher.setText(artists.get(pos % artists.size()).getName());

		showMap(maps[pos % maps.length]);

		ViewCompat.animate(greenDot)
				.translationX(dotCoords[pos % dotCoords.length][0])
				.translationY(dotCoords[pos % dotCoords.length][1])
				.start();

		currentPosition = pos;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		final MyApplication application = (MyApplication) getApplicationContext();
		application.getApplicationComponent().inject(this);
		searchResultsViewModel = ViewModelProviders.of(this, searchResultsViewModelFactory)
				.get(SearchResultsViewModel.class);
		setSupportActionBar(toolbar);
		List<Artist> artists = new ArrayList<>();
		initRecyclerView(artists);
		initCountryText();
		initSwitchers(artists);
		initGreenDot();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_navigation, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String s) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "onQueryTextSubmit: query->" + query);
//				searchResultsViewModel.getSearchResultsByArtist(query).observe(MainNavigationActivity.this, artists -> populateUI(artists));
				return false;
			}
		});
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing() && decodeMapBitmapTask != null) {
			decodeMapBitmapTask.cancel(true);
		}
	}

	private void setCountryText(String text, boolean left2right) {
		final TextView invisibleText;
		final TextView visibleText;
		if (country1TextView.getAlpha() > country2TextView.getAlpha()) {
			visibleText = country1TextView;
			invisibleText = country2TextView;
		} else {
			visibleText = country2TextView;
			invisibleText = country1TextView;
		}

		final int vOffset;
		if (left2right) {
			invisibleText.setX(0);
			vOffset = countryOffset2;
		} else {
			invisibleText.setX(countryOffset2);
			vOffset = 0;
		}

		invisibleText.setText(text);

		final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
		final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
		final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", countryOffset1);
		final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

		final AnimatorSet animSet = new AnimatorSet();
		animSet.playTogether(iAlpha, vAlpha, iX, vX);
		animSet.setDuration(countryAnimDuration);
		animSet.start();
	}

	private void showMap(@DrawableRes int resId) {
		if (decodeMapBitmapTask != null) {
			decodeMapBitmapTask.cancel(true);
		}

		final int w = mapSwitcher.getWidth();
		final int h = mapSwitcher.getHeight();

		decodeMapBitmapTask = new DecodeBitmapTask(getResources(), resId, w, h, mapLoadListener);
		decodeMapBitmapTask.execute();
	}

}
