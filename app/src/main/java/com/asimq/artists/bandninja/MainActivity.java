package com.asimq.artists.bandninja;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import android.Manifest;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.asimq.artists.bandninja.MusicItemsListFragment.OnFragmentInteractionListener;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;
import com.asimq.artists.bandninja.ui.CustomEditText;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.TagDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.TagDetailViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener,
		ConnectionCallbacks,
		OnConnectionFailedListener {

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
				onDisplayArtistsFromStorage(artists);
			}
		}
	}

	final String TAG = this.getClass().getSimpleName();
	private ApplicationComponent applicationComponent;
	@Inject
	ArtistDataDao artistDataDao;
	@Inject
	ArtistTagDao artistTagDao;
	@BindView(R.id.fab)
	FloatingActionButton fab;
	private GoogleApiClient mClient;
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

	private void considerDisplayingArtistsFromStorage() {
		new ConsiderDisplayingArtistsFromStorageTask().executeOnExecutor(Executors.newSingleThreadExecutor(), artistDataDao);
	}

	private void hideKeyboard() {
		InputMethodManager inputManager =
				(InputMethodManager) this.
						getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(
				this.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
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
		ButterKnife.bind(this);
		final MyApplication application = (MyApplication) getApplicationContext();
		applicationComponent = application.getApplicationComponent();
		applicationComponent.inject(this);
		tagDetailViewModel = ViewModelProviders.of(this, tagDetailViewModelFactory)
				.get(TagDetailViewModel.class);

		// Build up the LocationServices API client
		// Uses the addApi method to request the LocationServices API
		// Also uses enableAutoManage to automatically when to connect/suspend the client
		mClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.enableAutoManage(this, this)
				.build();

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		setUpSearchByArtistView();
		considerDisplayingArtistsFromStorage();
	}

	Location mLocation;

	@Override
	protected void onStart() {
		super.onStart();
		if (mClient != null) {
			mClient.connect();
		}
	}

	private void considerCheckingLocationPermissions() {
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {
				Log.i(TAG, "fine location was granted");
				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
				mLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);


				if(mLocation!=null)
				{
					Log.i(TAG, "mLocation: " + mLocation);
				}


			} else {
				// No explanation needed, we can request the permission.
				Log.i(TAG, "fine location was not granted");
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_REQUEST_FINE_LOCATION : {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.i(TAG, "API Client Connection Successful!");
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.i(TAG, "API Client Connection Suspended!");
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.i(TAG, "API Client Connection Failed!");
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
	public void onDisplayArtistsFromStorage(@NonNull List<Artist> artists) {
		MusicItemsListFragment musicItemsListFragment = (MusicItemsListFragment)
				getSupportFragmentManager().findFragmentById(R.id.musicItemsListFragment);
		if (musicItemsListFragment != null) {
			musicItemsListFragment.displayArtistsFromStorage(artists);
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
//		considerCheckingLocationPermissions();
		tagDetailViewModel.getTopTags().observe(this, new Observer<List<Tag>>() {
			@Override
			public void onChanged(@Nullable List<Tag> tags) {
				Log.d(TAG, "tags: " + tags);
			}
		});
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