package com.asimq.artists.bandninja;

import java.util.List;

import javax.inject.Inject;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asimq.artists.bandninja.asynctasks.SaveArtistTask;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Image;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.SearchResultsViewModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainNavigationActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, NavigationListener {

	private class Adapter extends RecyclerView.Adapter<ViewHolder> {

		private final List<Artist> artists;

		private Adapter(@NonNull List<Artist> artists) {
			this.artists = artists;
		}

		private void attemptToLoadThumbnail(@NonNull ViewHolder holder, @NonNull Artist artist) {
			String imageUrl = getImageUrl(artist);
			if (imageUrl.isEmpty()) {
				return;
			}
			Picasso.with(MainNavigationActivity.this).load(imageUrl).into(
					holder.thumbnailView, new com.squareup.picasso.Callback() {
						@Override
						public void onError() {
							Log.i(TAG, "image is empty");
						}

						@Override
						public void onSuccess() {

						}
					});
		}

		private String getImageUrl(@NonNull Artist artist) {
			for (Image image : artist.getImages()) {
				if ("large".equals(image.getSize())) {
					return image.getText();
				}
			}
			return "";
		}

		@Override
		public int getItemCount() {
			return artists.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
			final Artist artist = artists.get(i);
			holder.titleView.setText(artist.getName());
			attemptToLoadThumbnail(holder, artist);
			searchResultsViewModel.getArtistInfo(artist.getName()).observe(MainNavigationActivity.this,
					artistDetailedInfo -> populateTags(holder, artistDetailedInfo, i));
		}

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
			View view = getLayoutInflater().inflate(R.layout.list_item_search_results, viewGroup, false);
			ViewHolder vh = new ViewHolder(view);
			return vh;
		}

		private void populateTags(ViewHolder holder, Artist artistDetailedInfo, int position) {
			StringBuilder sb = new StringBuilder();
			int count = 0;
			final List<Tag> allTags = artistDetailedInfo.getTagWrapper().getTags();
			for (Tag tag : allTags) {
				sb.append(tag.getName()).append(count++ < (allTags.size() - 1) ? ", " : "");
			}
			holder.subtitleView.setText(sb);
			holder.itemView.setOnClickListener((View v) -> {
				ArtistData artistData = new ArtistData(artistDetailedInfo);
				new SaveArtistTask(artistDataDao, MainNavigationActivity.this).execute(artistData);
			});
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.sub_text)
		TextView subtitleView;
		@BindView(R.id.thumbnail)
		ImageView thumbnailView;
		@BindView(R.id.primary_text)
		TextView titleView;

		public ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	public static final String MB_ID = "MB_ID";
	final String TAG = this.getClass().getSimpleName();
	@Inject
	ArtistDataDao artistDataDao;
	@BindView(R.id.fab)
	FloatingActionButton fab;
	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	private SearchResultsViewModel searchResultsViewModel;
	@Inject
	SearchResultsViewModelFactory searchResultsViewModelFactory;
	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@Override
	public void navigateTo(@NonNull String mbid) {
		final Intent intent = new Intent(MainNavigationActivity.this, MainActivity.class);
		intent.putExtra(MB_ID, mbid);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_launcher);
		ButterKnife.bind(this);
		final MyApplication application = (MyApplication) getApplicationContext();
		application.getApplicationComponent().inject(this);
		searchResultsViewModel = ViewModelProviders.of(this, searchResultsViewModelFactory)
				.get(SearchResultsViewModel.class);
		setSupportActionBar(toolbar);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
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
				searchResultsViewModel.getSearchResultsByArtist(query).observe(MainNavigationActivity.this, artists -> populateUI(artists));
				return false;
			}
		});
		return true;
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_camera) {
			// Handle the camera action
		} else if (id == R.id.nav_gallery) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}
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

	private void populateUI(@Nullable List<Artist> artists) {
		if (null == artists || artists.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();
			return;
		}
		Adapter adapter = new Adapter(artists);
		adapter.setHasStableIds(true);
		mRecyclerView.setAdapter(adapter);
		int columnCount = getResources().getInteger(R.integer.list_column_count);
		StaggeredGridLayoutManager sglm =
				new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(sglm);
	}
}
