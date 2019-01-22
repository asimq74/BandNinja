package com.asimq.artists.bandninja.remote.retrofit;

import com.asimq.artists.bandninja.json.AlbumInfoWrapper;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.json.ResultsWrapper;
import com.asimq.artists.bandninja.json.Tag;
import com.asimq.artists.bandninja.json.TagWrapper;
import com.asimq.artists.bandninja.json.TopAlbumsWrapper;
import com.asimq.artists.bandninja.json.TopArtistsByTagWrapper;
import com.asimq.artists.bandninja.json.TopTagsWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetMusicInfo {

	//	/2.0/?method=album.getinfo&api_key=YOUR_API_KEY&artist=Cher&album=Believe&format=json
	@GET("/2.0/")
	Call<AlbumInfoWrapper> getAlbumInfo(@Query("method") String method, @Query("artist") String artist,
			@Query("album") String album, @Query("api_key") String api_key,
			@Query("format") String format);

	//	/2.0/?method=album.getinfo&api_key=YOUR_API_KEY&artist=Cher&album=Believe&format=json
	@GET("/2.0/")
	Call<AlbumInfoWrapper> getAlbumInfo(@Query("method") String method, @Query("mbid") String mbId,
			@Query("api_key") String api_key, @Query("format") String format);

	//    /2.0/?method=artist.getinfo&artist=Cher&api_key=YOUR_API_KEY&format=json
	@GET("/2.0/")
	Call<ArtistWrapper> getArtistInfo(@Query("method") String method, @Query("artist") String artist,
			@Query("api_key") String api_key, @Query("format") String format);

	//	/2.0/?method=artist.search&artist=cher&api_key=YOUR_API_KEY&format=json
	@GET("/2.0/")
	Call<ResultsWrapper> getArtists(@Query("method") String method, @Query("artist") String artist,
			@Query("api_key") String api_key, @Query("format") String format, @Query("limit") int limit);

	@GET("/2.0/")
	Call<Tag[]> getTagByArtistId(@Query("method") String method, @Query("mbid") String mbid,
			@Query("api_key") String api_key, @Query("user") String user, @Query("format") String format);

	//	/2.0/?method=artist.search&artist=cher&api_key=YOUR_API_KEY&format=json
	@GET("/2.0/")
	Call<TagWrapper> getTags(@Query("method") String method, @Query("artist") String artist,
			@Query("api_key") String api_key, @Query("user") String user, @Query("format") String format);

	@GET("/2.0/")
	Call<TopAlbumsWrapper> getTopAlbums(@Query("method") String method, @Query("artist") String artist,
			@Query("api_key") String api_key, @Query("format") String format,
			@Query("page") int page);

	//    method=tag.getTopTagsLiveData&api_key=06aec4c91800f972d32c0d702c003bd5&format=json
	@GET("/2.0/")
	Call<TopTagsWrapper> getTopTags(@Query("method") String method, @Query("api_key") String api_key,
			@Query("format") String format);

//	http://ws.audioscrobbler.com/2.0/?method=tag.gettopartists&tag=alternative&api_key=key&format=json
	@GET("/2.0/")
	Call<TopArtistsByTagWrapper> getArtistByTag(@Query("method") String method, @Query("tag") String tag, @Query("api_key") String api_key,
			@Query("format") String format);
}
