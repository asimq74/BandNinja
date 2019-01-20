package com.asimq.artists.bandninja;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import com.asimq.artists.bandninja.ui.CustomMultiSelectListPreference;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            PreferenceManager prefMgr = getPreferenceManager();
            prefMgr.setSharedPreferencesName("BAND_NINJA_PREFERENCES");
            addPreferencesFromResource(R.xml.preferences);
            setHasOptionsMenu(true);
            // gallery EditText change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.display_name_key)));

            // notification preference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.favorite_genre_key)));

        }


        private static void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's
            // current value.
            SharedPreferences prefs = preference.getSharedPreferences();
            Object value;
            if (preference instanceof MultiSelectListPreference) {
                value = prefs.getStringSet(preference.getKey(), new HashSet<>());
            } else {
                value = prefs.getString(preference.getKey(), "");
            }
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();

                if (preference instanceof MultiSelectListPreference) {
                    // For multi select list preferences we should show a list of the selected options
                    MultiSelectListPreference listPreference = (MultiSelectListPreference) preference;
                    CharSequence[] values = listPreference.getEntries();
                    StringBuilder options = new StringBuilder();
                    for(String stream : (HashSet<String>) newValue) {
                        int index = listPreference.findIndexOfValue(stream);
                        if (index >= 0) {
                            if (options.length() != 0) {
                                options.append(", ");
                            }
                            options.append(values[index]);
                        }
                    }
                    preference.setSummary(options);
                }
                else if (preference instanceof ListPreference) {
                    // For list preferences, look up the correct display value in
                    // the preference's 'entries' list.
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    // Set the summary to reflect the new value.
                    preference.setSummary(
                            index >= 0
                                    ? listPreference.getEntries()[index]
                                    : null);

                } else if (preference instanceof EditTextPreference) {
                    if (preference.getKey().equals("display_name_key")) {
                        // update the changed gallery name to summary filed
                        preference.setSummary(stringValue);
                    }
                } else {
                    preference.setSummary(stringValue);
                }
                return true;
            }
        };
    }

}