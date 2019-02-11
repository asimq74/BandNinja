package com.asimq.artists.bandninja;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.asimq.artists.bandninja.MusicItemsListFragment.OnDetailsInteractionListener;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.ui.HeaderView;
import com.asimq.artists.bandninja.utils.Util;
import com.asimq.artists.bandninja.viewmodelfactories.AlbumDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements OnDetailsInteractionListener,
        AppBarLayout.OnOffsetChangedListener, MusicItemsListFragment.DetailsActivityCallback {


    public static final String EXTRA_IMAGE = "extraImage";
    public static final String EXTRA_TITLE = "extraTitle";
    final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.float_header_view)
    HeaderView floatHeaderView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_header_view)
    HeaderView toolbarHeaderView;
    @Inject
    AlbumDetailViewModelFactory albumDetailViewModelFactory;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.title)
    TextView titleView;
    private boolean isHideToolbarView = false;
    private ApplicationComponent applicationComponent;
    private AlbumDetailViewModel albumDetailViewModel;
    private Location mLocation = null;

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
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
        ButterKnife.bind(this);
        final MyApplication application = (MyApplication) getApplicationContext();
        applicationComponent = application.getApplicationComponent();
        applicationComponent.inject(this);
        albumDetailViewModel = ViewModelProviders.of(this, albumDetailViewModelFactory)
                .get(AlbumDetailViewModel.class);
        albumDetailViewModel.getAllAlbumDatas().observe(this,
                albumDatas -> onDisplayAlbumDatas(albumDatas));
        collapsingToolbarLayout.setTitle(" ");
        appBarLayout.addOnOffsetChangedListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String itemTitle = getIntent().getStringExtra(EXTRA_TITLE);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(itemTitle);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        toolbarHeaderView.bindTo(itemTitle, "", "");
        floatHeaderView.bindTo(itemTitle, "", "");
        String extraImageUrl = getIntent().getStringExtra(EXTRA_IMAGE);
        loadImageIntoToolbar(extraImageUrl);
        if (itemTitle.equals(getString(R.string.topAlbums))) {
            onDisplayTopAlbums();
            return;
        }
        onDisplayAlbumsByArtist(itemTitle);
    }

    @BindView(R.id.image)
    ImageView image;

    @Override
    public void loadImageIntoToolbar(String extraImageUrl) {
        if (!extraImageUrl.isEmpty()) {
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
        }
    }

    private void onDisplayTopAlbums() {
        albumDetailViewModel.obtainAllAlbumDatas(getApplicationContext());
    }

    private void onDisplayAlbumDatas(List<AlbumData> albumDatas) {
        MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
                getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
        if (musicItemsListFragment != null) {
            musicItemsListFragment.buildAlbumDatas(
                    albumDatas.subList(0, albumDatas.size() < 10 ? albumDatas.size() : 10));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String locationFromPreferences = sharedPreferences.getString("location", null);
        StringBuilder titleViewBuilder = new StringBuilder(defaultSharedPreferences.getString(getString(R.string.display_name_key), ""));
        if (null != locationFromPreferences) {
            mLocation = new Gson().fromJson(locationFromPreferences, Location.class);
            String localityAndPostalCode = Util.getLocalityAndPostalCode(this, mLocation.getLatitude(), mLocation.getLongitude());
            titleViewBuilder.append("\n").append(localityAndPostalCode);
        }
        titleView.setText(titleViewBuilder.toString());
    }

    @Override
    public void onDisplayAlbumsByArtist(@NonNull String artistName) {
        MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
                getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
        if (musicItemsListFragment != null) {
            musicItemsListFragment.displayAlbumsByArtist(artistName);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            toolbarHeaderView.findViewById(R.id.header_view_author).setVisibility(View.GONE);
            toolbarHeaderView.findViewById(R.id.header_view_published_date).setVisibility(View.GONE);
            final TextView titleView = toolbarHeaderView.findViewById(R.id.header_view_title);
            titleView.setTextAppearance(this,
                    getResources().getBoolean(R.bool.isTablet) ?
                            android.R.style.TextAppearance_Material_Headline :
                            android.R.style.TextAppearance_Material_Subhead);
            titleView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

}