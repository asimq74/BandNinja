package com.asimq.artists.bandninja;

import android.app.SearchManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asimq.artists.bandninja.data.Artist;
import com.asimq.artists.bandninja.data.ArtistsPojo;
import com.asimq.artists.bandninja.data.Image;
import com.asimq.artists.bandninja.remote.retrofit.GetArtists;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String API_KEY = BuildConfig.LastFMApiKey;
    public static final String DEFAULT_FORMAT = "json";
    public static final String ITEM_ID = "ITEM_ID";
    final String TAG = this.getClass().getSimpleName();
    private RecyclerView mRecyclerView;

    private void populateUI(@Nullable Artist[] artists) {
        Adapter adapter = new Adapter(artists);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: query->" + query);
                final GetArtists service = RetrofitClientInstance.getRetrofitInstance().create(GetArtists.class);
                Call<ArtistsPojo> call = service.getArtists("artist.search", query,
                        API_KEY, DEFAULT_FORMAT);
                call.enqueue(new Callback<ArtistsPojo>() {

                    @Override
                    public void onFailure(Call<ArtistsPojo> call, Throwable t) {
                        Log.e(TAG, "error calling service", t);
                        Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call<ArtistsPojo> call, Response<ArtistsPojo> response) {
                        final ArtistsPojo artistPojo = response.body();
                        if (artistPojo == null) {
                            return;
                        }

                        Log.i(TAG, "result: " + artistPojo.getResult());
                        populateUI(artistPojo.getResult().getArtistmatches().getArtists());

                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageButton expandButtonView;
        public TextView subtitleView;
        public TextView supportingTextView;
        public ImageView thumbnailView;
        public TextView titleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.article_title);
            subtitleView = view.findViewById(R.id.article_subtitle);
            expandButtonView = view.findViewById(R.id.expand_button);
            supportingTextView = view.findViewById(R.id.supporting_text);
            expandButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (supportingTextView.getVisibility() == View.VISIBLE) {
                        expandButtonView.setImageResource(R.drawable.ic_expand_less_black_36dp);
                        supportingTextView.setVisibility(View.GONE);
                    } else {
                        expandButtonView.setImageResource(R.drawable.ic_expand_more_black_36dp);
                        supportingTextView.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final Artist[] artists;


        public Adapter(Artist[] artists) {
            this.artists = artists;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.list_item_search_results, viewGroup, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int adapterPosition = vh.getAdapterPosition();
                    final long itemId = getItemId(adapterPosition);
                    final Intent intent = new Intent(MainNavigationActivity.this, ResultActivity.class);
                    intent.putExtra(ITEM_ID, itemId);
                    startActivity(intent);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            Artist artist = artists[i];
            holder.titleView.setText(artist.getName());
            holder.subtitleView.setText(artist.getName());
            attemptToLoadThumbnail(holder, artist);
            holder.expandButtonView.setImageResource(R.drawable.ic_expand_less_black_36dp);
            holder.supportingTextView.setVisibility(View.GONE);
        }

        private void attemptToLoadThumbnail(@NonNull ViewHolder holder, @NonNull Artist artist) {
            String imageUrl = getImageUrl(artist);
            if (imageUrl.isEmpty()) {
                return;
            }
            Picasso.with(MainNavigationActivity.this).load(imageUrl).into(
                    holder.thumbnailView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Log.i(TAG, "image is empty");
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
            return artists.length;
        }
    }
}
