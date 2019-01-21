package com.asimq.artists.bandninja;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class ConnectionActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private final static int ALL_PERMISSIONS_RESULT = 101;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private long FASTEST_INTERVAL = 5000; /* 5 secs */
	final String TAG = this.getClass().getSimpleName();
	private long UPDATE_INTERVAL = 15000;  /* 15 secs */
	TextView latLng;
	private AdView adView;
	GoogleApiClient mGoogleApiClient;
	Location mLocation;
	private LocationRequest mLocationRequest;
	private ArrayList<String> permissions = new ArrayList();
	private ArrayList<String> permissionsRejected = new ArrayList();
	private ArrayList<String> permissionsToRequest;

	private boolean canMakeSmores() {
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
	}

	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else
				finish();

			return false;
		}
		return true;
	}

	private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
		ArrayList result = new ArrayList();

		for (String perm : wanted) {
			if (!hasPermission(perm)) {
				result.add(perm);
			}
		}

		return result;
	}

	private boolean hasPermission(String permission) {
		if (canMakeSmores()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
			}
		}
		return true;
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {

		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

		if (mLocation != null) {
			processLocation();
		}

		startLocationUpdates();

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);

		// Initialize the Mobile Ads SDK.
		MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

		// Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
		// values/strings.xml.
		adView = findViewById(R.id.ad_view);



		latLng = findViewById(R.id.latLng);

		permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
		permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

		permissionsToRequest = findUnAskedPermissions(permissions);
		//get the permissions we have asked for before but are not granted..
		//we will store this in a global list to access later.

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			if (permissionsToRequest.size() > 0)
				requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
		}

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopLocationUpdates();
	}

	@Override
	public void onLocationChanged(Location location) {

		if (location != null)
			mLocation = location;
			processLocation();
	}

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

		switch (requestCode) {

			case ALL_PERMISSIONS_RESULT:
				for (String perms : permissionsToRequest) {
					if (!hasPermission(perms)) {
						permissionsRejected.add(perms);
					}
				}

				if (permissionsRejected.size() > 0) {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
							showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
												requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
											}
										}
									});
							return;
						}
					}

				} else {

					startLocationUpdates();
				}

				break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!checkPlayServices()) {
			latLng.setText("Please install Google Play services.");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	private void processLocation() {
		// Create an ad request. Check your logcat output for the hashed device ID to
		// get test ads on a physical device. e.g.
		// "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.setLocation(mLocation)
				.addTestDevice("D64BF957D55963D6B121A98C94BEBA22")
				.build();

		adView.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				// Code to be executed when when the user is about to return
				// to the app after tapping on an ad.
				Log.i(TAG, "Ad closed");
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				// Code to be executed when an ad request fails.
				Log.i(TAG, "Ad failed to load errorCode=" + errorCode);
			}

			@Override
			public void onAdLeftApplication() {
				// Code to be executed when the user has left the app.
				Log.i(TAG, "Ad left the application");
			}

			@Override
			public void onAdLoaded() {
				// Code to be executed when an ad finishes loading.
				Log.i(TAG, "Ad finished loading");
			}

			@Override
			public void onAdOpened() {
				// Code to be executed when an ad opens an overlay that
				// covers the screen.
				Log.i(TAG, "Ad opened");
			}
		});
		// Start loading the ad in the background.
		adView.loadAd(adRequest);

		List<Address> addresses = new ArrayList<>();
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
// lat,lng, your current location
		try {
			addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
		} catch (IOException e) {
			latLng.setText("Latitude : " + mLocation.getLatitude() + " , Longitude : " + mLocation.getLongitude());
		}
		if (!addresses.isEmpty()) {
			latLng.setText("zip code : " + addresses.get(0).getPostalCode());
		}
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(ConnectionActivity.this)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

	protected void startLocationUpdates() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
		}

		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

	}

	public void stopLocationUpdates() {
		if (mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi
					.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}
}