package com.asimq.artists.bandninja;

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
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.asimq.artists.bandninja.cards.SliderAdapter;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.BaseMusicItem;
import com.asimq.artists.bandninja.json.MusicItem;
import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.json.Wiki;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.dao.AlbumDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;
import com.asimq.artists.bandninja.ui.Executable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

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

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public final CardSnapHelper CARD_SNAP_HELPER = new CardSnapHelper();
    final String TAG = this.getClass().getSimpleName();
    @Inject
    AlbumDataDao albumDataDao;
    @Inject
    AlbumDetailViewModelFactory albumDetailViewModelFactory;
    @Inject
    ArtistDataDao artistDataDao;
    @Inject
    ArtistDetailViewModelFactory artistDetailViewModelFactory;
    @Inject
    ArtistTagDao artistTagDao;
    @Inject
    BandItemRepository bandItemRepository;
    @BindView(R.id.ts_description)
    TextSwitcher descriptionsSwitcher;
    OnMainActivityInteractionListener mCallback;
    @BindView(R.id.mainTitleView_1)
    TextView mainTitleView1;
    @BindView(R.id.mainTitleView_2)
    TextView mainTitleView2;
    @BindView(R.id.ts_place)
    TextSwitcher placeSwitcher;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @Inject
    SearchResultsViewModelFactory searchResultsViewModelFactory;
    @BindView(R.id.tracksRecyclerView)
    RecyclerView tracksRecyclerView;
    private AlbumDetailViewModel albumDetailViewModel;
    private final Executable<String> albumInfoObservable = createAlbumInfoObservable();
    private ApplicationComponent applicationComponent;
    private long artistAnimDuration;
    private ArtistDetailViewModel artistDetailViewModel;
    private int artistOffset1;
    private int artistOffset2;
    private int currentPosition;
    private CardSliderLayoutManager layoutManger;
    private SearchResultsViewModel searchResultsViewModel;
    private final Executable<String> artistInfoObservable = createArtistInfoObservable();
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

    @NonNull
    private Executable<String> createAlbumInfoObservable() {
        return params -> albumDetailViewModel.getAlbumInfo(params[0], params[1]).observe(
                getActivity(), albumDetailedInfo -> populateAlbumInfo(albumDetailedInfo));
    }

    @NonNull
    private Executable<String> createArtistInfoObservable() {
        return params -> searchResultsViewModel.getArtistInfo(params[0]).observe(
                getActivity(), artistDetailedInfo -> populateArtistInfo(artistDetailedInfo));
    }

    protected void displayAlbumsByArtist(@NonNull String artistName) {
        albumDetailViewModel.getAlbumsByArtist(artistName).observe(this, this::populateAlbums);
    }

    protected void displayArtistsFromStorage(@NonNull List<Artist> artists) {
        populateArtists(artists);
    }

    protected void displaySearchResultsByArtist(@NonNull String artistName) {
        searchResultsViewModel.getSearchResultsByArtist(artistName).observe(this,
                this::populateArtists);
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
//		recyclerView.post(new Runnable() {
//			@Override
//			public void run() {
//				// Call smooth scroll
//				recyclerView.smoothScrollToPosition(sliderAdapter.getItemCount() - 1);
//			}
//		});
    }

    private void initSwitchers(@NonNull List<? extends MusicItem> musicItems, Executable<String> furtherAction) {
        final MusicItem musicItem = musicItems.get(0);
        String name = musicItem.getName();
        String mbid = musicItem.getMbid();
        Log.d(TAG, String.format("musicItem: %s mbid: %s", name, mbid));
        if (musicItem instanceof Album) {
            Album album = (Album) musicItem;
            furtherAction.executeWith(album.getArtist().getName(), name);
        } else {
            furtherAction.executeWith(name);
        }

        descriptionsSwitcher.removeAllViews();
        descriptionsSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
        descriptionsSwitcher.setCurrentText("");
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

        placeSwitcher.setInAnimation(getActivity(), animV[0]);
        placeSwitcher.setOutAnimation(getActivity(), animV[1]);

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
        searchResultsViewModel.getArtistsRefreshingMutableLiveData().observe(this, loading -> {
            if (loading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Log.d(TAG, "loading progress began...");
                return;
            }
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "loading progress ended...");
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void populateAlbumInfo(@NonNull AlbumInfo albumInfo) {
        albumDetailViewModel.saveTracks(albumInfo);
        albumDetailViewModel.saveAlbumData(albumInfo);
        processAlbumInfo(albumInfo);
    }

    private void populateAlbums(List<Album> albums) {
        if (albums.isEmpty()) return;
        initRecyclerView(albums, albumInfoObservable, new OnAlbumCardClickedListener(albums));
        initMusicItemNameText(albums);
        initSwitchers(albums, albumInfoObservable);
    }

    private void populateAlbumsFromAlbumDatas(List<AlbumData> albumDatas) {
        List<Album> albums = new ArrayList<>();
        for (AlbumData albumdata : albumDatas) {
            albums.add(new Album(albumdata));
        }
        populateAlbums(albums);
    }

    private void populateArtistInfo(@NonNull Artist artistDetailedInfo) {
        if (null == artistDetailedInfo) {
            recyclerView.setVisibility(View.GONE);
            mainTitleView1.setText(getString(R.string.informationUnavailable));
            return;
        }
        if (!Util.isConnected(getActivity())) {
            return;
        }
        artistDetailViewModel.saveArtist(artistDetailedInfo);
        searchResultsViewModel.getArtistInfo(artistDetailedInfo.getName()).observe(this,
                artist -> processArtistInfo(artist));
    }

    private void populateArtists(@NonNull List<Artist> artists) {
        if (null == artists || artists.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            mainTitleView1.setText(getString(R.string.informationUnavailable));
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        initRecyclerView(artists, artistInfoObservable, new OnArtistCardClickedListener(artists));
        initMusicItemNameText(artists);
        initSwitchers(artists, artistInfoObservable);
    }

    protected void populateArtistsByTag(@NonNull String tag) {
        searchResultsViewModel.getTopArtistsByTag(tag).observe(this, this::populateArtists);
    }

    protected void populateTopAlbums() {
//        albumDataDao.fetchLiveAlbumDatas().observe(this, this::populateAlbumsFromAlbumDatas);
    }

    protected void populateTopArtists() {
        searchResultsViewModel.getTopArtists().observe(this, this::populateArtists);
    }

    private void populateTracksRecyclerView(@NonNull List<Track> tracks) {
        TracksAdapter adapter = new TracksAdapter(tracks);
        adapter.setHasStableIds(true);
        tracksRecyclerView.setAdapter(adapter);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        tracksRecyclerView.setLayoutManager(sglm);
    }

    private void processAlbumInfo(@NonNull AlbumInfo albumInfo) {
        String tagsText = null != albumInfo.getTagWrapper() ? Util.getTagsAsString(albumInfo.getTagWrapper().getTags()) : "";
        placeSwitcher.removeAllViews();
        placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
        placeSwitcher.setCurrentText(tagsText);

        descriptionsSwitcher.removeAllViews();
        descriptionsSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
        final Wiki wiki = albumInfo.getWiki();
        if (null != wiki) {
            updateDescriptionsSwitcher(albumInfo.getArtist(), albumInfo.getName(), albumInfo.getMbid(), wiki.getSummary(), Entities.ALBUM);
        }
        final List<Track> tracks = albumInfo.getTrackWrapper().getTracks();
        tracksRecyclerView.setVisibility(View.VISIBLE);
        populateTracksRecyclerView(tracks);
    }

    private void processArtistInfo(Artist artist) {
        String tagsText = Util.getTagsAsString(artist.getTagWrapper().getTags());
        placeSwitcher.removeAllViews();
        placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
        placeSwitcher.setCurrentText(tagsText);
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

    private void updateDescriptionsSwitcher(String artist, String album, String mbid, String text,
                                            Entities type) {
        descriptionsSwitcher.removeAllViews();
        descriptionsSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
        StringBuilder abbreviatedSummaryBuilder = new StringBuilder();
        if (text.isEmpty()) {
            abbreviatedSummaryBuilder.append(getString(R.string.summaryUnavailable));
        } else {
            abbreviatedSummaryBuilder.append(text.length() > 100 ?
                    text.substring(0, 100) : text).append("\n")
                    .append(getString(R.string.readMore));
        }
        Util.populateHTMLForSwitcher(descriptionsSwitcher, abbreviatedSummaryBuilder.toString());
        TextView tv = (TextView) descriptionsSwitcher.getCurrentView();
        if (!text.isEmpty()) {
            tv.setOnClickListener(new OnClickListener() {
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
            ;
        }
    }

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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView trackListItemView;

        public ViewHolder(View view) {
            super(view);
            trackListItemView = view.findViewById(R.id.trackListItemView);
        }
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

    private class TracksAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Track> tracks;

        public TracksAdapter(List<Track> tracks) {
            this.tracks = tracks;
        }

        @Override
        public int getItemCount() {
            return tracks.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Track track = tracks.get(position);
            holder.trackListItemView.setText(String.format("%s. %s - %s", position + 1, track.getName(), track.getDuration()));
            holder.trackListItemView.setMovementMethod(LinkMovementMethod.getInstance());

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.track_list_item_detail, parent, false));
        }

    }

}
