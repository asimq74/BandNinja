package com.asimq.artists.bandninja;

import java.util.ArrayList;
import java.util.HashSet;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.asimq.artists.bandninja.utils.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	public static class MyPreferenceFragment extends PreferenceFragment {

		/**
		 * A preference value change listener that updates the preference's summary
		 * to reflect its new value.
		 */
		private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				if (preference instanceof MultiSelectListPreference) {
					// For multi select list preferences we should show a list of the selected options
					MultiSelectListPreference listPreference = (MultiSelectListPreference) preference;
					CharSequence[] values = listPreference.getEntries();
					StringBuilder options = new StringBuilder();
					for (String stream : (HashSet<String>) newValue) {
						int index = listPreference.findIndexOfValue(stream);
						if (index >= 0) {
							if (options.length() != 0) {
								options.append(", ");
							}
							options.append(values[index]);
						}
					}
					preference.setSummary(options);
				} else if (preference instanceof ListPreference) {
					// For list preferences, look up the correct display value in
					// the preference's 'entries' list.
					String stringValue = newValue.toString();
					ListPreference listPreference = (ListPreference) preference;
					int index = listPreference.findIndexOfValue(stringValue);

					// Set the summary to reflect the new value.
					preference.setSummary(
							index >= 0
									? listPreference.getEntries()[index]
									: null);

				} else if (preference instanceof EditTextPreference) {
					String stringValue = newValue.toString();
					if (preference.getKey().equals("display_name_key")) {
						// update the changed gallery name to summary filed
						preference.setSummary(stringValue);
					}
				}
				return true;
			}
		};

		private static void bindPreferenceSummaryToValue(Preference preference) {
			// Set the listener to watch for value changes.
			preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

			// Trigger the listener immediately with the preference's
			// current value.
			SharedPreferences prefs = preference.getSharedPreferences();
			Object value;
			if (preference instanceof MultiSelectListPreference) {
				value = prefs.getStringSet(preference.getKey(), new HashSet<>());
			} else if (preference instanceof SwitchPreference) {
				value = prefs.getBoolean(preference.getKey(), false);
			} else {
				value = prefs.getString(preference.getKey(), "");
			}
			sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
		}


		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			setHasOptionsMenu(true);
			// gallery EditText change listener
			bindPreferenceSummaryToValue(findPreference(getString(R.string.display_name_key)));

			// notification preference change listener
			bindPreferenceSummaryToValue(findPreference(getString(R.string.favorite_genre_key)));

			final SwitchPreference locationSwitchPreference = (SwitchPreference) findPreference(this.getResources()
					.getString(R.string.location_switch_key));
// SwitchPreference preference change listener
			locationSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object o) {
					if (locationSwitchPreference.isChecked()) {
						Toast.makeText(getActivity(), "Unchecked", Toast.LENGTH_SHORT).show();

						// Checked the switch programmatically
						locationSwitchPreference.setChecked(false);
					} else {
						Toast.makeText(getActivity(), "Checked", Toast.LENGTH_SHORT).show();

						// Unchecked the switch programmatically
						locationSwitchPreference.setChecked(true);
						((SettingsActivity) getActivity()).determineWhetherToAskForPermissions();
					}
					return false;
				}
			});
		}

	}

	public void determineWhetherToAskForPermissions() {
		permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
		permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

		permissionsToRequest = findUnAskedPermissions(permissions);
		//get the permissions we have asked for before but are not granted..
		//we will store this in a global list to access later.

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			if (!permissionsToRequest.isEmpty()) {
				requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
				return;
			}
			startGoogleApiClient();
			return;
		}
		startGoogleApiClient();
	}

	private final static int ALL_PERMISSIONS_RESULT = 101;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private long FASTEST_INTERVAL = 5000; /* 5 secs */
	final String TAG = this.getClass().getSimpleName();
	private long UPDATE_INTERVAL = 15000;
	private GoogleApiClient mGoogleApiClient;
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
		Log.d(TAG, "connection failed");
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "connection suspended");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
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
				permissionsRejected.clear();
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
					startGoogleApiClient();
				}

				break;
		}

	}

	private void processLocation() {

		String address = "";
		address = Util.getPostalCode(this, mLocation.getLatitude(), mLocation.getLongitude());
		if (!address.isEmpty()) {
			SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			Log.d(TAG, "Postal code found: " + address);
			String json = mLocation == null ? null : new Gson().toJson(mLocation);
			editor.putString("location", json);
			editor.commit();
			String locationInserted = sharedPref.getString("location", "");
			Log.d(TAG, "location inserted: " + locationInserted);
		}
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(this)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

	public void startGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
		mGoogleApiClient.connect();
	}

	protected void startLocationUpdates() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
		}

		if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);
		}

	}

	public void stopLocationUpdates() {
		if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi
					.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}
}