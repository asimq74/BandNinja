package com.asimq.artists.bandninja;

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

import com.asimq.artists.bandninja.data.Album;
import com.asimq.artists.bandninja.data.Artist;
import com.asimq.artists.bandninja.data.ArtistInfoPojo;
import com.asimq.artists.bandninja.data.ArtistsPojo;
import com.asimq.artists.bandninja.data.TopAlbumsPojo;
import com.asimq.artists.bandninja.remote.retrofit.GetArtists;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

	public static final String API_KEY = "06aec4c91800f972d32c0d702c003bd5";
	public static final String DEFAULT_FORMAT = "json";
	;
	final String TAG = this.getClass().getSimpleName();

	private void getAlbumInfo(final String artistName, final GetArtists service) {
		Call<ArtistInfoPojo> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
				API_KEY, DEFAULT_FORMAT);
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

				getTopAlbumInfo(artistInfoPojo, service, artistName);
			}
		});
	}

	private void getTopAlbumInfo(final ArtistInfoPojo artistInfoPojo, GetArtists service, final String artistName) {
		Call<TopAlbumsPojo> topAlbumsPojoCall = service.getTopAlbums("artist.gettopalbums", artistName,
				API_KEY, DEFAULT_FORMAT, 1);
		topAlbumsPojoCall.enqueue(new Callback<TopAlbumsPojo>() {
			@Override
			public void onFailure(Call<TopAlbumsPojo> call, Throwable t) {
				Log.e(TAG, "error calling service", t);
				Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onResponse(Call<TopAlbumsPojo> call, Response<TopAlbumsPojo> response) {
				final TopAlbumsPojo topAlbumsPojo = response.body();
				if (topAlbumsPojo == null) {
					return;
				}

				Log.d(TAG, String.format("\n\ntop albums for %s (%s items)", artistName, topAlbumsPojo.getTopalbums().getAlbums().length));
				for (Album album : topAlbumsPojo.getTopalbums().getAlbums()) {
					Log.d(TAG, String.format("\t%s - %s\n", album.getName(), album.getArtist().getName()));
				}
			}
		});
	}

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

				getAlbumInfo("The Cult", service);
				for (Artist artist : artistPojo.getResult().getArtistmatches().getArtists()) {
					final String artistName = artist.getName();
					getAlbumInfo(artistName, service);
				}
			}
		});

	}
}
