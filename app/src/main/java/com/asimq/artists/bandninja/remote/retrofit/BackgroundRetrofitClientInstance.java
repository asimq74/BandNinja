package com.asimq.artists.bandninja.remote.retrofit;

import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackgroundRetrofitClientInstance {
	private static Retrofit retrofit;
	private static final String BASE_URL = "http://ws.audioscrobbler.com";

	public static Retrofit getRetrofitInstance() {
		if (retrofit == null) {
			retrofit = new Builder()
					.baseUrl(BASE_URL)
					.callbackExecutor(Executors.newSingleThreadExecutor())
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}
		return retrofit;
	}
}
