package com.asimq.artists.bandninja;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.asimq.artists.bandninja.MusicItemsListFragment.OnFragmentInteractionListener;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;
import com.asimq.artists.bandninja.room.dao.ArtistTagDao;
import com.asimq.artists.bandninja.ui.CustomEditText;
import com.asimq.artists.bandninja.utils.Util;
import com.asimq.artists.bandninja.viewmodelfactories.SearchResultsViewModelFactory;
import com.asimq.artists.bandninja.viewmodelfactories.TagDetailViewModelFactory;
import com.asimq.artists.bandninja.viewmodels.TagDetailViewModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.locationView)
    TextView locationView;
    @Inject
    ArtistDataDao artistDataDao;
    @Inject
    ArtistTagDao artistTagDao;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.searchByArtistEditView)
    CustomEditText searchByArtistEditTextView;
    @Inject
    SearchResultsViewModelFactory searchResultsViewModelFactory;
    @Inject
    TagDetailViewModelFactory tagDetailViewModelFactory;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ApplicationComponent applicationComponent;
    private GoogleApiClient mClient;
    private TagDetailViewModel tagDetailViewModel;
    private Location mLocation = null;

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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setUpSearchByArtistView();
        considerDisplayingArtistsFromStorage();
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

}