package com.asimq.artists.bandninja;

import android.app.Application;
import android.content.Context;

import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.dagger.ApplicationModule;
import com.asimq.artists.bandninja.dagger.DaggerApplicationComponent;

/**
 * Base Application Class
 */

public class MyApplication extends Application {

	public static MyApplication get(Context context) {
		return (MyApplication) context.getApplicationContext();
	}

	private ApplicationComponent applicationComponent;

	public ApplicationComponent getApplicationComponent() {
		return applicationComponent;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		applicationComponent = DaggerApplicationComponent
				.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
		applicationComponent.inject(this);

	}

}
