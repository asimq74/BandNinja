package com.asimq.artists.bandninja.receivers;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RefreshNavigationDrawerReceiver extends BroadcastReceiver {

	public static final String SHOULD_REFRESH_DRAWER = "should-refresh-drawer";
	private final MutableLiveData<Boolean> shouldRefreshNavigationDrawerObservable = new MutableLiveData<>();

	public LiveData<Boolean> getData() {
		return shouldRefreshNavigationDrawerObservable;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get extra data included in the Intent
		boolean shouldRefresh = intent.getBooleanExtra(SHOULD_REFRESH_DRAWER, false);
		// entry point of data
		shouldRefreshNavigationDrawerObservable.setValue(shouldRefresh);
	}

}
