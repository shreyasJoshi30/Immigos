package com.example.welcomebot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;

import androidx.fragment.app.Fragment;


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



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =  new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment selectedFragment = null;
            switch(menuItem.getItemId()){
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
