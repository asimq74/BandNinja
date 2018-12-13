package com.asimq.artists.bandninja;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.asimq.artists.bandninja.json.Album;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TopAlbumsWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetArtists;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "query=" + query);
        }
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
		Call<Tag[]> artistTagInfoCall = service.getTagByArtistId("artist.getTagWrapper", mbid, API_KEY, "RJ", DEFAULT_FORMAT);
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
		setContentView(R.layout.result_activity_main);
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
        handleIntent(getIntent());
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
}
