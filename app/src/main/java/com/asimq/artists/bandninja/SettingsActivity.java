package com.asimq.artists.bandninja;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import android.Manifest;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.asimq.artists.bandninja.utils.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
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
public class SettingsActivity extends PreferenceActivity {

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
						// Checked the switch programmatically
						Toast.makeText(getActivity(),
								getString(R.string.locationServicesDisabled),
								Toast.LENGTH_LONG).show();
						locationSwitchPreference.setChecked(false);
					} else {
						if (!(Util.isConnected(getActivity()))) {
							Toast.makeText(getActivity(),
									getString(R.string.locationServicesRequireInternet),
									Toast.LENGTH_LONG).show();
							locationSwitchPreference.setChecked(false);
							return false;
						}

						if (!(Util.isGooglePlayServicesAvailable(getActivity()))) {
							Toast.makeText(getActivity(),
									getString(R.string.locationServicesRequireGooglePlay),
									Toast.LENGTH_LONG).show();
							locationSwitchPreference.setChecked(false);
							return false;
						}

						Toast.makeText(getActivity(),
								getString(R.string.locationServicesEnabled),
								Toast.LENGTH_LONG).show();
						locationSwitchPreference.setChecked(true);
						((SettingsActivity) getActivity()).determineWhetherToAskForPermissions();
					}
					return true;
				}
			});
		}

	}

	private static final int ALL_PERMISSIONS_RESULT = 101;
	private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
	static final String TAG = SettingsActivity.class.getSimpleName();
	/**
	 * Provides the entry point to the Fused Location Provider API.
	 */
	private FusedLocationProviderClient mFusedLocationClient;
	/**
	 * Represents a geographical location.
	 */
	protected Location mLastLocation;
	private ArrayList<String> permissionsRejected = new ArrayList();

	/**
	 * Return the current state of the permissions needed.
	 */
	private boolean checkPermissions() {
		int permissionState = ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_COARSE_LOCATION);
		return permissionState == PackageManager.PERMISSION_GRANTED;
	}

	public void determineWhetherToAskForPermissions() {
		if (!checkPermissions()) {
			requestPermissions();
		} else {
			getLastLocation();
		}
	}

	/**
	 * Provides a simple way of getting a device's location and is well suited for
	 * applications that do not require a fine-grained location and that do not need location
	 * updates. Gets the best and most recent location currently available, which may be null
	 * in rare cases when a location is not available.
	 * <p>
	 * Note: this method should be called after location permission has been granted.
	 */
	@SuppressWarnings("MissingPermission")
	private void getLastLocation() {
		mFusedLocationClient.getLastLocation()
				.addOnCompleteListener(this, task -> {
					if (task.isSuccessful() && task.getResult() != null) {
						mLastLocation = task.getResult();
						Log.d(TAG, String.format(Locale.ENGLISH, "latitude: %f", mLastLocation.getLatitude()));
						Log.d(TAG, String.format(Locale.ENGLISH, "longitude: %f", mLastLocation.getLongitude()));
						processLocation();
					} else {
						Log.w(TAG, "getLastLocation:exception", task.getException());
						showSnackbar(getString(R.string.no_location_detected));
					}
				});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		Log.i(TAG, "onRequestPermissionResult");
		if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
			if (grantResults.length <= 0) {
				// If user interaction was interrupted, the permission request is cancelled and you
				// receive empty arrays.
				Log.i(TAG, "User interaction was cancelled.");
			} else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission granted.
				getLastLocation();
			} else {
				if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (shouldShowRequestPermissionRationale(permissionsRejected.get(0)))) {
					showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
										requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
									}
								}
							});
				}
			}
		}
	}

	private void processLocation() {

		String address = "";
		address = Util.getPostalCode(this, mLastLocation.getLatitude(), mLastLocation.getLongitude());
		if (!address.isEmpty()) {
			SharedPreferences sharedPref = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			Log.d(TAG, "Postal code found: " + address);
			String json = mLastLocation == null ? null : new Gson().toJson(mLastLocation);
			editor.putString("location", json);
			editor.commit();
			String locationInserted = sharedPref.getString("location", "");
			Log.d(TAG, "location inserted: " + locationInserted);
		}
	}

	private void requestPermissions() {
		boolean shouldProvideRationale =
				ActivityCompat.shouldShowRequestPermissionRationale(this,
						Manifest.permission.ACCESS_COARSE_LOCATION);

		// Provide an additional rationale to the user. This would happen if the user denied the
		// request previously, but didn't check the "Don't ask again" checkbox.
		if (shouldProvideRationale) {
			Log.i(TAG, "Displaying permission rationale to provide additional context.");

			showSnackbar(R.string.permission_rationale, android.R.string.ok,
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							// Request permission
							startLocationPermissionRequest();
						}
					});

		} else {
			Log.i(TAG, "Requesting permission");
			// Request permission. It's possible this can be auto answered if device policy
			// sets the permission in a given state or the user denied the permission
			// previously and checked "Never ask again".
			startLocationPermissionRequest();
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

	/**
	 * Shows a {@link Snackbar} using {@code text}.
	 *
	 * @param text The Snackbar text.
	 */
	private void showSnackbar(final String text) {
		View container = findViewById(android.R.id.content);
		if (container != null) {
			Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
		}
	}

	/**
	 * Shows a {@link Snackbar}.
	 *
	 * @param mainTextStringId The id for the string resource for the Snackbar text.
	 * @param actionStringId   The text of the action item.
	 * @param listener         The listener associated with the Snackbar action.
	 */
	private void showSnackbar(final int mainTextStringId, final int actionStringId,
			View.OnClickListener listener) {
		Snackbar.make(findViewById(android.R.id.content),
				getString(mainTextStringId),
				Snackbar.LENGTH_INDEFINITE)
				.setAction(getString(actionStringId), listener).show();
	}

	private void startLocationPermissionRequest() {
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
				REQUEST_PERMISSIONS_REQUEST_CODE);
	}

}