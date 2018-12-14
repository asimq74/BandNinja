package com.asimq.artists.bandninja;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TopAlbumsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetArtists;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.ui.HeaderView;
import com.asimq.artists.bandninja.viewmodelfactories.ArtistDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.ArtistDetailViewModel;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    public static final String API_KEY = BuildConfig.LastFMApiKey;
    public static final String DEFAULT_FORMAT = "json";
    final String TAG = this.getClass().getSimpleName();
    @Inject
    ArtistDetailViewModelFactory artistDetailViewModelFactory;
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.summary)
    TextView summary;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.float_header_view)
    HeaderView floatHeaderView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_header_view)
    HeaderView toolbarHeaderView;
    @BindView(R.id.cardView)
    CardView cardView;
    private ArtistDetailViewModel artistDetailViewModel;
    private boolean isHideToolbarView = false;
    private String mbid = "";

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

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground(findViewById(R.id.fab), palette);
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorPrimaryLight));
        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    private void getAlbumInfo(final String artistName, final GetArtists service) {
        Call<ArtistWrapper> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
                API_KEY, DEFAULT_FORMAT);
        artistInfoCall.enqueue(new Callback<ArtistWrapper>() {

            @Override
            public void onFailure(Call<ArtistWrapper> call, Throwable t) {
                Log.e(TAG, "error calling service", t);
                Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call<ArtistWrapper> call, Response<ArtistWrapper> response) {
                final ArtistWrapper artistWrapper = response.body();
                if (artistWrapper == null) {
                    return;
                }

                getTopAlbumInfo(artistWrapper, service, artistName);
            }
        });
    }

    private void getArtistTagInfo(final String mbid, final GetArtists service) {
        Call<Tag[]> artistTagInfoCall = service.getTagByArtistId("artist.getTag", mbid, API_KEY, "RJ", DEFAULT_FORMAT);
        artistTagInfoCall.enqueue(new Callback<Tag[]>() {

            @Override
            public void onFailure(Call<Tag[]> call, Throwable t) {
                Log.e(TAG, "error calling service", t);
                Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call<Tag[]> call, Response<Tag[]> response) {
                final Tag[] tags = response.body();
                if (tags == null) {
                    return;
                }
                Log.d(TAG, String.format("\n\ntags for %s : %s", mbid, tags));
            }
        });
    }

    private void getTopAlbumInfo(final ArtistWrapper artistWrapper, GetArtists service, final String artistName) {
        Call<TopAlbumsWrapper> topAlbumsPojoCall = service.getTopAlbums("artist.gettopalbums", artistName,
                API_KEY, DEFAULT_FORMAT, 1);
        topAlbumsPojoCall.enqueue(new Callback<TopAlbumsWrapper>() {
            @Override
            public void onFailure(Call<TopAlbumsWrapper> call, Throwable t) {
                Log.e(TAG, "error calling service", t);
                Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call<TopAlbumsWrapper> call, Response<TopAlbumsWrapper> response) {
                final TopAlbumsWrapper topAlbumsWrapper = response.body();
                if (topAlbumsWrapper == null) {
                    return;
                }

                Log.d(TAG, String.format("\n\ntop albums for %s (%s items)", artistName, topAlbumsWrapper.getTopalbums().getAlbums().size()));
                for (Album album : topAlbumsWrapper.getTopalbums().getAlbums()) {
                    Log.d(TAG, String.format("\t%s - %s\n", album.getName(), album.getArtist().getName()));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                mbid = getIntent().getStringExtra(MainNavigationActivity.MB_ID);
            }
        } else {
            mbid = savedInstanceState.getString(MainNavigationActivity.MB_ID);
        }
        setContentView(R.layout.result_activity_main);
        ButterKnife.bind(this);
        final MyApplication application = (MyApplication) getApplicationContext();
        application.getApplicationComponent().inject(this);
        collapsingToolbarLayout.setTitle(" ");
        appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        artistDetailViewModel = ViewModelProviders.of(this, artistDetailViewModelFactory)
                .get(ArtistDetailViewModel.class);
        artistDetailViewModel.getArtistDetail(mbid).observe(this, artistDetail -> populateUI(artistDetail));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
//		final GetArtists service = RetrofitClientInstance.getRetrofitInstance().create(GetArtists.class);
//		Call<ResultsWrapper> call = service.getArtists("artist.search", "The Cult",
//				API_KEY, DEFAULT_FORMAT);
//		call.enqueue(new Callback<ResultsWrapper>() {
//
//			@Override
//			public void onFailure(Call<ResultsWrapper> call, Throwable t) {
//				Log.e(TAG, "error calling service", t);
//				Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();
//			}
//
//			@Override
//			public void onResponse(Call<ResultsWrapper> call, Response<ResultsWrapper> response) {
//				final ResultsWrapper artistPojo = response.body();
//				if (artistPojo == null) {
//					return;
//				}
//
//				getAlbumInfo("The Cult", service);
//				for (Artist artist : artistPojo.getResult().getArtistmatches().getArtists()) {
//					final String artistName = artist.getName();
//					getAlbumInfo(artistName, service);
//					getArtistTagInfo(artist.getMbid(), service);
//				}
//			}
//		});

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MainNavigationActivity.MB_ID, mbid);
        super.onSaveInstanceState(outState);
    }

    private void populateUI(@NonNull ArtistData artistDetail) {
        toolbarHeaderView.bindTo(artistDetail.getName(), artistDetail.getName(), artistDetail.getName());
        floatHeaderView.bindTo(artistDetail.getName(), artistDetail.getName(), artistDetail.getName());
        summary.setText(Html.fromHtml(artistDetail.getBio()
                .replaceAll("(\r\n\r\n)", "<p/>")
                .replaceAll("(\r\n)", " ")));
        summary.setMovementMethod(LinkMovementMethod.getInstance());
        final String image = artistDetail.getImage();
        Picasso.with(this).load(image).into(
                photo, new com.squareup.picasso.Callback() {
                    @Override
                    public void onError() {
                        Log.e(TAG, "error loading photoView with url: " + image);
                    }

                    private void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }

                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(this::onGenerated);
                    }
                });
    }

}
