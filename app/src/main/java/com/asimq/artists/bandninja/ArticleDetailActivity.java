package com.asimq.artists.bandninja;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asimq.artists.bandninja.json.Track;
import com.asimq.artists.bandninja.room.AlbumData;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.TrackData;
import com.asimq.artists.bandninja.room.dao.TrackDataDao;
import com.asimq.artists.bandninja.ui.HeaderView;
import com.asimq.artists.bandninja.utils.Util.Entities;
import com.asimq.artists.bandninja.viewmodelfactories.AlbumDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.ArtistDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.AlbumDetailViewModel;
import com.asimq.artists.bandninja.viewmodels.ArtistDetailViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
		implements AppBarLayout.OnOffsetChangedListener {

	private class Adapter extends RecyclerView.Adapter<ViewHolder> {

		private String[] paragraphs;

		public Adapter(String[] paragraphs) {
			this.paragraphs = paragraphs;
		}

		@Override
		public int getItemCount() {
			return paragraphs.length;
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.bodyParagraphView.setText(Html.fromHtml(paragraphs[position]
					.replaceAll("(\n)", "<br />")
					.replaceAll("(\r)", "<br />")));
			holder.bodyParagraphView.setMovementMethod(LinkMovementMethod.getInstance());

		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ViewHolder(getLayoutInflater().inflate(R.layout.list_item_detail, parent, false));
		}

	}

	public class SnackBarListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			if (entityType.equals(Entities.ARTIST.name())) {
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Information about " + globalArtistData.getName());
				StringBuilder builder = new StringBuilder(globalArtistData.getName()).append("\n")
						.append(globalArtistData.getBio()).append("\n").append(globalArtistData.getImage());
				shareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
			} else {
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, String.format("Information about %s - %s",
						globalAlbumData.getArtist(), globalAlbumData.getName()));
				StringBuilder builder = new StringBuilder(globalAlbumData.getName()).append("\n")
						.append(globalAlbumData.getWiki()).append("\n");
				shareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
			}
			shareIntent.setType("text/plain");
			startActivity(Intent.createChooser(shareIntent, getString(R.string.shareArticle)));
		}
	}

	private class TracksAdapter extends RecyclerView.Adapter<TracksViewHolder> {

		private List<TrackData> tracks;

		public TracksAdapter(List<TrackData> tracks) {
			this.tracks = tracks;
		}

		@Override
		public int getItemCount() {
			return tracks.size();
		}

		@Override
		public void onBindViewHolder(TracksViewHolder holder, int position) {
			final TrackData track = tracks.get(position);
			holder.trackListItemView.setText(String.format("%s. %s - %s", position + 1, track.getName(), track.getDuration()));
			holder.trackListItemView.setMovementMethod(LinkMovementMethod.getInstance());

		}

		@Override
		public TracksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new TracksViewHolder(getLayoutInflater().inflate(R.layout.track_list_item_detail, parent, false));
		}

	}

	public static class TracksViewHolder extends RecyclerView.ViewHolder {

		public TextView trackListItemView;

		public TracksViewHolder(View view) {
			super(view);
			trackListItemView = view.findViewById(R.id.trackListItemView);
		}
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public TextView bodyParagraphView;

		public ViewHolder(View view) {
			super(view);
			bodyParagraphView = view.findViewById(R.id.article_body);
		}
	}

	public static final String ALBUM = "ALBUM";
	public static final String ARTIST = "ARTIST";
	public static final String ENTITY_TYPE = "ENTITY_TYPE";
	public static final String MBID = "MBID";
	private final String TAG = this.getClass().getSimpleName();
	private AlbumDetailViewModel albumDetailViewModel;
	@Inject
	AlbumDetailViewModelFactory albumDetailViewModelFactory;
	private String albumName = "";
	@BindView(R.id.app_bar_layout)
	AppBarLayout appBarLayout;
	private ArtistDetailViewModel artistDetailViewModel;
	@Inject
	ArtistDetailViewModelFactory artistDetailViewModelFactory;
	private String artistName = "";
	private CardView cardView;
	@BindView(R.id.collapsing_toolbar)
	CollapsingToolbarLayout collapsingToolbarLayout;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.US);
	private String entityType = "ARTIST";
	@BindView(R.id.float_header_view)
	HeaderView floatHeaderView;
	private AlbumData globalAlbumData = new AlbumData();
	private ArtistData globalArtistData = new ArtistData();
	private boolean isHideToolbarView = false;
	@BindView(R.id.body_text_recycler_view)
	RecyclerView mRecyclerView;
	private String mbId = "";
	@BindView(R.id.progressBar)
	ProgressBar progressBar;
	private String publishedDate = "";
	private SnackBarListener snackBarListener = new SnackBarListener();
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.toolbar_header_view)
	HeaderView toolbarHeaderView;
	@Inject
	TrackDataDao trackDataDao;
	private Map<String, Track> tracksByAlbumMap = new HashMap<>();
	@BindView(R.id.tracksRecyclerView)
	RecyclerView tracksRecyclerView;

	private void applyPalette(Palette palette) {
		int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
		int primary = getResources().getColor(R.color.colorPrimary);
		collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
		collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
		updateBackground(findViewById(R.id.fab), palette);
		supportStartPostponedEnterTransition();
	}

	void buildTracks(List<TrackData> trackDatas) {
		Log.d(TAG, "trackDatas: " + trackDatas);
		if (null == trackDatas || trackDatas.isEmpty()) {
			return;
		}
		populateTracksRecyclerView(trackDatas);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent motionEvent) {
		try {
			return super.dispatchTouchEvent(motionEvent);
		} catch (NullPointerException e) {
			return false;
		}
	}

	@OnClick(R.id.fab)
	public void onClickFab(View view) {
		Snackbar mySnackbar = Snackbar.make(view, R.string.thanksForSharing, Snackbar.LENGTH_LONG);
		mySnackbar.setAction(R.string.shareArticle, snackBarListener);
		TextView text = mySnackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
		text.setTextColor(getResources().getColor(R.color.ltgray));
		mySnackbar.setActionTextColor(Color.WHITE);
		mySnackbar.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description_detail);
		ButterKnife.bind(this);
		final MyApplication application = (MyApplication) getApplicationContext();
		application.getApplicationComponent().inject(this);
		collapsingToolbarLayout.setTitle(" ");
		cardView = findViewById(R.id.cardView);
		appBarLayout = findViewById(R.id.app_bar_layout);
		appBarLayout.addOnOffsetChangedListener(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		if (savedInstanceState == null) {
			if (getIntent() != null && getIntent().getExtras() != null) {
				mbId = getIntent().getStringExtra(MBID);
				entityType = getIntent().getStringExtra(ENTITY_TYPE);
				artistName = getIntent().getStringExtra(ARTIST);
				albumName = getIntent().getStringExtra(ALBUM);
			}
		} else {
			mbId = savedInstanceState.getString(MBID);
			entityType = savedInstanceState.getString(ENTITY_TYPE);
			artistName = savedInstanceState.getString(ARTIST);
			albumName = getIntent().getStringExtra(ALBUM);
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(MBID, mbId);
		outState.putString(ENTITY_TYPE, entityType);
		outState.putString(ARTIST, artistName);
		outState.putString(ALBUM, albumName);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		artistDetailViewModel = ViewModelProviders.of(this, artistDetailViewModelFactory)
				.get(ArtistDetailViewModel.class);
		albumDetailViewModel = ViewModelProviders.of(this, albumDetailViewModelFactory)
				.get(AlbumDetailViewModel.class);
		if (Entities.ARTIST.name().equals(entityType)) {
			final LiveData<ArtistData> artistLiveData = mbId.isEmpty() ?
					artistDetailViewModel.getArtistLiveDataByName(artistName) : artistDetailViewModel.getArtistLiveDataById(mbId);
			artistLiveData.observe(this, this::populateInitialView);
		} else if (Entities.ALBUM.name().equals(entityType)) {
			albumDetailViewModel.getObservableAlbumData().observe(this, this::populateInitialView);
			albumDetailViewModel.obtainAlbumData(albumName, mbId);
		}
	}

	protected void populateBody(String body) {
		cardView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		populateUI(body.split("(\r\n\r\n)"));
		progressBar.setVisibility(View.GONE);
		cardView.setVisibility(View.VISIBLE);
	}

	private void populateInitialView(@NonNull ArtistData artistData) {
		globalArtistData = artistData;
		publishedDate = "";
		toolbarHeaderView.bindTo(artistData.getName(), publishedDate, publishedDate);
		floatHeaderView.bindTo(artistData.getName(), publishedDate, publishedDate);
		final ImageView photoView = findViewById(R.id.photo);
		final String photoUrl = artistData.getImage();
		Picasso.with(ArticleDetailActivity.this).load(photoUrl).into(photoView, new Callback() {
			@Override
			public void onError() {
				Log.e(TAG, "error loading photoView with url: " + photoUrl);
			}

			private void onGenerated(Palette palette) {
				applyPalette(palette);
			}

			@Override
			public void onSuccess() {
				Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
				Palette.from(bitmap).generate(this::onGenerated);
				populateBody(artistData.getBio());
			}
		});
		progressBar.setVisibility(View.GONE);
		cardView.setVisibility(View.VISIBLE);
	}

	private void populateInitialView(@NonNull AlbumData albumData) {
		globalAlbumData = albumData;
		toolbarHeaderView.bindTo(albumData.getName(), albumData.getArtist(), albumData.getReleaseDate());
		floatHeaderView.bindTo(albumData.getName(), albumData.getArtist(), albumData.getReleaseDate());
		final ImageView photoView = findViewById(R.id.photo);
		final String photoUrl = albumData.getImage();
		if (photoUrl.isEmpty()) return;
		Picasso.with(ArticleDetailActivity.this).load(photoUrl).into(photoView, new Callback() {
			@Override
			public void onError() {
				Log.e(TAG, "error loading photoView with url: " + photoUrl);
			}

			private void onGenerated(Palette palette) {
				applyPalette(palette);
			}

			@Override
			public void onSuccess() {
				Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
				Palette.from(bitmap).generate(this::onGenerated);
				final String body = albumData.getWiki().isEmpty() ? "" : albumData.getWiki();
				populateBody(body);
				buildTracks(albumData.getTrackDatas());
			}
		});
		progressBar.setVisibility(View.GONE);
		cardView.setVisibility(View.VISIBLE);
	}

	private void populateTracksRecyclerView(@NonNull List<TrackData> tracks) {
		TracksAdapter adapter = new TracksAdapter(tracks);
		adapter.setHasStableIds(true);
		tracksRecyclerView.setAdapter(adapter);
		StaggeredGridLayoutManager sglm =
				new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
		tracksRecyclerView.setLayoutManager(sglm);
	}

	private void populateUI(@NonNull String[] paragraphs) {
		Adapter adapter = new Adapter(paragraphs);
		adapter.setHasStableIds(true);
		mRecyclerView.setAdapter(adapter);
		StaggeredGridLayoutManager sglm =
				new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(sglm);
	}

	private void updateBackground(FloatingActionButton fab, Palette palette) {
		int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
		int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorPrimaryLight));
		fab.setRippleColor(lightVibrantColor);
		fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
	}

}
