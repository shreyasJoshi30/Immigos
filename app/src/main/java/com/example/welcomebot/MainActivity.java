package com.example.welcomebot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.text.Html;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Immigos");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigationid);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.launcher_icon);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment selectedFragment = new LandingPageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3c529e")));
        //getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Immigos </font>"));


    }

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

}
