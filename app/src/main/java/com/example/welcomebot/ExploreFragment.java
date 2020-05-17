package com.example.welcomebot;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.MODE_PRIVATE;


/**
 * This fragment uses google maps api which displays the map and fetches data from the database to show interesting points and locations.
 */
public class ExploreFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    //Global level variable declarations
    MapView mMapView;
    private GoogleMap googleMap;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    View rootView;
    Bundle savedInstance;
    LocationBean locationBean;
    Location currentLocation;
    SeekBar range;
    TextView tv_range;
    int rangeValue=5;
    FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    String postcode="";
    BottomNavigationView bottomNavigationView;
    String defaultLanguage = "NA";

    String classify="";
    String type="";
    Bundle bundle = null;
    Query query;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        savedInstance = savedInstanceState;
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);
        loadMap(rootView,savedInstance);
        setHasOptionsMenu(true);
        range = (SeekBar)rootView.findViewById(R.id.sb_range);
        tv_range = (TextView) rootView.findViewById(R.id.tv_range);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);


        //fetching default language from shared preferences and calling the translate function based on preference.
        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");



        //---------------------------------------on click listeners for map categories-------------------------------------------------//

        Button filterButton = (Button) rootView.findViewById(R.id.btn_sports);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                classify = "Sport";
                 fetchData();
                view.setVisibility(View.GONE);

            }
        });

        //Art,Food,

        Button libraryBtn = (Button) rootView.findViewById(R.id.btn_libraries);
        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                classify = "Library";
                fetchData();
                view.setVisibility(View.GONE);

            }
        });

        Button marketBtn = (Button) rootView.findViewById(R.id.btn_markets);
        marketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                classify = "Market";
                fetchData();
                view.setVisibility(View.GONE);

            }
        });
       Button artBtn = (Button) rootView.findViewById(R.id.btn_art);
        artBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                classify = "Art";
                fetchData();
                view.setVisibility(View.GONE);

            }
        });

        Button foodBtn = (Button) rootView.findViewById(R.id.btn_food);
        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                classify = "Food";
                fetchData();
                view.setVisibility(View.GONE);

            }
        });

        Button HospitalBtn = (Button) rootView.findViewById(R.id.btn_hospitals);
        HospitalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getView().findViewById(R.id.filterCard);
                classify = "Hospital";
                fetchData();
                view.setVisibility(View.GONE);
            }
        });


        Button MedicareBtn = (Button) rootView.findViewById(R.id.btn_medicare);
        MedicareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getView().findViewById(R.id.filterCard);
                classify = "Service";
                fetchData();
                view.setVisibility(View.GONE);
            }
        });
        //------------------------------------------------------------------------------------------------------//

        //seekbar for changing the range of search in maps
        range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(defaultLanguage.equals("cn")){

                    tv_range.setText("范围（公里）"+ String.valueOf(progress));
                    rangeValue=progress;
                }else{

                    tv_range.setText("Range(km) "+ String.valueOf(progress));
                    rangeValue=progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            /**
             * call the fetchData() method when the user sets the range for searh radius
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fetchData();
            }
        });

        translateLabels();
        return rootView;

    }

    //------------------------------------------------------------------------------------------------------//

    /**
     * this method is used to translate the fragment views based on language preference.
     * //defaultlanguage - the language preference
     * //bottomNavigationView - the bottom menu
     */
    public void translateLabels(){
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);


        Button filterButton = (Button) rootView.findViewById(R.id.btn_sports);
        Button libraryBtn = (Button) rootView.findViewById(R.id.btn_libraries);
        Button marketBtn = (Button) rootView.findViewById(R.id.btn_markets);
        Button artBtn = (Button) rootView.findViewById(R.id.btn_art);
        Button foodBtn = (Button) rootView.findViewById(R.id.btn_food);
        Button HospitalBtn = (Button) rootView.findViewById(R.id.btn_hospitals);
        Button MedicareBtn = (Button) rootView.findViewById(R.id.btn_medicare);


        if(defaultLanguage.equals("cn")){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));

            tv_range.setText(getActivity().getResources().getString(R.string.tr_range));
            filterButton.setText(getActivity().getResources().getString(R.string.tr_filterButton));
            libraryBtn.setText(getActivity().getResources().getString(R.string.tr_libraryBtn));
            marketBtn.setText(getActivity().getResources().getString(R.string.tr_marketBtn));
            artBtn.setText(getActivity().getResources().getString(R.string.tr_artBtn));
            foodBtn.setText(getActivity().getResources().getString(R.string.tr_foodBtn));
            HospitalBtn.setText(getActivity().getResources().getString(R.string.tr_HospitalBtn));
            MedicareBtn.setText(getActivity().getResources().getString(R.string.tr_MedicareBtn));

            bottomNavigationView.getMenu().getItem(0).setTitle("家园");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.tr_icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.tr_icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.tr_icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));

        }
        else{
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Explore");

            tv_range.setText(getActivity().getResources().getString(R.string.range));
            filterButton.setText(getActivity().getResources().getString(R.string.filterButton));
            libraryBtn.setText(getActivity().getResources().getString(R.string.libraryBtn));
            marketBtn.setText(getActivity().getResources().getString(R.string.marketBtn));
            artBtn.setText(getActivity().getResources().getString(R.string.artBtn));
            foodBtn.setText(getActivity().getResources().getString(R.string.foodBtn));
            HospitalBtn.setText(getActivity().getResources().getString(R.string.HospitalBtn));
            MedicareBtn.setText(getActivity().getResources().getString(R.string.MedicareBtn));

            bottomNavigationView.getMenu().getItem(0).setTitle("Home");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.icon_explore));
        }
    }

    //------------------------------------------------------------------------------------------------------//

    /**
     * overdidden method used to handle supportaction bar
     * @param menu the menu items displayed on action bar
     * @param inflater inflator used to inflate the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.custom_toolbar, menu);

        MenuItem china  = menu.findItem(R.id.app_bar_China);
        MenuItem aus  = menu.findItem(R.id.app_bar_australia);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * overridden method to handle the ontap/onselect options of menu
     * @param item the item that is selected
     * @return default return true
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
            translateLabels();

        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            defaultLanguage = "au";
           translateLabels();
        }

        if(id == R.id.app_bar_search){
            View view = getView().findViewById(R.id.filterCard);
            view.setVisibility(View.VISIBLE);

            return true;
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

    /**
     * loadMap methos is used to load maps on the screen and also used for getting user's current location
     * @param view
     * @param savedInstanceState
     */
    private void loadMap(View view, Bundle savedInstanceState) {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (EasyPermissions.hasPermissions(getActivity(), perms)) {



            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    // For showing a move to my location button
                    if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);

            }
        });

            //fusedLocationClient is used to get current location or the last known location from the device

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

            //get current permission and initialise the location object
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!= null){
                        currentLocation = location;
                        double lat =  currentLocation.getLatitude();
                        double lon = currentLocation.getLongitude();
                        LatLng currentLoc = new LatLng(lat, lon);

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLoc).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                         bundle = getArguments();

                        //This data is called from exercise fragment to load only sports locations
                        if(bundle != null){
                            postcode = bundle.getString("postcode");
                            type = bundle.getString("category");
                            classify = "Sport";
                            fetchData();

                        }
                    }
                }
            });
            }
            else {
               EasyPermissions.requestPermissions(getActivity(),"This application requires your location access to load maps",
                       REQUEST_LOCATION_PERMISSION,perms);
            }


    }

//------------------------------------------------------------------------------------------------------//

    /**
     * Methods to ask permissions from the users
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        loadMap(rootView,savedInstance);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if(EasyPermissions.somePermissionPermanentlyDenied(getActivity(),perms)){
            new AppSettingsDialog.Builder(getActivity()).build().show();
        }
    }

    /**
     * Overridden method which will load the map once permissions are acquired
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            loadMap(rootView,savedInstance);

        }
    }

    @Override
    public void onResume() { super.onResume();mMapView.onResume(); }
    @Override
    public void onPause() { super.onPause(); mMapView.onPause(); }
    @Override
    public void onDestroy() { super.onDestroy();mMapView.onDestroy(); }
    @Override
    public void onLowMemory() { super.onLowMemory(); mMapView.onLowMemory(); }

    /**
     * This method is used to ask permission for gps location from the user
     * @param requestCode default constant and unique permission code
     * @param permissions the array of permissions which need to be asked
     * @param grantResults the result of users action
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //------------------------------------------------------------------------------------------------------//

    /**
     * fetchData is used to get data from firebase
     * classify -  parameter used to filter the query from the dataset and plot the data on map based on results
     * uses firestore database to access two databases
     * //param sportsDataset - contains all the records for sports locations
     * //param placesDatasetv - contains all the records for other locations
     * //LocationBean - the PoJo class used for setting values
     */
    public void fetchData() {
        googleMap.clear();
        if(classify.equals("")){
            classify="Library";
        }

        List<LocationBean> locationBeanList = new ArrayList<LocationBean>();
        db = FirebaseFirestore.getInstance();
        if(!classify.equals("Sport")){

            query = db.collection("placesDataset").whereEqualTo("Classify",classify);
        }else{
            if(bundle!=null){
                //postcode = bundle.getString("postcode");
                //String postcodeString = postcode+".0";
                //type = bundle.getString("category");
                query = db.collection("sportsDataset").whereEqualTo("Classify",classify).whereEqualTo("Type",type);
            }
            else{
                query = db.collection("sportsDataset").whereEqualTo("Classify",classify);
            }
        }

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //String response = document.getId() + " => " + document.getData();

                                //Log.i("the response", response);

                                Location location = new Location("loc");
                                locationBean = new LocationBean();
                                for (Map.Entry m : document.getData().entrySet()) {

                                    switch (m.getKey().toString()) {


                                        case "Name":
                                            locationBean.setName(m.getValue().toString());
                                            break;
                                        case "Description":
                                            locationBean.setDescription(m.getValue().toString());
                                            break;
                                        case "Classify":
                                            locationBean.setClassify(m.getValue().toString());
                                            break;
                                        case "Address":
                                            locationBean.setAddress(m.getValue().toString());
                                            break;
                                        case "Latitude":
                                            location.setLatitude(Double.parseDouble( m.getValue().toString()));
                                            break;
                                        case "Longitude":
                                            location.setLongitude(Double.parseDouble( m.getValue().toString()));
                                            break;
                                        case "Postcode":
                                            locationBean.setPostcode((int)Double.parseDouble(m.getValue().toString()));
                                            break;
                                        case "Type":
                                            locationBean.setType(m.getValue().toString());
                                            break;
                                    }


                                }
                                locationBean.setLocation(location);
                                locationBeanList.add(locationBean);
                            }
                        } else {
                            Log.w("ERROR!!!", "Error getting documents.", task.getException());
                            Toast.makeText(getContext(), "Error getting documents", Toast.LENGTH_SHORT).show();
                        }

                        if (locationBeanList.size() > 0) {

                            for (int i = 0; i < locationBeanList.size(); i++) {

                                //getting the distance of the location with users current location and loading only nearby ones
                                float distance = currentLocation.distanceTo(locationBeanList.get(i).getLocation());
                                if (distance <= (rangeValue*1000)) {

                                LatLng coordinates = new LatLng(locationBeanList.get(i).getLocation().getLatitude(), locationBeanList.get(i).getLocation().getLongitude());
                                String title = locationBeanList.get(i).getName();
                                String desc = locationBeanList.get(i).getDescription();

                                googleMap.addMarker(
                                        new MarkerOptions()
                                                .position(coordinates)
                                                .title(title)
                                                .snippet(desc)

                                );
                            }
                            }
                        }
                    }
                });
    }

    //------------------------------------------------------------------------------------------------------//
}
