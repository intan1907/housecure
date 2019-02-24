package com.pbd.housecure.housecure;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences sharedPreferences;
    private SwitchPreference switchNotification;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        setNotificationListener();
        setEmergencySummary();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        switchNotification.setChecked(NotificationManagerCompat
                .from(getContext())
                .areNotificationsEnabled());
    }

    private void setNotificationListener() {
        switchNotification = (SwitchPreference) findPreference(getString(R.string.pref_notifications_key));
        switchNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getActivity().getPackageName());
                    intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
                    startActivity(intent);
                } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void setEmergencySummary() {
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
