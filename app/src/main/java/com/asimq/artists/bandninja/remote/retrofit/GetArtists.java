package com.asimq.artists.bandninja.remote.retrofit;

import com.asimq.artists.bandninja.data.ArtistInfoPojo;
import com.asimq.artists.bandninja.data.ArtistsPojo;
import com.asimq.artists.bandninja.data.TopAlbumsPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetArtists {

    //	/2.0/?method=artist.search&artist=cher&api_key=YOUR_API_KEY&format=json
    @GET("/2.0/")
    Call<ArtistsPojo> getArtists(@Query("method") String method, @Query("artist") String artist,
                                 @Query("api_key") String api_key, @Query("format") String format);

    //    /2.0/?method=artist.getinfo&artist=Cher&api_key=YOUR_API_KEY&format=json
    @GET("/2.0/")
    Call<ArtistInfoPojo> getArtistInfo(@Query("method") String method, @Query("artist") String artist,
                                       @Query("api_key") String api_key, @Query("format") String format);

    @GET("/2.0/")
    Call<TopAlbumsPojo> getTopAlbums(@Query("method") String method, @Query("artist") String artist,
                                     @Query("api_key") String api_key, @Query("format") String format,
        @Query("page") int page);
}
