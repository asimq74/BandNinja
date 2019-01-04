package com.asimq.artists.bandninja.remote.retrofit;

import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.ResultsWrapper;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TagWrapper;
import com.asimq.artists.bandninja.json.TopAlbumsWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetArtists {

	//    /2.0/?method=artist.getinfo&artist=Cher&api_key=YOUR_API_KEY&format=json
	@GET("/2.0/")
	Call<ArtistWrapper> getArtistInfo(@Query("method") String method, @Query("artist") String artist,
                                      @Query("api_key") String api_key, @Query("format") String format);

	//	/2.0/?method=artist.search&artist=cher&api_key=YOUR_API_KEY&format=json
	@GET("/2.0/")
	Call<ResultsWrapper> getArtists(@Query("method") String method, @Query("artist") String artist,
									@Query("api_key") String api_key, @Query("format") String format, @Query("limit") int limit);

	@GET("/2.0/")
	Call<TopAlbumsWrapper> getTopAlbums(@Query("method") String method, @Query("artist") String artist,
										@Query("api_key") String api_key, @Query("format") String format,
										@Query("page") int page);

	//	/2.0/?method=artist.search&artist=cher&api_key=YOUR_API_KEY&format=json
	@GET("/2.0/")
	Call<TagWrapper> getTags(@Query("method") String method, @Query("artist") String artist,
							 @Query("api_key") String api_key, @Query("user") String user, @Query("format") String format);

	@GET("/2.0/")
	Call<Tag[]> getTagByArtistId(@Query("method") String method, @Query("mbid") String mbid,
			@Query("api_key") String api_key, @Query("user") String user, @Query("format") String format);
}
