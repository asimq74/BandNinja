package com.asimq.artists.bandninja;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.BaseMusicItem;
import com.asimq.artists.bandninja.json.Image;
import com.asimq.artists.bandninja.json.MusicItem;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;
import com.asimq.artists.bandninja.ui.Executable;
import com.asimq.artists.bandninja.utils.DecodeBitmapTask;
import com.asimq.artists.bandninja.viewmodelfactories.AlbumDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicItemsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicItemsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicItemsListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public final CardSnapHelper CARD_SNAP_HELPER = new CardSnapHelper();
    final String TAG = this.getClass().getSimpleName();
    private final int[][] dotCoords = new int[5][2];
    private final int[] maps = {R.drawable.map_paris, R.drawable.map_seoul, R.drawable.map_london, R.drawable.map_beijing};
    @BindView(R.id.mainTitleView_1)
    TextView mainTitleView1;
    @BindView(R.id.mainTitleView_2)
    TextView mainTitleView2;
    @Inject
    ArtistDataDao artistDataDao;
    @Inject
    ArtistTagDao artistTagDao;
    @BindView(R.id.blueTabLayout)
    View blueTabLayout;
    @BindView(R.id.ts_clock)
    TextSwitcher clockSwitcher;
    @BindView(R.id.ts_description)
    TextSwitcher descriptionsSwitcher;
    @BindView(R.id.green_dot)
    View greenDot;
    OnFragmentInteractionListener mCallback;
    @BindView(R.id.ts_map)
    ImageSwitcher mapSwitcher;
    @BindView(R.id.ts_place)
    TextSwitcher placeSwitcher;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @Inject
    SearchResultsViewModelFactory searchResultsViewModelFactory;
    @Inject
    AlbumDetailViewModelFactory albumDetailViewModelFactory;
    @BindView(R.id.ts_temperature)
    TextSwitcher temperatureSwitcher;
    private ApplicationComponent applicationComponent;
    private long artistAnimDuration;
    private int artistOffset1;
    private int artistOffset2;
    private int currentPosition;
    private DecodeBitmapTask decodeMapBitmapTask;
    private CardSliderLayoutManager layoutManger;
    private DecodeBitmapTask.Listener mapLoadListener;
    private SearchResultsViewModel searchResultsViewModel;
    private final Executable<String> artistInfoObservable = createArtistInfoObservable();
    private AlbumDetailViewModel albumDetailViewModel;
    private SliderAdapter sliderAdapter;

    public MusicItemsListFragment() {
        // Required empty public constructor
    }

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

    protected void displaySearchResultsByArtist(@NonNull String artistName) {
        searchResultsViewModel.getSearchResultsByArtist(artistName).observe(this,
                this::populateArtists);
    }

    protected void displayAlbumsByArtist(@NonNull String artistName) {
        albumDetailViewModel.getAlbumsByArtist(artistName).observe(this, this::populateAlbums);
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

                greenDot.setX(dotCoords[0][0]);
                greenDot.setY(dotCoords[0][1]);
            }
        });
    }

    private void initRecyclerView(@NonNull List<? extends BaseMusicItem> musicItems,
                                  @NonNull Executable<String> furtherAction,
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
                    onActiveCardChange(musicItems, furtherAction);
                }
            }
        });

        recyclerView.setLayoutManager(new CardSliderLayoutManager(Objects.requireNonNull(getActivity())));
        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        CARD_SNAP_HELPER.attachToRecyclerView(recyclerView);
    }

    private void initRecyclerViewForAlbums(@NonNull List<Album> albums, Executable<String> furtherAction) {
        if (null != sliderAdapter) {
            sliderAdapter.clear();
        }
        sliderAdapter = new SliderAdapter(applicationComponent, albums, (view) ->{});
        sliderAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange(albums, furtherAction);
                }
            }
        });

        recyclerView.setLayoutManager(new CardSliderLayoutManager(Objects.requireNonNull(getActivity())));
        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        CARD_SNAP_HELPER.attachToRecyclerView(recyclerView);
    }

    private void initSwitchers(@NonNull List<? extends BaseMusicItem> musicItems, Executable<String> furtherActions) {
        String name = musicItems.get(0).getName();
        String mbid = musicItems.get(0).getMbid();
        Log.d(TAG, String.format("musicItem: %s mbid: %s", name, mbid));
        furtherActions.executeWith(name);

//        temperatureSwitcher.removeAllViews();
//        temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
//        temperatureSwitcher.setCurrentText(musicItems.get(0).getListeners() + "");

        clockSwitcher.removeAllViews();
        clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
        clockSwitcher.setCurrentText(musicItems.get(0).getName());

        descriptionsSwitcher.removeAllViews();
        descriptionsSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
        descriptionsSwitcher.setCurrentText("");

        mapSwitcher.removeAllViews();
        mapSwitcher.setInAnimation(getActivity(), R.anim.fade_in);
        mapSwitcher.setOutAnimation(getActivity(), R.anim.fade_out);
        mapSwitcher.setFactory(new ImageViewFactory());
        mapSwitcher.setImageResource(maps[0]);

        mapLoadListener = bitmap -> {
            ((ImageView) mapSwitcher.getNextView()).setImageBitmap(bitmap);
            mapSwitcher.showNext();
        };
    }

    private void onActiveCardChange(List<? extends MusicItem> musicItems, Executable<String> furtherAction) {
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }

        onActiveCardChange(musicItems, pos, furtherAction);
    }

    private void onActiveCardChange(List<? extends MusicItem> musicItems, int pos, Executable<String> furtherAction) {
        MusicItem musicItem = musicItems.get(pos);
        String name = musicItem.getName();
        String mbid = musicItem.getMbid();
        Log.d(TAG, String.format("name: %s mbid: %s", name, mbid));
        if (musicItem instanceof Album) {
            Album album = (Album) musicItem;
            furtherAction.executeWith(album.getArtist().getName(), name);
        } else {
            furtherAction.executeWith(name);
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

        temperatureSwitcher.setInAnimation(getActivity(), animH[0]);
        temperatureSwitcher.setOutAnimation(getActivity(), animH[1]);
        //replace with progress bar
//		temperatureSwitcher.setText(musicItems.get(pos % musicItems.size()).getName());

        placeSwitcher.setInAnimation(getActivity(), animV[0]);
        placeSwitcher.setOutAnimation(getActivity(), animV[1]);
//replace with progress bar
//		placeSwitcher.setText(musicItems.get(pos % musicItems.size()).getName());

        clockSwitcher.setInAnimation(getActivity(), animV[0]);
        clockSwitcher.setOutAnimation(getActivity(), animV[1]);
        // replace with progress bar
//		clockSwitcher.setText(musicItems.get(pos % musicItems.size()).getName());

        // replace with progress bar
//		descriptionsSwitcher.setText(musicItems.get(pos % musicItems.size()).getName());

        showMap(maps[pos % maps.length]);

        ViewCompat.animate(greenDot)
                .translationX(dotCoords[pos % dotCoords.length][0])
                .translationY(dotCoords[pos % dotCoords.length][1])
                .start();

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
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity().isFinishing() && decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true);
        }
    }

    private void populateArtistInfo(@NonNull Artist artistDetailedInfo) {
        ArtistData artistData = new ArtistData(artistDetailedInfo);
        Log.d(TAG, String.format("artistData: %s", artistData));
        new BaseSaveArtistTask(artistDataDao).execute(artistData);
        searchResultsViewModel.getArtistInfo(artistData.getName()).observe(this,
                artist -> processArtistInfo(artist));
    }

    private void populateArtists(List<Artist> artists) {
        blueTabLayout.setVisibility(View.GONE);
        initRecyclerView(artists, artistInfoObservable, new OnArtistCardClickedListener(artists));
        initMusicItemNameText(artists);
        initSwitchers(artists, artistInfoObservable);
        initGreenDot();
    }

    @NonNull
    private Executable<String> createArtistInfoObservable() {
        return params -> searchResultsViewModel.getArtistInfo(params[0]).observe(
                getActivity(), artistDetailedInfo -> populateArtistInfo(artistDetailedInfo));
    }

    
    private final Executable<String> albumInfoObservable = createAlbumInfoObservable();
    
    @NonNull
    private Executable<String> createAlbumInfoObservable() {
        return params -> albumDetailViewModel.getAlbumInfo(params[0], params[1]).observe(
                getActivity(), albumDetailedInfo -> populateAlbumInfo(albumDetailedInfo));
    }

    private void populateAlbumInfo(@NonNull AlbumInfo albumInfo) {
        Log.i(TAG, "albumInfo=" + albumInfo);
    }

    private void populateAlbums(List<Album> albums) {
        blueTabLayout.setVisibility(View.GONE);
        initRecyclerView(albums, albumInfoObservable, view -> {
        });
        initMusicItemNameText(albums);
        initSwitchers(albums, (params) -> {
        });
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
        placeSwitcher.removeAllViews();
        placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
        placeSwitcher.setCurrentText(tagsText);

        descriptionsSwitcher.removeAllViews();
        descriptionsSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
        descriptionsSwitcher.setCurrentText(artist.getBio().getSummary());
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

    public void setOnFragmentInteractionListener(OnFragmentInteractionListener onFragmentInteractionListener) {
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
        void onSearchedForArtistName(@NonNull String artistName);
    }


    public interface OnDetailsInteractionListener {
        void onDisplayAlbumsByArtist(@NonNull String artistName);
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

    private class OnArtistCardClickedListener implements View.OnClickListener {
        private final List<Artist> artists;

        public OnArtistCardClickedListener(List<Artist> artists) {
            this.artists = artists;
        }


        private String getImageUrl(@NonNull Artist artist) {
            for (Image image : artist.getImages()) {
                if ("mega".equals(image.getSize())) {
                    return image.getText();
                }
            }
            return "";
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
                intent.putExtra(DetailsActivity.EXTRA_IMAGE, getImageUrl(artist));
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
}
