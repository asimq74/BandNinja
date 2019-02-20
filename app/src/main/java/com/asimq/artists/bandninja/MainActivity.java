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
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.asimq.artists.bandninja.MusicItemsListFragment.OnMainActivityInteractionListener;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.jobs.BandSyncJobService;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.receivers.RefreshNavigationDrawerReceiver;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.service.ServiceUtil;
import com.asimq.artists.bandninja.ui.DrawerHeaderView;
import com.asimq.artists.bandninja.utils.Util;
import com.asimq.artists.bandninja.viewmodelfactories.NavigationDrawerMenuViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.NavigationDrawerMenuViewModel;
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

	public static final String EXTRA_CURRENT_ALBUM = "EXTRA_CURRENT_ALBUM";
	public static final String EXTRA_CURRENT_ARTIST = "EXTRA_CURRENT_ARTIST";
	public static final String EXTRA_CURRENT_METHOD = "EXTRA_CURRENT_METHOD";
	public static final String EXTRA_CURRENT_TAG = "EXTRA_CURRENT_TAG";
	private static final String JOB_TAG = "MyJobService";
	public static final String NETWORK_CHANGE_INTENT = "network-change-intent";
	public static final String ON_DISPLAYING_ARTISTS_BY_TAG = "onDisplayingArtistsByTag";
	public static final String ON_DISPLAYING_TOP_ARTISTS = "onDisplayingTopArtists";
	public static final String ON_QUERY_TEXT_SUBMIT = "onQueryTextSubmit";
	public static final String ON_SEARCH_FOR_AN_ARTIST = "onSearchForAnArtist";
	public static final String REFRESH_DRAWER_INTENT = "refresh-drawer-intent";
	final String TAG = this.getClass().getSimpleName();
	AdView adView;
	private ApplicationComponent applicationComponent;
	@Inject
	ArtistDataDao artistDataDao;
	private String currentAlbum = "";
	private String currentArtist = "";
	private String currentMethod = "";
	private String currentTag = "";
	DrawerHeaderView drawerHeaderView;
	private RefreshNavigationDrawerReceiver drawerReceiver;
	private Map<String, String> genreMap = new HashMap<>();
	@BindView(R.id.locationView)
	TextView locationView;
	private FirebaseJobDispatcher mDispatcher;
	@BindView(R.id.drawer_layout)
	DrawerLayout mDrawerLayout;
	private Location mLocation = null;
	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;
	private NavigationDrawerMenuViewModel navigationDrawerMenuViewModel;
	@Inject
	NavigationDrawerMenuViewModelFactory navigationDrawerMenuViewModelFactory;
	@BindView(R.id.nav_view)
	NavigationView navigationView;
	@BindView(R.id.toolbar)
	Toolbar toolbar;

	private void bindDrawerHeaderView() {
		String localityAndPostalCode = "";
		String name = "";
		SharedPreferences sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String locationFromPreferences = sharedPreferences.getString("location", null);
		name = defaultSharedPreferences.getString(getString(R.string.display_name_key), "");
		if (null != locationFromPreferences) {
			mLocation = new Gson().fromJson(locationFromPreferences, Location.class);
			localityAndPostalCode = Util.getLocalityAndPostalCode(this, mLocation.getLatitude(), mLocation.getLongitude());
		}
		navigationView.removeHeaderView(drawerHeaderView);
		drawerHeaderView = null;
		drawerHeaderView = (DrawerHeaderView) LayoutInflater.from(this).inflate(R.layout.drawer_header, null);
		if (!Util.isConnected(this)) {
			drawerHeaderView.hideSearchLayout();
			drawerHeaderView.setDisconnectedFromInternetViewShown(true);
		} else {
			drawerHeaderView.setDisconnectedFromInternetViewShown(false);
		}
		drawerHeaderView.bindTo(name, localityAndPostalCode);
		navigationView.addHeaderView(drawerHeaderView);
	}

	@Override
	public void closeNavigationDrawer() {
		mDrawerLayout.closeDrawers();
	}

	@Override
	public void considerDisplayingArtistsFromStorage() {
		new ConsiderDisplayingArtistsFromStorageTask().executeOnExecutor(Executors.newSingleThreadExecutor(), artistDataDao);
	}

	private void determineDataToDisplay() {
		if (!Util.isConnected(this)) {
			onDisplayingYourTopArtists();
			return;
		}
		if (ON_DISPLAYING_ARTISTS_BY_TAG.equals(currentMethod)) {
			onDisplayingArtistsByTag(currentTag);
		} else if (ON_DISPLAYING_TOP_ARTISTS.equals(currentMethod)) {
			onDisplayingTopArtists();
		} else if (ON_QUERY_TEXT_SUBMIT.equals(currentMethod)) {
			onSearchedForArtistName(currentArtist);
		} else {
			onDisplayingTopArtists();
		}
	}

	@Override
	public void hideKeyboard() {
		InputMethodManager inputManager =
				(InputMethodManager) this.
						getSystemService(Context.INPUT_METHOD_SERVICE);
		if (null == this.getCurrentFocus()) {
			return;
		}
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
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (getIntent() != null && getIntent().getExtras() != null) {
			currentTag = getIntent().getStringExtra(EXTRA_CURRENT_TAG);
			currentArtist = getIntent().getStringExtra(EXTRA_CURRENT_ARTIST);
			currentAlbum = getIntent().getStringExtra(EXTRA_CURRENT_ALBUM);
			currentMethod = getIntent().getStringExtra(EXTRA_CURRENT_METHOD);
			Set<String> keySet = getIntent().getExtras().keySet();
			for (String key : keySet) {
				Log.d(TAG, String.format("key: %s value: %s", key, getIntent().getStringExtra(key)));
			}
		} else {
			Log.d(TAG, "intent was null");
		}
		if (bundle != null) {
			currentTag = bundle.getString(EXTRA_CURRENT_TAG);
			currentArtist = bundle.getString(EXTRA_CURRENT_ARTIST);
			currentAlbum = bundle.getString(EXTRA_CURRENT_ALBUM);
			currentMethod = bundle.getString(EXTRA_CURRENT_METHOD);
			Log.d(TAG, String.format("current state restored onCreate %s, %s, %s, %s", currentMethod, currentArtist, currentAlbum, currentTag));
		}
		setContentView(R.layout.activity_main);
		if (Util.isGooglePlayServicesAvailable(this)) {
			mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
			scheduleJob();
		} else {
			ServiceUtil.scheduleJob(this);
		}
		ButterKnife.bind(this);
		final MyApplication application = (MyApplication) getApplicationContext();
		drawerReceiver = new RefreshNavigationDrawerReceiver();
		applicationComponent = application.getApplicationComponent();
		applicationComponent.inject(this);
		navigationDrawerMenuViewModel = ViewModelProviders.of(this, navigationDrawerMenuViewModelFactory)
				.get(NavigationDrawerMenuViewModel.class);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
		setupDrawer();
		loadAd();
		drawerHeaderView.setUpSearchByArtistView(this);
		determineDataToDisplay();

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
				currentArtist = query;
				currentMethod = ON_QUERY_TEXT_SUBMIT;
				currentAlbum = "";
				currentTag = "";
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
			currentArtist = "";
			currentMethod = ON_DISPLAYING_ARTISTS_BY_TAG;
			currentAlbum = "";
			currentTag = tag;
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
			currentArtist = "";
			currentMethod = ON_DISPLAYING_TOP_ARTISTS;
			currentAlbum = "";
			currentTag = "";
			musicItemsListFragment.populateTopArtists();
		}
	}

	@Override
	public void onDisplayingYourTopArtists() {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			musicItemsListFragment.populateYourTopArtists();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		} else if (item.getItemId() == android.R.id.home) {
			mDrawerLayout.openDrawer(GravityCompat.START);
			return true;
		}
		return false;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		currentTag = savedInstanceState.getString(EXTRA_CURRENT_TAG);
		currentArtist = savedInstanceState.getString(EXTRA_CURRENT_ARTIST);
		currentAlbum = savedInstanceState.getString(EXTRA_CURRENT_ALBUM);
		currentMethod = savedInstanceState.getString(EXTRA_CURRENT_METHOD);
		Log.d(TAG, String.format("current state restored %s, %s, %s, %s", currentMethod, currentArtist, currentAlbum, currentTag));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(EXTRA_CURRENT_TAG, currentTag);
		outState.putString(EXTRA_CURRENT_ARTIST, currentArtist);
		outState.putString(EXTRA_CURRENT_ALBUM, currentAlbum);
		outState.putString(EXTRA_CURRENT_METHOD, currentMethod);
		Log.d(TAG, String.format("current state saved %s, %s, %s, %s", currentMethod, currentArtist, currentAlbum, currentTag));
	}

	@Override
	public void onSearchedForArtistName(@NonNull String artistName) {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			currentMethod = ON_QUERY_TEXT_SUBMIT;
			musicItemsListFragment.displaySearchResultsByArtist(artistName);
		}
		hideKeyboard();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// add data source
		navigationDrawerMenuViewModel.addDataSource(drawerReceiver.getData());
		navigationDrawerMenuViewModel.shouldRefreshNavigationDrawerObservable().observe(this, this::refreshDrawer);
		LocalBroadcastManager.getInstance(this).registerReceiver(drawerReceiver, new IntentFilter(REFRESH_DRAWER_INTENT));
		SharedPreferences sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
		String json = sharedPreferences.getString("location", null);
		if (null != json) {
			mLocation = new Gson().fromJson(json, Location.class);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// don't forget to remove receiver data source
		navigationDrawerMenuViewModel.removeDataSource(drawerReceiver.getData());
		LocalBroadcastManager.getInstance(this).unregisterReceiver(drawerReceiver);
	}

	private void populateDrawerMenu() {
		navigationView.getMenu().clear();
		final Menu menu = navigationView.getMenu();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (!Util.isConnected(this)) {
			drawerHeaderView.setDisconnectedFromInternetViewShown(true);
			menu.add(getString(R.string.yourTopAlbums));
			menu.add(getString(R.string.yourTopArtists));
			return;
		}
		drawerHeaderView.setDisconnectedFromInternetViewShown(false);
		Set<String> favoriteGenres = prefs.getStringSet(getString(R.string.favorite_genre_key), new HashSet<>());
		if (favoriteGenres.isEmpty()) {
			menu.add(getString(R.string.pref_title_favorite_genres));
		}
		menu.add(getString(R.string.topArtists));
		for (String genre : favoriteGenres) {
			if (!genre.isEmpty()) {
				String capitalGenre = genre.substring(0, 1).toUpperCase() + genre.substring(1);
				final String byGenreKey = String.format("Top Artists in %s", capitalGenre);
				menu.add(byGenreKey);
				genreMap.put(byGenreKey, genre);
			}
		}
	}

	private void refreshDrawer(boolean refreshDrawer) {
		populateDrawerMenu();
		bindDrawerHeaderView();
	}

	private void scheduleJob() {
		Bundle bundle = new Bundle();
		bundle.putString("key", "value");
		Job myJob = mDispatcher.newJobBuilder()
				.setService(BandSyncJobService.class)
				.setTag(JOB_TAG)
				.setRecurring(true)
				.setTrigger(Trigger.executionWindow(8640, 8700))
				.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
				.setReplaceCurrent(false)
				.setConstraints(Constraint.ON_ANY_NETWORK)
				.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
				.setExtras(bundle)
				.build();
		mDispatcher.mustSchedule(myJob);
	}

	private void setupDrawer() {
		bindDrawerHeaderView();
		ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				SettingsActivity.sendMessage(MainActivity.this);
				drawerHeaderView.setDisconnectedFromInternetViewShown(!drawerHeaderView.isDisconnectedFromInternetViewShown());
				super.onDrawerOpened(drawerView);
			}
		};
		populateDrawerMenu();
		navigationView.setNavigationItemSelectedListener(
				menuItem -> {
					// set item as selected to persist highlight
					menuItem.setChecked(true);
					// close drawer when item is tapped
					closeNavigationDrawer();

					String popupMenuItemText = menuItem.getTitle().toString();
					if (genreMap.containsKey(popupMenuItemText)) {
						onDisplayingArtistsByTag(genreMap.get(popupMenuItemText));
						return true;
					}
					if (getString(R.string.topArtists).equals(popupMenuItemText)) {
						onDisplayingTopArtists();
						return true;
					}
					if (getString(R.string.pref_title_favorite_genres).equals(popupMenuItemText)) {
						startActivity(new Intent(this, SettingsActivity.class));
						return true;
					}
					if (getString(R.string.yourTopAlbums).equals(popupMenuItemText)) {
						onDisplayingTopAlbums();
						return true;
					}
					if (getString(R.string.yourTopArtists).equals(popupMenuItemText)) {
						onDisplayingYourTopArtists();
						return true;
					}
					return true;
				});
		mDrawerLayout.addDrawerListener(drawerToggle);
		drawerToggle.syncState();
	}

}