package com.example.welcomebot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;


public class MainActivity extends AppCompatActivity {

final int LOCATION_PERMISSION_CODE = 30;
final int CALENDAR_READ_PERMISSION_CODE = 31;
final int CALENDAR_WRITE_PERMISSION_CODE = 32;
LocationManager locationManager;
    //------------------------------------------------------------------------------------------------------//


    @Override
    public void onBackPressed() {


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof LandingPageFragment))
        {
            LandingPageFragment dashboardFragment = new LandingPageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    dashboardFragment).commit();
        }else {
            super.onBackPressed();
        }
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Immigos");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigationid);
        bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.launcher_icon);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment selectedFragment = new LandingPageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3c529e")));
        //getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Immigos </font>"));


        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5,
                    50, mLocationListener);
            //do nothing
        }else{
            requestLocationPermission();
        }


        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            //do nothing
        }else{
            requestCalendarPermission();
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            //do nothing
        }else{
            requestCalendarWritePermission();
        }



    }
    //------------------------------------------------------------------------------------------------------//
    /**overridden methods for current location
     * mlocationListener to capture the current location of device
     */

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    //------------------------------------------------------------------------------------------------------//

    /**
     * Code to request permissions for ACCESS_FINE_LOCATION
     */

    private void requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Required")
                    .setMessage("The application required location permission to show you nearby places")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
        }

    }
    //------------------------------------------------------------------------------------------------------//

    /**
     * Method to requesy calendar permission READ_CALENDAR and WRITE_CALENDAR
     */

    private void requestCalendarPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CALENDAR)){

            new AlertDialog.Builder(this)
                    .setTitle("Calendar Permission Required")
                    .setMessage("The application requires Calendar permission to read events")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CALENDAR},CALENDAR_READ_PERMISSION_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALENDAR},CALENDAR_READ_PERMISSION_CODE);
        }

    }

    //------------------------------------------------------------------------------------------------------//

    private void requestCalendarWritePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_CALENDAR)){

            new AlertDialog.Builder(this)
                    .setTitle("Calendar Permission Required")
                    .setMessage("The application requires Calendar permission to add events")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_CALENDAR},CALENDAR_WRITE_PERMISSION_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();


        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_CALENDAR},CALENDAR_WRITE_PERMISSION_CODE);
        }

    }

    //------------------------------------------------------------------------------------------------------//



    /**
     * This function is used to toggle between fragments based on selected choice
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =  new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment selectedFragment = null;
            switch(menuItem.getItemId()){

              case R.id.nav_landingPage:
                    selectedFragment = new LandingPageFragment();
                    break;
              case R.id.nav_chat:
                  selectedFragment = new ChatFragment();
                  break;
               case R.id.nav_home:
                   selectedFragment =  new HomeFragment();
                   break;
                case R.id.nav_events:
                    selectedFragment = new EventsFragment();
                    break;
                case R.id.nav_explore:
                    selectedFragment = new ExploreFragment();
                    break;

          }
          getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                  selectedFragment).commit();
          return true;
        }
    };
    //------------------------------------------------------------------------------------------------------//


}
