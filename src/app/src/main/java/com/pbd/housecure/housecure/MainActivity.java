package com.pbd.housecure.housecure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    //    private SharedPreferences mPreferences;
//    private String sharedPrefFile = getString(R.string.package_name);
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private TextView actionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
//        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
//        preferencesEditor.putBoolean("INTRUDER", true);
//        preferencesEditor.apply();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupActionBar();

        drawer = findViewById(R.id.drawer_layout);

        toggle = setupDrawerToggle();
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        setupDrawerContent(navigationView);

//        boolean isIntruder = mPreferences.getBoolean("INTRUDER", false);
//        if (isIntruder) {
//            Intent intentAccelerator = new Intent(this, ShakeService.class);
//            Intent intentProximity = new Intent(this, CallProximityService.class);
//            startService(intentAccelerator);
//            startService(intentProximity);
//
//        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    private void setupActionBar() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_app_bar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        actionbar.setCustomView(viewActionBar, params);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setHomeButtonEnabled(true);

        actionBarTitle = findViewById(R.id.action_bar_title);
        actionBarTitle.setText(getTitle());
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = HomeFragment.class;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                setTitle(R.string.app_name);
                break;
            case R.id.nav_log:
                fragmentClass = LogFragment.class;
                setTitle(R.string.nav_log);
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                setTitle(R.string.nav_settings);
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();

        menuItem.setChecked(true);
        actionBarTitle.setText(getTitle());
        drawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
//        preferencesEditor.putBoolean("INTRUDER", true);
//        preferencesEditor.apply();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }
}
