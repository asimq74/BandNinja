package com.asimq.artists.bandninja.remote.retrofit;

import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
	private static Retrofit retrofit;
	private static final String BASE_URL = "http://ws.audioscrobbler.com";

	public static Retrofit getRetrofitInstance() {
		if (retrofit == null) {
			retrofit = new Builder()
					.baseUrl(BASE_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}
		return retrofit;
	}
}
