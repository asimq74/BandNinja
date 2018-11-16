package com.asimq.artists.bandninja;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.asimq.artists.bandninja.data.Artist;
import com.asimq.artists.bandninja.data.ArtistInfoPojo;
import com.asimq.artists.bandninja.data.ArtistsPojo;
import com.asimq.artists.bandninja.data.Result;
import com.asimq.artists.bandninja.remote.retrofit.GetArtists;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    final String TAG = this.getClass().getSimpleName();

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
        final GetArtists service = RetrofitClientInstance.getRetrofitInstance().create(GetArtists.class);
        Call<ArtistsPojo> call = service.getArtists("artist.search", "The Cult",
                "06aec4c91800f972d32c0d702c003bd5", "json");
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

                Log.d(TAG, "results=" + artistPojo);
                for (Artist artist: artistPojo.getResult().getArtistmatches().getArtists()) {
                    final String artistName = artist.getName();
                    Call<ArtistInfoPojo> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
                            "06aec4c91800f972d32c0d702c003bd5", "json");
                    artistInfoCall.enqueue(new Callback<ArtistInfoPojo>() {

                        @Override
                        public void onFailure(Call<ArtistInfoPojo> call, Throwable t) {
                            Log.e(TAG, "error calling service", t);
                            Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(Call<ArtistInfoPojo> call, Response<ArtistInfoPojo> response) {
                            final ArtistInfoPojo artistInfoPojo = response.body();
                            if (artistInfoPojo == null) {
                                return;
                            }

                            Log.d(TAG, "artistInfoPojo=" + artistInfoPojo);
                        }
                    });
                }
            }
        });

    }
}
