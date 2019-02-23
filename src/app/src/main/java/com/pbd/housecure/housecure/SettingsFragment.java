package com.pbd.housecure.housecure;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        setEmergencySummary();
    }

    public void setEmergencySummary() {
        final String emergencyKey = getString(R.string.pref_emergency_key);
        String emergencyDefault = getString(R.string.pref_emergency_default_value);
        String emergencyValue = sharedPreferences.getString(emergencyKey, emergencyDefault);

        final EditTextPreference editTextPreference = (EditTextPreference) findPreference(emergencyKey);
        editTextPreference.setSummary(emergencyValue);

        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = o.toString();
                sharedPreferences.edit().putString(emergencyKey, newValue).apply();
                editTextPreference.setSummary(newValue);
                return true;
            }
        });
    }
}
