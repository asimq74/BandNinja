package com.asimq.artists.bandninja;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.asimq.artists.bandninja.cards.AlbumDataSliderAdapter;
import com.asimq.artists.bandninja.cards.SliderAdapter;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.BaseMusicItem;
import com.asimq.artists.bandninja.json.MusicItem;
import com.asimq.artists.bandninja.json.Wiki;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.utils.Util;
import com.asimq.artists.bandninja.utils.Util.Entities;
import com.asimq.artists.bandninja.viewmodelfactories.AlbumDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.ArtistDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.ArtistDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMainActivityInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicItemsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicItemsListFragment extends Fragment {

	public interface OnDetailsInteractionListener {

		void onDisplayAlbumsByArtist(@NonNull String artistName);
	}
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
	public interface OnMainActivityInteractionListener {

		void onDisplayArtistList(@NonNull List<Artist> artists);

		void onDisplayingArtistsByTag(@NonNull String tag);

		void onDisplayingTopAlbums();

		void onDisplayingTopArtists();

		void onSearchedForArtistName(@NonNull String artistName);

	}

	private class OnAlbumCardClickedListener implements View.OnClickListener {

		private final List<Album> albums;

		public OnAlbumCardClickedListener(List<Album> albums) {
			this.albums = albums;
		}

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
				Album album = albums.get(clickedPosition);
				Intent articleDetailIntent = new Intent(getActivity(), ArticleDetailActivity.class);
				articleDetailIntent.putExtra(ArticleDetailActivity.MBID, album.getMbid());
				articleDetailIntent.putExtra(ArticleDetailActivity.ENTITY_TYPE, Entities.ALBUM.name());
				articleDetailIntent.putExtra(ArticleDetailActivity.ARTIST, album.getArtist().getName());
				final CardView cardView = (CardView) view;
				final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
				ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
						Objects.requireNonNull(getActivity()), sharedView, DetailsActivity.EXTRA_IMAGE);
				startActivity(articleDetailIntent, options.toBundle());
			} else if (clickedPosition > activeCardPosition) {
				recyclerView.smoothScrollToPosition(clickedPosition);
			}
		}
	}


    private class OnAlbumDataCardClickedListener implements View.OnClickListener {

        private final List<AlbumData> albums;

        public OnAlbumDataCardClickedListener(List<AlbumData> albums) {
            this.albums = albums;
        }

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
                AlbumData album = albums.get(clickedPosition);
                Intent articleDetailIntent = new Intent(getActivity(), ArticleDetailActivity.class);
                articleDetailIntent.putExtra(ArticleDetailActivity.MBID, album.getMbid());
                articleDetailIntent.putExtra(ArticleDetailActivity.ENTITY_TYPE, Entities.ALBUM.name());
                articleDetailIntent.putExtra(ArticleDetailActivity.ARTIST, album.getArtist());
                final CardView cardView = (CardView) view;
                final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        Objects.requireNonNull(getActivity()), sharedView, DetailsActivity.EXTRA_IMAGE);
                startActivity(articleDetailIntent, options.toBundle());
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
            }
        }
    }

	private class OnAlbumInfoCardClickedListener implements View.OnClickListener {

		private final List<AlbumInfo> albums;

		public OnAlbumInfoCardClickedListener(List<AlbumInfo> albums) {
			this.albums = albums;
		}

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
				AlbumInfo album = albums.get(clickedPosition);
				Intent articleDetailIntent = new Intent(getActivity(), ArticleDetailActivity.class);
				articleDetailIntent.putExtra(ArticleDetailActivity.MBID, album.getMbid());
				articleDetailIntent.putExtra(ArticleDetailActivity.ENTITY_TYPE, Entities.ALBUM.name());
				articleDetailIntent.putExtra(ArticleDetailActivity.ARTIST, album.getArtist());
				final CardView cardView = (CardView) view;
				final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
				ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
						Objects.requireNonNull(getActivity()), sharedView, DetailsActivity.EXTRA_IMAGE);
				startActivity(articleDetailIntent, options.toBundle());
			} else if (clickedPosition > activeCardPosition) {
				recyclerView.smoothScrollToPosition(clickedPosition);
			}
		}
	}

	private class OnArtistCardClickedListener implements View.OnClickListener {

		private final List<Artist> artists;

		public OnArtistCardClickedListener(List<Artist> artists) {
			this.artists = artists;
		}

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
				Artist artist = artists.get(clickedPosition);
				intent.putExtra(DetailsActivity.EXTRA_IMAGE, Util.getImageUrl(artist));
				intent.putExtra(DetailsActivity.EXTRA_TITLE, artist.getName());
				final CardView cardView = (CardView) view;
				final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
				ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
						Objects.requireNonNull(getActivity()), sharedView, DetailsActivity.EXTRA_IMAGE);
				startActivity(intent, options.toBundle());
			} else if (clickedPosition > activeCardPosition) {
				recyclerView.smoothScrollToPosition(clickedPosition);
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
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

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
	@Inject
	AlbumDataDao albumDataDao;
	private Map<String, AlbumData> albumDatasByName = new HashMap<>();
	private AlbumDetailViewModel albumDetailViewModel;
	@Inject
	AlbumDetailViewModelFactory albumDetailViewModelFactory;
	private ApplicationComponent applicationComponent;
	private long artistAnimDuration;
	@Inject
	ArtistDataDao artistDataDao;
	private Map<String, ArtistData> artistDatasByName = new HashMap<>();
	private ArtistDetailViewModel artistDetailViewModel;
	@Inject
	ArtistDetailViewModelFactory artistDetailViewModelFactory;
	private int artistOffset1;
	private int artistOffset2;
	@Inject
	BandItemRepository bandItemRepository;
	private int currentPosition;
	@BindView(R.id.descriptionLayout)
	View descriptionLayout;
	@BindView(R.id.ts_description)
	TextView descriptionTextView;
	private CardSliderLayoutManager layoutManger;
	OnMainActivityInteractionListener mCallback;
	@BindView(R.id.mainTitleLayout)
	View mainTitleLayout;
	@BindView(R.id.mainTitleView_1)
	TextView mainTitleView1;
	@BindView(R.id.mainTitleView_2)
	TextView mainTitleView2;
	@BindView(R.id.ts_place)
	TextView place;
	@BindView(R.id.progressBar)
	ProgressBar progressBar;
	@BindView(R.id.recycler_view)
	RecyclerView recyclerView;
	private SearchResultsViewModel searchResultsViewModel;
	@Inject
	SearchResultsViewModelFactory searchResultsViewModelFactory;
	private SliderAdapter sliderAdapter;
	private AlbumDataSliderAdapter albumDataSliderAdapter;
	@BindView(R.id.tagsLayout)
	View tagsLayout;

	public MusicItemsListFragment() {
		// Required empty public constructor
	}

    protected void buildAlbumDatas(@NonNull List<AlbumData> albumDatas) {
        if (null == albumDatas || albumDatas.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            mainTitleView1.setText(getString(R.string.informationUnavailable));
            hideFieldsExceptTitle();
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        initRecyclerViewForAlbumDatas(albumDatas, new OnAlbumDataCardClickedListener(albumDatas));
        initMusicItemNameTextForAlbumDatas(albumDatas);
        buildSwitchersForAlbumDatas(albumDatas);
    }

	private void buildAlbums(@NonNull List<AlbumInfo> albumInfos) {
		if (null == albumInfos || albumInfos.isEmpty()) {
			recyclerView.setVisibility(View.GONE);
			mainTitleView1.setText(getString(R.string.informationUnavailable));
			hideFieldsExceptTitle();
			return;
		}
		recyclerView.setVisibility(View.VISIBLE);
		initRecyclerView(albumInfos, new OnAlbumInfoCardClickedListener(albumInfos));
		initMusicItemNameText(albumInfos);
		buildSwitchers(albumInfos);
	}

	private void buildArtists(@NonNull List<Artist> artists) {
		if (null == artists || artists.isEmpty()) {
			recyclerView.setVisibility(View.GONE);
			mainTitleView1.setText(getString(R.string.informationUnavailable));
			hideFieldsExceptTitle();
			return;
		}
		recyclerView.setVisibility(View.VISIBLE);
		initRecyclerView(artists, new OnArtistCardClickedListener(artists));
		initMusicItemNameText(artists);
		buildSwitchers(artists);
	}


    private void buildSwitchersForAlbumDatas(@NonNull List<AlbumData> musicItems) {
        final AlbumData musicItem = musicItems.get(0);
        String name = musicItem.getName();
        String mbid = musicItem.getMbid();
        Log.d(TAG, String.format("musicItem: %s mbid: %s", name, mbid));
        processAlbumData(musicItem);
    }

	private void buildSwitchers(@NonNull List<? extends MusicItem> musicItems) {
		final MusicItem musicItem = musicItems.get(0);
		String name = musicItem.getName();
		String mbid = musicItem.getMbid();
		Log.d(TAG, String.format("musicItem: %s mbid: %s", name, mbid));
		if (musicItem instanceof Album) {
			Album album = (Album) musicItem;
			considerHandlingSavedAlbumInfo(album.getArtist().getName(), name);
		} else if (musicItem instanceof AlbumInfo) {
			AlbumInfo albumInfo = (AlbumInfo) musicItem;
			processAlbumInfo(albumInfo);
		} else {
			Artist artist = (Artist) musicItem;
			processArtistInfo(artist);
		}
	}

	private void considerHandlingSavedAlbumInfo(String artistName, String albumName) {
		String key = Util.getKeyFromArtistAndAlbum(artistName, albumName);
		AlbumData albumData = albumDatasByName.get(key);
		if (null != albumData && !albumData.getName().isEmpty()) {
			AlbumInfo albumInfoFromData = new AlbumInfo(albumData);
			processAlbumInfo(albumInfoFromData);
			return;
		}
		albumDetailViewModel.getAlbumInfo(artistName, albumName).observe(
				getActivity(), albumDetailedInfo -> populateAlbumInfo(albumDetailedInfo));
	}

	private void considerHandlingSavedArtistInfo(@NonNull Artist artistDetailedInfo) {
		String artistName = artistDetailedInfo.getName();
		ArtistData artistData = artistDatasByName.get(artistName);
		if (null != artistData && !artistData.getName().isEmpty()) {
			Artist artistFromData = new Artist(artistData);
			processArtistInfo(artistFromData);
			return;
		}
		artistDetailViewModel.saveArtist(artistDetailedInfo);
		searchResultsViewModel.getArtistInfo(artistName).observe(this, artist -> processArtistInfo(artist));
	}

	protected void displayAlbumsByArtist(@NonNull String artistName) {
		albumDetailViewModel.searchForAlbums(getActivity().getApplicationContext(), artistName);
	}

	protected void displayArtistsFromStorage(@NonNull List<Artist> artists) {
		populateArtists(artists);
	}

	protected void displaySearchResultsByArtist(@NonNull String artistName) {
		searchResultsViewModel.searchForArtist(getActivity().getApplicationContext(), artistName);
	}

	void handleRefreshing(Entities type, boolean isRefreshing) {
		Log.d(TAG, String.format("%s are refreshing: %s", type.name(), (isRefreshing ? true : false)));
		if (isRefreshing) {
			progressBar.setVisibility(View.VISIBLE);
			setVisibilityOnMainFields(View.GONE);
		} else {
			progressBar.setVisibility(View.GONE);
			setVisibilityOnMainFields(View.VISIBLE);
		}
	}


	public interface DetailsActivityCallback {
		void loadImageIntoToolbar(String extraImageUrl);
	}

    private void initMusicItemNameTextForAlbumDatas(@NonNull List<AlbumData> musicItems) {
		AlbumData albumData = musicItems.get(0);
		((DetailsActivityCallback) getActivity()).loadImageIntoToolbar(albumData.getImage());
        artistAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        artistOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
        artistOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
        mainTitleView1.setX(artistOffset1);
        mainTitleView2.setX(artistOffset2);
		String formattedTitle = formatAlbumDataTitle(albumData);
		mainTitleView1.setText(formattedTitle);
        mainTitleView2.setAlpha(0f);
        mainTitleView2.setText(formattedTitle);
    }

    private String formatAlbumDataTitle(@NonNull AlbumData albumData) {
		String musicItemName = albumData.getName();
		String artist = albumData.getArtist();
		return String.format("%s - %s", artist, musicItemName);
	}

	private void initMusicItemNameText(@NonNull List<? extends MusicItem> musicItems) {
		String musicItemName = musicItems.get(0).getName();
		artistAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
		artistOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
		artistOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
		mainTitleView1.setX(artistOffset1);
		mainTitleView2.setX(artistOffset2);
		mainTitleView1.setText(musicItemName);
		mainTitleView2.setAlpha(0f);
		mainTitleView2.setText(musicItemName);
	}


    private void initRecyclerViewForAlbumDatas(@NonNull List<AlbumData> albumDatas,
                                  @NonNull OnClickListener onClickListener) {
        if (null != albumDataSliderAdapter) {
            sliderAdapter.clear();
        }
		albumDataSliderAdapter = new AlbumDataSliderAdapter(applicationComponent, albumDatas, onClickListener);
		albumDataSliderAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(albumDataSliderAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChangeForAlbumDatas(albumDatas);
                }
            }
        });

        recyclerView.setLayoutManager(new CardSliderLayoutManager(Objects.requireNonNull(getActivity())));
        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        CARD_SNAP_HELPER.attachToRecyclerView(recyclerView);
    }

	private void initRecyclerView(@NonNull List<? extends BaseMusicItem> musicItems,
			@NonNull OnClickListener onClickListener) {
		if (null != sliderAdapter) {
			sliderAdapter.clear();
		}
		sliderAdapter = new SliderAdapter(applicationComponent, musicItems, onClickListener);
		sliderAdapter.notifyDataSetChanged();
		recyclerView.setAdapter(sliderAdapter);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					onActiveCardChange(musicItems);
				}
			}
		});

		recyclerView.setLayoutManager(new CardSliderLayoutManager(Objects.requireNonNull(getActivity())));
		layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

		CARD_SNAP_HELPER.attachToRecyclerView(recyclerView);
	}

	private void initSwitchers(@NonNull List<? extends MusicItem> musicItems) {
		final MusicItem musicItem = musicItems.get(0);
		String name = musicItem.getName();
		String mbid = musicItem.getMbid();
		Log.d(TAG, String.format("musicItem: %s mbid: %s", name, mbid));
		if (musicItem instanceof Album) {
			Album album = (Album) musicItem;
			considerHandlingSavedAlbumInfo(album.getArtist().getName(), name);
		} else {
			Artist artist = (Artist) musicItem;
			considerHandlingSavedArtistInfo(artist);
		}
	}

	private void onActiveCardChange(List<? extends MusicItem> musicItems) {
		final int pos = layoutManger.getActiveCardPosition();
		if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
			return;
		}

		onActiveCardChange(musicItems, pos);
	}

    private void onActiveCardChangeForAlbumDatas(List<AlbumData> musicItems) {
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }

        onActiveCardChangeForAlbumDatas(musicItems, pos);
    }

    private void onActiveCardChangeForAlbumDatas(List<AlbumData> albumDatas, int pos) {
	    AlbumData albumData = albumDatas.get(pos);
        String artistName = albumData.getArtist();
        String albumName = albumData.getName();
		((DetailsActivityCallback) getActivity()).loadImageIntoToolbar(albumData.getImage());
        Log.d(TAG, String.format("artistName: %s albumName: %s", artistName, albumName));
        considerHandlingSavedAlbumInfo(artistName, albumName);
        int animH[] = new int[]{R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[]{R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;
            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        setArtistText(formatAlbumDataTitle(albumDatas.get(pos % albumDatas.size())), left2right);

        currentPosition = pos;

    }

	private void onActiveCardChange(List<? extends MusicItem> musicItems, int pos) {
		MusicItem musicItem = musicItems.get(pos);
		String name = musicItem.getName();
		String mbid = musicItem.getMbid();
		Log.d(TAG, String.format("name: %s mbid: %s", name, mbid));
		if (musicItem instanceof Album) {
			Album album = (Album) musicItem;
			considerHandlingSavedAlbumInfo(album.getArtist().getName(), name);
		} else if (musicItem instanceof AlbumInfo) {
			AlbumInfo albumInfo = (AlbumInfo) musicItem;
			processAlbumInfo(albumInfo);
		} else {
			Artist artist = (Artist) musicItem;
			considerHandlingSavedArtistInfo(artist);
		}
		int animH[] = new int[]{R.anim.slide_in_right, R.anim.slide_out_left};
		int animV[] = new int[]{R.anim.slide_in_top, R.anim.slide_out_bottom};

		final boolean left2right = pos < currentPosition;
		if (left2right) {
			animH[0] = R.anim.slide_in_left;
			animH[1] = R.anim.slide_out_right;
			animV[0] = R.anim.slide_in_bottom;
			animV[1] = R.anim.slide_out_top;
		}

		setArtistText(musicItems.get(pos % musicItems.size()).getName(), left2right);

		currentPosition = pos;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
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
		albumDetailViewModel = ViewModelProviders.of(this, albumDetailViewModelFactory)
				.get(AlbumDetailViewModel.class);
		artistDetailViewModel = ViewModelProviders.of(this, artistDetailViewModelFactory)
				.get(ArtistDetailViewModel.class);
		searchResultsViewModel.getArtistsLiveDataObservable().observe(this,
				artists -> buildArtists(artists));
		searchResultsViewModel.getIsRefreshingObservable().observe(this,
				isRefreshing -> handleRefreshing(Entities.ARTIST, isRefreshing));
		albumDetailViewModel.getIsRefreshingObservable().observe(this,
				isRefreshing -> handleRefreshing(Entities.ALBUM, isRefreshing));
		albumDetailViewModel.getAlbumsLiveDataObservable().observe(this,
				albums -> buildAlbums(albums));
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	void populateAlbumDatas(List<AlbumData> albumDatas) {
		for (AlbumData albumData : albumDatas) {
			albumDatasByName.put(Util.getKeyFromAlbumData(albumData), albumData);
		}
		Log.d(TAG, "albumDatas: " + albumDatas);
	}

	private void populateAlbumInfo(@NonNull AlbumInfo albumInfo) {
		albumDetailViewModel.saveTracks(albumInfo);
		albumDetailViewModel.saveAlbumData(albumInfo);
		processAlbumInfo(albumInfo);
	}

	private void populateAlbums(List<Album> albums) {
		if (albums.isEmpty()) return;
		initRecyclerView(albums, new OnAlbumCardClickedListener(albums));
		initMusicItemNameText(albums);
		initSwitchers(albums);
	}

	private void populateArtists(@NonNull List<Artist> artists) {
		if (null == artists || artists.isEmpty()) {
			recyclerView.setVisibility(View.GONE);
			mainTitleView1.setText(getString(R.string.informationUnavailable));
			return;
		}
		recyclerView.setVisibility(View.VISIBLE);
		initRecyclerView(artists, new OnArtistCardClickedListener(artists));
		initMusicItemNameText(artists);
		initSwitchers(artists);
	}

	protected void populateArtistsByTag(@NonNull String tag) {
//        searchResultsViewModel.getTopArtistsByTag(tag).observe(this, this::populateArtists);
		searchResultsViewModel.searchForArtistByTag(getActivity().getApplicationContext(), tag);
	}

	protected void populateTopAlbums() {
		Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
		detailsIntent.putExtra(DetailsActivity.EXTRA_IMAGE, "");
		detailsIntent.putExtra(DetailsActivity.EXTRA_TITLE, getString(R.string.topAlbums));
		startActivity(detailsIntent);
	}

	protected void populateTopArtists() {
		searchResultsViewModel.searchForTopArtists(getActivity().getApplicationContext());
	}

	private void processAlbumData(@NonNull AlbumData albumData) {
		mainTitleView1.setText(formatAlbumDataTitle(albumData));
		mainTitleView2.setText(formatAlbumDataTitle(albumData));
		place.setText(albumData.getTags());
		updateDescriptionsSwitcher(albumData);
	}


	private void processAlbumInfo(@NonNull AlbumInfo albumInfo) {
		String tagsText = null != albumInfo.getTagWrapper() ? Util.getTagsAsString(albumInfo.getTagWrapper().getTags()) : "";
		place.setText(tagsText);
		final Wiki wiki = albumInfo.getWiki();
		if (null != wiki) {
			updateDescriptionsSwitcher(albumInfo.getArtist(), albumInfo.getName(), albumInfo.getMbid(), wiki.getSummary(), Entities.ALBUM);
		}
	}

	private void processArtistInfo(Artist artist) {
		if (null == artist) {
			return;
		}
		String tagsText = Util.getTagsAsString(artist.getTagWrapper().getTags());
		place.setText(tagsText);
		updateDescriptionsSwitcher(artist.getName(), "", artist.getMbid(), artist.getBio().getSummary(), Entities.ARTIST);
	}

	private void setArtistText(String text, boolean left2right) {
		final TextView invisibleText;
		final TextView visibleText;
		if (mainTitleView1.getAlpha() > mainTitleView2.getAlpha()) {
			visibleText = mainTitleView1;
			invisibleText = mainTitleView2;
		} else {
			visibleText = mainTitleView2;
			invisibleText = mainTitleView1;
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

	public void setOnFragmentInteractionListener(OnMainActivityInteractionListener onFragmentInteractionListener) {
		mCallback = onFragmentInteractionListener;
	}

	private void setVisibilityOnMainFields(int visibility) {
		recyclerView.setVisibility(visibility);
		mainTitleLayout.setVisibility(visibility);
		tagsLayout.setVisibility(visibility);
		descriptionLayout.setVisibility(visibility);
	}

	private void hideFieldsExceptTitle() {
		recyclerView.setVisibility(View.GONE);
		tagsLayout.setVisibility(View.GONE);
		descriptionLayout.setVisibility(View.GONE);
	}


	private void updateDescriptionsSwitcher(AlbumData albumData) {
		String wiki = albumData.getWiki();
		if (!wiki.isEmpty()) {
			descriptionTextView.setText(R.string.summaryUnavailable);
		} else {
			Util.populateHTMLForTextView(descriptionTextView, wiki);
			Util.makeTextViewResizable(descriptionTextView, 3, getString(R.string.readMore), true);
		}
		if (!wiki.isEmpty()) {
			descriptionTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent articleDetailIntent = new Intent(getActivity(), ArticleDetailActivity.class);
					articleDetailIntent.putExtra(ArticleDetailActivity.MBID, albumData.getMbid());
					articleDetailIntent.putExtra(ArticleDetailActivity.ENTITY_TYPE, Entities.ALBUM.name());
					articleDetailIntent.putExtra(ArticleDetailActivity.ARTIST, albumData.getArtist());
					articleDetailIntent.putExtra(ArticleDetailActivity.ALBUM, albumData.getName());
					startActivity(articleDetailIntent);
				}
			});
		}
	}

	private void updateDescriptionsSwitcher(String artist, String album, String mbid, String text,
			Entities type) {

		if (text.isEmpty()) {
			descriptionTextView.setText(R.string.summaryUnavailable);
		} else {
			Util.populateHTMLForTextView(descriptionTextView, text);
			Util.makeTextViewResizable(descriptionTextView, 3, getString(R.string.readMore), true);
		}
		if (!text.isEmpty()) {
			descriptionTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent articleDetailIntent = new Intent(getActivity(), ArticleDetailActivity.class);
					articleDetailIntent.putExtra(ArticleDetailActivity.MBID, mbid);
					articleDetailIntent.putExtra(ArticleDetailActivity.ENTITY_TYPE, type.name());
					articleDetailIntent.putExtra(ArticleDetailActivity.ARTIST, artist);
					articleDetailIntent.putExtra(ArticleDetailActivity.ALBUM, album);
					startActivity(articleDetailIntent);
				}
			});
		}
	}

}
