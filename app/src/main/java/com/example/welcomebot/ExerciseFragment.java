package com.example.welcomebot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * this fragment is used to display sports locations on maps based on users choice of sport.
 */
public class ExerciseFragment extends Fragment {

    View rootview;
    Button btn_searchLocation;

    String defaultLanguage = "NA";

    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;
    BottomNavigationView bottomNavigationView;

    LocationManager locationManager;
    Location currentLocation;
    Boolean isLocationAcquired = false;

    //------------------------------------------------------------------------------------------------------//


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_exercise, container, false);
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);

        // the array list of sports choices
        ArrayList<String> category = new ArrayList<String>();

        category.add("Aerobics");
        category.add("Badminton");
        category.add("Beach Volleyball");
        category.add("Body Building");
        category.add("Cycling");
        category.add("Dancing");
        category.add("Disk Golf");
        category.add("Golf");
        category.add("Shooting Sports");
        category.add("Snooker / Billiards / Pool");
        category.add("soccer");
        category.add("Squash / Racquetball");
        category.add("Table Tennis");
        category.add("Tennis (Outdoor)");
        category.add("Volleyball");
        setHasOptionsMenu(true);

        //capturing current location
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                    50, mLocationListener);
        }


        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");
        Spinner category_spinner = (Spinner)rootview.findViewById(R.id.spinner_category);

        //initialising the array adapter  for spinner
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, category);

        category_spinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        arrayAdapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        getActivity()));

        //category_spinner.setAdapter(arrayAdapter);
        category_spinner.setPrompt("Select your favorite category!");

        // onclick listener for search button which navigates to maps fragment
        btn_searchLocation = (Button)rootview.findViewById(R.id.btn_searchLocation);
        btn_searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isLocationAcquired && category_spinner.getSelectedItem() !=null){

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        addresses.get(0).getPostalCode();
                       // System.out.println(addresses.size());
                        //System.out.println(addresses.get(0).getPostalCode());

                        Bundle bundle = new Bundle();
                        String sportsCat =  category_spinner.getSelectedItem().toString();
                        bundle.putString("postcode",addresses.get(0).getPostalCode());
                        bundle.putString("category",sportsCat);

                        //Toast.makeText(getActivity(), postcode.getText().toString(), Toast.LENGTH_LONG).show();
                        ExploreFragment exploreFragment = new ExploreFragment();
                        exploreFragment.setArguments(bundle);


                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, exploreFragment)
                                .commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(getActivity(), "Please turn on your loction and select a category ", Toast.LENGTH_LONG).show();
                }
            }
        });


        //---------------------english chinese translator-------------------

        FirebaseTranslatorOptions englishToChineseOptions =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        .setTargetLanguage(FirebaseTranslateLanguage.ZH)
                        .build();
        englishChineseTranslator =
                FirebaseNaturalLanguage.getInstance().getTranslator(englishToChineseOptions);

        englishToChineseConditions = new FirebaseModelDownloadConditions.Builder()
                .build();

        englishChineseTranslator.downloadModelIfNeeded(englishToChineseConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //isAuCndownloaded  = true;
                //   Toast.makeText(getContext(), "English-Chinese translator downloaded", Toast.LENGTH_LONG).show();
            }
        });
        //---------------------english chinese translator-------------------
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        if (defaultLanguage.equals("au")) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Exercise");
        } else {
            translate();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("行使");
        }

        return rootview;
    }

    //------------------------------------------------------------------------------------------------------//


    /**
     * LocationListener is used to listent to location changes and sets the current location
     */
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            currentLocation = location;
            isLocationAcquired = true;
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
     * Method used for translating content based on language preference
     */
    public void translate() {

        if (defaultLanguage.equals("cn")) {

            bottomNavigationView.getMenu().getItem(0).setTitle("家园");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.tr_icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.tr_icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.tr_icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));


            Button btn_searchLocation = rootview.findViewById(R.id.btn_searchLocation);
        englishChineseTranslator.translate(getActivity().getResources().getString(R.string.exerciseDesc)).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView id_exercise = rootview.findViewById(R.id.id_exercise);
                        id_exercise.setText(s);
                        btn_searchLocation.setText("查找我附近的位置");
                    }
                });

            }
        });
    }else{
            bottomNavigationView.getMenu().getItem(0).setTitle("Home");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.icon_explore));
        }
    }

    //------------------------------------------------------------------------------------------------------//


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.custom_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item  = menu.findItem(R.id.app_bar_search);
        item.setVisible(false);

    }

    //------------------------------------------------------------------------------------------------------//


    /**
     * Overridden method to handle actionbar menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.app_bar_China){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","cn");
            editor.commit();
            defaultLanguage = "cn";
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("行使");
            //changeLabelToChinese();
            translate();
        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Exercise");
            defaultLanguage = "au";

            String text = getActivity().getResources().getString(R.string.exerciseDesc);
            TextView id_exercise = rootview.findViewById(R.id.id_exercise);
            id_exercise.setText(text);
            Button btn = rootview.findViewById(R.id.btn_searchLocation);
            btn.setText("Find Locations Near Me");
            translate();

            //changeLabelToEnglish();
        }

        if(id == R.id.app_bar_about){
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            intent.putExtra("SCREEN", "about");
            startActivity(intent);
        }

        if(id == android.R.id.home){
            BottomNavigationView bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);
            bottomNavigationView.setSelectedItemId(R.id.nav_landingPage);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LandingPageFragment())
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }
    //------------------------------------------------------------------------------------------------------//

}
