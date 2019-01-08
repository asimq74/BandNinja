package com.asimq.artists.bandninja;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicItemsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicItemsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MusicItemsListFragment extends Fragment {

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {

		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}

	private class ImageViewFactory implements ViewSwitcher.ViewFactory {

		@Override
		public View makeView() {
			final ImageView imageView = new ImageView(getActivity());
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
				final Intent intent = new Intent(getActivity(), DetailsActivity.class);
				intent.putExtra(DetailsActivity.BUNDLE_IMAGE_ID, 2);

				final CardView cardView = (CardView) view;
				final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
				final ActivityOptions options = ActivityOptions
						.makeSceneTransitionAnimation(getActivity(), sharedView, "shared");
				startActivity(intent, options.toBundle());
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
			final TextView textView = new TextView(getActivity());

			if (center) {
				textView.setGravity(Gravity.CENTER);
			}

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
				textView.setTextAppearance(getActivity(), styleId);
			} else {
				textView.setTextAppearance(styleId);
			}

			return textView;
		}

	}
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	public static final String MB_ID = "MB_ID";

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment MusicItemsListFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MusicItemsListFragment newInstance(String param1, String param2) {
		MusicItemsListFragment fragment = new MusicItemsListFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}
	public final CardSnapHelper CARD_SNAP_HELPER = new CardSnapHelper();
	final String TAG = this.getClass().getSimpleName();
	private ApplicationComponent applicationComponent;
	private TextView artist1TextView;
	private TextView artist2TextView;
	private long artistAnimDuration;
	@Inject
	ArtistDataDao artistDataDao;
	private int artistOffset1;
	private int artistOffset2;
	@Inject
	ArtistTagDao artistTagDao;
	private TextSwitcher clockSwitcher;
	private int currentPosition;
	private DecodeBitmapTask decodeMapBitmapTask;
	private final int[] descriptions = {R.string.text1, R.string.text2, R.string.text3, R.string.text4, R.string.text5};
	private TextSwitcher descriptionsSwitcher;
	private final int[][] dotCoords = new int[5][2];
	private View greenDot;
	private CardSliderLayoutManager layoutManger;
	OnFragmentInteractionListener mCallback;
	private OnFragmentInteractionListener mListener;
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!"ARTIST_BOUND".equals(intent.getAction())) {
				return;
			}
			String artistName = intent.getStringExtra("ARTIST_NAME");
			String artistMbid = intent.getStringExtra("ARTIST_MBID");
			Log.d(TAG, String.format("artist: %s mbid: %s", artistName, artistMbid));
		}
	};
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	private DecodeBitmapTask.Listener mapLoadListener;
	private ImageSwitcher mapSwitcher;
	private final int[] maps = {R.drawable.map_paris, R.drawable.map_seoul, R.drawable.map_london, R.drawable.map_beijing};
	private TextSwitcher placeSwitcher;
	private RecyclerView recyclerView;
	private SearchResultsViewModel searchResultsViewModel;
	@Inject
	SearchResultsViewModelFactory searchResultsViewModelFactory;
	private SliderAdapter sliderAdapter;
	private TextSwitcher temperatureSwitcher;
	public MusicItemsListFragment() {
		// Required empty public constructor
	}

	private void initArtistNameText(@NonNull List<Artist> artists) {
		String artistName = artists.get(0).getName();
		artistAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
		artistOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
		artistOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
		artist1TextView = getActivity().findViewById(R.id.mainTitleView_1);
		artist2TextView = getActivity().findViewById(R.id.mainTitleView_2);

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

				greenDot = getActivity().findViewById(R.id.green_dot);
				greenDot.setX(dotCoords[0][0]);
				greenDot.setY(dotCoords[0][1]);
			}
		});
	}

	private void initRecyclerView(@NonNull List<Artist> artists) {
		if (null != sliderAdapter) {
			sliderAdapter.clear();
		}
		sliderAdapter = new SliderAdapter(applicationComponent, artists, new OnCardClickListener());
		sliderAdapter.notifyDataSetChanged();
		recyclerView = getActivity().findViewById(R.id.recycler_view);
		recyclerView.setAdapter(sliderAdapter);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					onActiveCardChange(artists);
				}
			}
		});

		recyclerView.setLayoutManager(new CardSliderLayoutManager(getActivity()));
		layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

		CARD_SNAP_HELPER.attachToRecyclerView(recyclerView);
	}

	private void initSwitchers(@NonNull List<Artist> artists) {
		String artistName = artists.get(0).getName();
		String artistMbid = artists.get(0).getMbid();
		Log.d(TAG, String.format("artist: %s mbid: %s", artistName, artistMbid));
		searchResultsViewModel.getArtistInfo(artistName).observe(getActivity(),
				artistDetailedInfo -> populateArtistInfo(artistDetailedInfo));

		temperatureSwitcher = (TextSwitcher) getActivity().findViewById(R.id.ts_temperature);
		temperatureSwitcher.removeAllViews();
		temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
		temperatureSwitcher.setCurrentText(artists.get(0).getListeners() + "");

//		placeSwitcher = (TextSwitcher) getActivity().findViewById(R.id.ts_place);
//		placeSwitcher.removeAllViews();
//		placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
//		placeSwitcher.setCurrentText(artists.get(0).getName());

		clockSwitcher = (TextSwitcher) getActivity().findViewById(R.id.ts_clock);
		clockSwitcher.removeAllViews();
		clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
		clockSwitcher.setCurrentText(artists.get(0).getName());

		descriptionsSwitcher = (TextSwitcher) getActivity().findViewById(R.id.ts_description);
		descriptionsSwitcher.removeAllViews();
		descriptionsSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
		descriptionsSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);
		descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
		descriptionsSwitcher.setCurrentText(getString(descriptions[0]));

		mapSwitcher = (ImageSwitcher) getActivity().findViewById(R.id.ts_map);
		mapSwitcher.removeAllViews();
		mapSwitcher.setInAnimation(getActivity(), R.anim.fade_in);
		mapSwitcher.setOutAnimation(getActivity(), R.anim.fade_out);
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
		Log.d(TAG, String.format("artist: %s mbid: %s", artistName, artistMbid));
		temperatureSwitcher.removeAllViews();
		temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
		temperatureSwitcher.setCurrentText(artists.get(pos).getListeners() + "");
		searchResultsViewModel.getArtistInfo(artistName).observe(getActivity(),
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

		temperatureSwitcher.setInAnimation(getActivity(), animH[0]);
		temperatureSwitcher.setOutAnimation(getActivity(), animH[1]);
		//replace with progress bar
//		temperatureSwitcher.setText(artists.get(pos % artists.size()).getName());

		placeSwitcher.setInAnimation(getActivity(), animV[0]);
		placeSwitcher.setOutAnimation(getActivity(), animV[1]);
//replace with progress bar
//		placeSwitcher.setText(artists.get(pos % artists.size()).getName());

		clockSwitcher.setInAnimation(getActivity(), animV[0]);
		clockSwitcher.setOutAnimation(getActivity(), animV[1]);
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
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_music_items_list, container, false);
		ButterKnife.bind(this, view);
		final MyApplication application = (MyApplication) getActivity().getApplicationContext();
		applicationComponent = application.getApplicationComponent();
		applicationComponent.inject(this);
		searchResultsViewModel = ViewModelProviders.of(this, searchResultsViewModelFactory)
				.get(SearchResultsViewModel.class);
		searchResultsViewModel.getSearchResultsByArtist("kelly").observe(this, artists -> populateUI(artists));
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onPause() {
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(
//				mMessageReceiver);
		super.onPause();
		if (getActivity().isFinishing() && decodeMapBitmapTask != null) {
			decodeMapBitmapTask.cancel(true);
		}
	}

	@Override
	public void onResume() {
		// Register to receive messages.
		// We are registering an observer (mMessageReceiver) to receive Intents
		// with actions named "custom-event-name".
//		LocalBroadcastManager.getInstance(this).registerReceiver(
//				mMessageReceiver, new IntentFilter("ARTIST_BOUND"));
		super.onResume();
	}

	private void populateArtistInfo(Artist artistDetailedInfo) {
		ArtistData artistData = new ArtistData(artistDetailedInfo);
		Log.d(TAG, String.format("artistData: %s", artistData));
		new BaseSaveArtistTask(artistDataDao).execute(artistData);
		searchResultsViewModel.getArtistInfo(artistData.getName()).observe(this,
				artist -> processArtistInfo(artist));
	}

	private void populateUI(List<Artist> artists) {
		getActivity().findViewById(R.id.blueTabLayout).setVisibility(View.GONE);
		initRecyclerView(artists);
		initArtistNameText(artists);
		initSwitchers(artists);
		initGreenDot();
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
		placeSwitcher = getActivity().findViewById(R.id.ts_place);
		placeSwitcher.removeAllViews();
		placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
		placeSwitcher.setCurrentText(tagsText);

		descriptionsSwitcher = getActivity().findViewById(R.id.ts_description);
		descriptionsSwitcher.removeAllViews();
		descriptionsSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
		descriptionsSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);
		descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
		descriptionsSwitcher.setCurrentText(artist.getBio().getSummary());
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

	public void setOnHeadlineSelectedListener(OnFragmentInteractionListener onFragmentInteractionListener) {
		mCallback = onFragmentInteractionListener;
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
