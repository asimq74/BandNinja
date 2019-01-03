package com.asimq.artists.bandninja;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
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

import com.asimq.artists.bandninja.asynctasks.BaseSaveArtistTask;
import com.asimq.artists.bandninja.cards.SliderAdapter;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;
import com.asimq.artists.bandninja.utils.DecodeBitmapTask;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	public static final CardSnapHelper CARD_SNAP_HELPER = new CardSnapHelper();
	private ApplicationComponent applicationComponent;

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
//				onActiveCardChange(artists, clickedPosition);
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
	@Inject
	ArtistTagDao artistTagDao;
	private TextSwitcher clockSwitcher;
	private TextView artist1TextView;
	private TextView artist2TextView;
	private long artistAnimDuration;
	private int artistOffset1;
	private int artistOffset2;
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

	private void initArtistNameText(@NonNull List<Artist> artists) {
		String artistName = artists.get(0).getName();
		artistAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
		artistOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
		artistOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
		artist1TextView = findViewById(R.id.mainTitleView_1);
		artist2TextView = findViewById(R.id.mainTitleView_2);

		artist1TextView.setX(artistOffset1);
		artist2TextView.setX(artistOffset2);
		artist1TextView.setText(artistName);
		artist2TextView.setAlpha(0f);
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

	@Override
	protected void onPause() {
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(
//				mMessageReceiver);
		super.onPause();
		if (isFinishing() && decodeMapBitmapTask != null) {
			decodeMapBitmapTask.cancel(true);
		}
	}

	@Override
	protected void onResume() {
		// Register to receive messages.
		// We are registering an observer (mMessageReceiver) to receive Intents
		// with actions named "custom-event-name".
//		LocalBroadcastManager.getInstance(this).registerReceiver(
//				mMessageReceiver, new IntentFilter("ARTIST_BOUND"));
		super.onResume();
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive( Context context, Intent intent ) {
			if (!"ARTIST_BOUND".equals(intent.getAction())) {
				return;
			}
			String artistName = intent.getStringExtra("ARTIST_NAME");
			String artistMbid = intent.getStringExtra("ARTIST_MBID");
			Log.d( TAG, String.format("artist: %s mbid: %s", artistName, artistMbid));
		}
	};

	private void initRecyclerView(@NonNull List<Artist> artists) {
		if (null != sliderAdapter) {
			sliderAdapter.clear();
		}
		sliderAdapter = new SliderAdapter(applicationComponent, artists, new OnCardClickListener());
		sliderAdapter.notifyDataSetChanged();
		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setAdapter(sliderAdapter);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					onActiveCardChange(artists);
				}
			}
		});

		recyclerView.setLayoutManager(new CardSliderLayoutManager(this));
		layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

		CARD_SNAP_HELPER.attachToRecyclerView(recyclerView);
	}


	private void populateArtistInfo(Artist artistDetailedInfo) {
		ArtistData artistData = new ArtistData(artistDetailedInfo);
		Log.d( TAG, String.format("artistData: %s", artistData));
		new BaseSaveArtistTask(artistDataDao).execute(artistData);
		searchResultsViewModel.getArtistInfo(artistData.getName()).observe(MainActivity.this,
				artist -> processArtistInfo(artist));
	}


	private void processArtistInfo(Artist artist) {
		List<Tag> tags = artist.getTagWrapper().getTags();
		Log.d(TAG, "tags=" + tags);
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (Tag tag : tags) {
			sb.append(tag.getName()).append(count++ < (tags.size() - 1) ? ", " : "");
		}
		String tagsText = sb.toString();
		placeSwitcher = findViewById(R.id.ts_place);
		placeSwitcher.removeAllViews();
		placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
		placeSwitcher.setCurrentText(tagsText);

		descriptionsSwitcher = findViewById(R.id.ts_description);
		descriptionsSwitcher.removeAllViews();
		descriptionsSwitcher.setInAnimation(this, android.R.anim.fade_in);
		descriptionsSwitcher.setOutAnimation(this, android.R.anim.fade_out);
		descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
		descriptionsSwitcher.setCurrentText(artist.getBio().getSummary());
	}


	private void initSwitchers(@NonNull List<Artist> artists) {
		String artistName = artists.get(0).getName();
		String artistMbid = artists.get(0).getMbid();
		Log.d( TAG, String.format("artist: %s mbid: %s", artistName, artistMbid));
		searchResultsViewModel.getArtistInfo(artistName).observe(MainActivity.this,
				artistDetailedInfo -> populateArtistInfo(artistDetailedInfo));

		temperatureSwitcher = (TextSwitcher) findViewById(R.id.ts_temperature);
		temperatureSwitcher.removeAllViews();
		temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
		temperatureSwitcher.setCurrentText(artists.get(0).getListeners() + "");

//		placeSwitcher = (TextSwitcher) findViewById(R.id.ts_place);
//		placeSwitcher.removeAllViews();
//		placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
//		placeSwitcher.setCurrentText(artists.get(0).getName());

		clockSwitcher = (TextSwitcher) findViewById(R.id.ts_clock);
		clockSwitcher.removeAllViews();
		clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
		clockSwitcher.setCurrentText(artists.get(0).getName());

		descriptionsSwitcher = (TextSwitcher) findViewById(R.id.ts_description);
		descriptionsSwitcher.removeAllViews();
		descriptionsSwitcher.setInAnimation(this, android.R.anim.fade_in);
		descriptionsSwitcher.setOutAnimation(this, android.R.anim.fade_out);
		descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
		descriptionsSwitcher.setCurrentText(getString(descriptions[0]));

		mapSwitcher = (ImageSwitcher) findViewById(R.id.ts_map);
		mapSwitcher.removeAllViews();
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

	private void onActiveCardChange(List<Artist> artists) {
		final int pos = layoutManger.getActiveCardPosition();
		if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
			return;
		}

		onActiveCardChange(artists, pos);
	}


	private void onActiveCardChange(List<Artist> artists, int pos) {
		String artistName = artists.get(pos).getName();
		String artistMbid = artists.get(pos).getMbid();
		Log.d( TAG, String.format("artist: %s mbid: %s", artistName, artistMbid));
		temperatureSwitcher.removeAllViews();
		temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
		temperatureSwitcher.setCurrentText(artists.get(pos).getListeners() + "");
		searchResultsViewModel.getArtistInfo(artistName).observe(MainActivity.this,
				artistDetailedInfo -> populateArtistInfo(artistDetailedInfo));
		int animH[] = new int[]{R.anim.slide_in_right, R.anim.slide_out_left};
		int animV[] = new int[]{R.anim.slide_in_top, R.anim.slide_out_bottom};

		final boolean left2right = pos < currentPosition;
		if (left2right) {
			animH[0] = R.anim.slide_in_left;
			animH[1] = R.anim.slide_out_right;

			animV[0] = R.anim.slide_in_bottom;
			animV[1] = R.anim.slide_out_top;
		}

		setArtistText(artists.get(pos % artists.size()).getName(), left2right);

		temperatureSwitcher.setInAnimation(MainActivity.this, animH[0]);
		temperatureSwitcher.setOutAnimation(MainActivity.this, animH[1]);
		//replace with progress bar
//		temperatureSwitcher.setText(artists.get(pos % artists.size()).getName());

		placeSwitcher.setInAnimation(MainActivity.this, animV[0]);
		placeSwitcher.setOutAnimation(MainActivity.this, animV[1]);
//replace with progress bar
//		placeSwitcher.setText(artists.get(pos % artists.size()).getName());

		clockSwitcher.setInAnimation(MainActivity.this, animV[0]);
		clockSwitcher.setOutAnimation(MainActivity.this, animV[1]);
		// replace with progress bar
//		clockSwitcher.setText(artists.get(pos % artists.size()).getName());

		// replace with progress bar
//		descriptionsSwitcher.setText(artists.get(pos % artists.size()).getName());

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
		applicationComponent = application.getApplicationComponent();
		applicationComponent.inject(this);
		searchResultsViewModel = ViewModelProviders.of(this, searchResultsViewModelFactory)
				.get(SearchResultsViewModel.class);
		setSupportActionBar(toolbar);
	}

	private void populateUI(List<Artist> artists) {
		initRecyclerView(artists);
		initArtistNameText(artists);
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
				searchResultsViewModel.getSearchResultsByArtist(query).observe(MainActivity.this, artists -> populateUI(artists));
				return false;
			}
		});
		return true;
	}


	private void setArtistText(String text, boolean left2right) {
		final TextView invisibleText;
		final TextView visibleText;
		if (artist1TextView.getAlpha() > artist2TextView.getAlpha()) {
			visibleText = artist1TextView;
			invisibleText = artist2TextView;
		} else {
			visibleText = artist2TextView;
			invisibleText = artist1TextView;
		}

		final int vOffset;
		if (left2right) {
			invisibleText.setX(0);
			vOffset = artistOffset2;
		} else {
			invisibleText.setX(artistOffset2);
			vOffset = 0;
		}

		invisibleText.setText(text);

		final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
		final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
		final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", artistOffset1);
		final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

		final AnimatorSet animSet = new AnimatorSet();
		animSet.playTogether(iAlpha, vAlpha, iX, vX);
		animSet.setDuration(artistAnimDuration);
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
