package com.asimq.artists.bandninja;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.asimq.artists.bandninja.MusicItemsListFragment.OnMainActivityInteractionListener;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.jobs.BandSyncJobService;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.ui.CustomEditText;
import com.asimq.artists.bandninja.utils.Util;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.TagDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.TagDetailViewModel;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMainActivityInteractionListener {

	private class ConsiderDisplayingArtistsFromStorageTask extends AsyncTask<ArtistDataDao, Void, List<Artist>> {

		@Override
		protected List<Artist> doInBackground(ArtistDataDao... daos) {
			ArtistDataDao dao = daos[0];
			List<Artist> artists = new ArrayList<>();
			List<ArtistData> artistDatas = dao.fetchAllArtistDatas();
			for (ArtistData artistData : artistDatas) {
				artists.add(new Artist(artistData));
			}
			return artists;
		}

		@Override
		protected void onPostExecute(List<Artist> artists) {
			super.onPostExecute(artists);
			if (!artists.isEmpty()) {
				onDisplayArtistList(artists);
			}
		}
	}

	private static final String JOB_TAG = "MyJobService";
	final String TAG = this.getClass().getSimpleName();
	AdView adView;
	private ApplicationComponent applicationComponent;
	@Inject
	ArtistDataDao artistDataDao;
	@BindView(R.id.fab)
	FloatingActionButton fab;
	private Map<String, String> genreMap = new HashMap<>();
	ListPopupWindow listPopupWindow;
	@BindView(R.id.locationView)
	TextView locationView;
	private GoogleApiClient mClient;
	private FirebaseJobDispatcher mDispatcher;
	private Location mLocation = null;
	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	@BindView(R.id.searchByArtistEditView)
	CustomEditText searchByArtistEditTextView;
	@Inject
	SearchResultsViewModelFactory searchResultsViewModelFactory;
	private TagDetailViewModel tagDetailViewModel;
	@Inject
	TagDetailViewModelFactory tagDetailViewModelFactory;
	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private void cancelJob(String jobTag) {
		if ("".equals(jobTag)) {
			mDispatcher.cancelAll();
		} else {
			mDispatcher.cancel(jobTag);
		}
		Toast.makeText(this, "Job Cancelled!", Toast.LENGTH_LONG).show();
	}

	private void considerDisplayingArtistsFromStorage() {
		new ConsiderDisplayingArtistsFromStorageTask().executeOnExecutor(Executors.newSingleThreadExecutor(), artistDataDao);
	}

	private String[] getPopupMenuItems() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Set<String> favoriteGenres = prefs.getStringSet(getString(R.string.favorite_genre_key), new HashSet<>());
		List<String> popupMenuItems = new ArrayList<>();
		popupMenuItems.add(getString(R.string.topArtists));
		if (favoriteGenres.isEmpty()) {
			popupMenuItems.add(getString(R.string.topArtistsByGenre));
		}
		popupMenuItems.add(getString(R.string.topAlbums));
		for (String genre : favoriteGenres) {
			if (!genre.isEmpty()) {
				String capitalGenre = genre.substring(0, 1).toUpperCase() + genre.substring(1);
				final String byGenreKey = String.format("Top Artists in %s", capitalGenre);
				popupMenuItems.add(byGenreKey);
				genreMap.put(byGenreKey, genre);
			}
		}
		String[] items = new String[popupMenuItems.size()];
		return popupMenuItems.toArray(items);
	}

	private void hideKeyboard() {
		InputMethodManager inputManager =
				(InputMethodManager) this.
						getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(
				this.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void loadAd() {
		AdRequest adRequest;
		if (null == mLocation) {
			adRequest = new AdRequest.Builder()
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice("D64BF957D55963D6B121A98C94BEBA22")
					.build();
		} else {
			adRequest = new AdRequest.Builder()
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice("D64BF957D55963D6B121A98C94BEBA22")
					.setLocation(mLocation)
					.build();
		}

		adView = findViewById(R.id.ad_view);
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				// Code to be executed when when the user is about to return
				// to the app after tapping on an ad.
				Log.i(TAG, "Ad closed");
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				// Code to be executed when an ad request fails.
				Log.i(TAG, "Ad failed to load errorCode=" + errorCode);
			}

			@Override
			public void onAdLeftApplication() {
				// Code to be executed when the user has left the app.
				Log.i(TAG, "Ad left the application");
			}

			@Override
			public void onAdLoaded() {
				// Code to be executed when an ad finishes loading.
				Log.i(TAG, "Ad finished loading");
			}

			@Override
			public void onAdOpened() {
				// Code to be executed when an ad opens an overlay that
				// covers the screen.
				Log.i(TAG, "Ad opened");
			}
		});
//        // Start loading the ad in the background.
		adView.loadAd(adRequest);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		if (fragment instanceof MusicItemsListFragment) {
			MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment) fragment;
			musicItemsListFragment.setOnFragmentInteractionListener(this);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
		scheduleJob();
		ButterKnife.bind(this);
		final MyApplication application = (MyApplication) getApplicationContext();
		applicationComponent = application.getApplicationComponent();
		applicationComponent.inject(this);
		tagDetailViewModel = ViewModelProviders.of(this, tagDetailViewModelFactory)
				.get(TagDetailViewModel.class);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "your icon was clicked", Toast.LENGTH_SHORT).show();
				listPopupWindow = new ListPopupWindow(
						MainActivity.this);
				final String[] popupMenuItems = getPopupMenuItems();
				listPopupWindow.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.popup_menu_list_item,
						popupMenuItems));
				listPopupWindow.setAnchorView(toolbar);
				listPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
				listPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

				listPopupWindow.setModal(true);
				listPopupWindow.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						listPopupWindow.dismiss();
						final String popupMenuItemText = popupMenuItems[position];
						Toast.makeText(getApplicationContext(), popupMenuItemText + " was clicked", Toast.LENGTH_SHORT).show();
						if (genreMap.containsKey(popupMenuItemText)) {
							onDisplayingArtistsByTag(genreMap.get(popupMenuItemText));
							return;
						}
						if (getString(R.string.topArtists).equals(popupMenuItemText)) {
							onDisplayingTopArtists();
							return;
						}
						if (getString(R.string.topArtistsByGenre).equals(popupMenuItemText)) {
							considerDisplayingArtistsFromStorage();
							return;
						}
						if (getString(R.string.topAlbums).equals(popupMenuItemText)) {
							onDisplayingTopAlbums();
							return;
						}
					}
				});
				listPopupWindow.show();
			}

		});

		loadAd();
		setUpSearchByArtistView();
//		considerDisplayingArtistsFromStorage();
		onDisplayingArtistsByTag("alternative");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_navigation, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconified(false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String s) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "onQueryTextSubmit: query->" + query);
				onSearchedForArtistName(query);
				return false;
			}
		});
		return true;
	}

	@Override
	public void onDisplayArtistList(@NonNull List<Artist> artists) {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			musicItemsListFragment.displayArtistsFromStorage(artists);
		}
	}

	@Override
	public void onDisplayingArtistsByTag(@NonNull String tag) {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			musicItemsListFragment.populateArtistsByTag(tag);
		}
	}

	@Override
	public void onDisplayingTopAlbums() {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			musicItemsListFragment.populateTopAlbums();
		}
	}

	@Override
	public void onDisplayingTopArtists() {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			musicItemsListFragment.populateTopArtists();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
//        tagDetailViewModel.getTopTags().observe(this, tags -> Log.d(TAG, "tags: " + tags));
	}

	@Override
	public void onSearchedForArtistName(@NonNull String artistName) {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			musicItemsListFragment.displaySearchResultsByArtist(artistName);
		}
		hideKeyboard();
	}

	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
		String json = sharedPreferences.getString("location", null);
		if (null != json) {
			mLocation = new Gson().fromJson(json, Location.class);
			String postalCode = Util.getPostalCode(this, mLocation.getLatitude(), mLocation.getLongitude());
			locationView.setText(postalCode);
		}

	}

	private void scheduleJob() {
		Bundle bundle = new Bundle();
		bundle.putString("key", "value");
		Job myJob = mDispatcher.newJobBuilder()
				.setService(BandSyncJobService.class)
				.setTag(JOB_TAG)
				.setRecurring(true)
				.setTrigger(Trigger.executionWindow(5, 30))
				.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
				.setReplaceCurrent(false)
				.setConstraints(Constraint.ON_ANY_NETWORK)
				.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
				.setExtras(bundle)
				.build();
		mDispatcher.mustSchedule(myJob);
		Toast.makeText(this, "Job Scheduled!", Toast.LENGTH_LONG).show();
	}

	private void setUpSearchByArtistView() {
		searchByArtistEditTextView.setDrawableClickListener(target -> {
			switch (target) {
				case RIGHT:
					onSearchedForArtistName(searchByArtistEditTextView.getText().toString());
					break;
				case LEFT:
					searchByArtistEditTextView.getText().clear();
					hideKeyboard();
					considerDisplayingArtistsFromStorage();
					break;
				default:
					break;
			}
		});
		searchByArtistEditTextView.setOnKeyListener((v, keyCode, event) -> {
			// If the event is a key-down event on the "enter" button
			if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
					(keyCode == KeyEvent.KEYCODE_ENTER)) {
				onSearchedForArtistName(searchByArtistEditTextView.getText().toString());
				return true;
			}
			return false;
		});
	}

}