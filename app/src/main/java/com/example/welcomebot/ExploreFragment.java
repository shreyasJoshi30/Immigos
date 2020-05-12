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

public class ExploreFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    MapView mMapView;
    private GoogleMap googleMap;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    View rootView;
    Bundle savedInstance;
    LocationBean locationBean;
    Location currentLocation;
    String type ="";
    SeekBar range;
    TextView tv_range;
    int rangeValue=2;

    FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;

    int postcode=0;
    String category="";
    BottomNavigationView bottomNavigationView;
    String defaultLanguage = "NA";




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

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");
        translateLabels();



        //__________ on click listeners for map categories________________

        Button filterButton = (Button) rootView.findViewById(R.id.btn_sports);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                type = "Sport";
                 fetchData(type);
                view.setVisibility(View.GONE);
                //Toast.makeText(getActivity(), "button clicked!", Toast.LENGTH_LONG).show();

            }
        });

        //Art,Food,

        Button libraryBtn = (Button) rootView.findViewById(R.id.btn_libraries);
        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                type = "Library";
                fetchData(type);
                view.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "button clicked!", Toast.LENGTH_LONG).show();

            }
        });

        Button marketBtn = (Button) rootView.findViewById(R.id.btn_markets);
        marketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                type = "Market";
                fetchData(type);
                view.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "button clicked!", Toast.LENGTH_LONG).show();

            }
        });
       Button artBtn = (Button) rootView.findViewById(R.id.btn_art);
        artBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                type = "Art";
                fetchData(type);
                view.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "button clicked!", Toast.LENGTH_LONG).show();

            }
        });

        Button foodBtn = (Button) rootView.findViewById(R.id.btn_food);
        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getView().findViewById(R.id.filterCard);
                type = "Food";
                fetchData(type);
                view.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "button clicked!", Toast.LENGTH_LONG).show();

            }
        });

        range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_range.setText("Range: "+ String.valueOf(progress));
                rangeValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fetchData(type);
            }
        });

        //___________________________________Bundle params_________________________________________



        return rootView;

    }

    public void translateLabels(){
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);

        if(defaultLanguage.equals("cn")){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));

            bottomNavigationView.getMenu().getItem(0).setTitle("家园");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.tr_icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.tr_icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.tr_icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));

        }
        else{
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Explore");

            bottomNavigationView.getMenu().getItem(0).setTitle("Home");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.icon_explore));
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.custom_toolbar, menu);

        MenuItem china  = menu.findItem(R.id.app_bar_China);
        //china.setVisible(false);
        MenuItem aus  = menu.findItem(R.id.app_bar_australia);
        //aus.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);


    }


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

                // For dropping a marker at a point on the Map
                /*LatLng melbourne = new LatLng(-37.814, 144.96332);
                //googleMap.addMarker(new MarkerOptions().position(melbourne).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(melbourne).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/





            }
        });

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

                        Bundle bundle = getArguments();

                        if(bundle != null){
                            postcode = bundle.getInt("postcode");
                            category = bundle.getString("category");
                            fetchData("Sport");
                        }

                        //Toast.makeText(getContext(), "Current Location found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            }
            else {
               EasyPermissions.requestPermissions(getActivity(),"This application requires your location access to load maps",
                       REQUEST_LOCATION_PERMISSION,perms);
            }


    }



    /**
     * Methods tp ask permissions from the users
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            loadMap(rootView,savedInstance);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * fetchData is used to get data from firebase
     * @param type -  parameter used to filter the query from the dataset and plot the data on map based on results
     */
    public void fetchData(String type) {
        googleMap.clear();
        if(type.equals("")){
            type="Market";
        }

        List<LocationBean> locationBeanList = new ArrayList<LocationBean>();
        db = FirebaseFirestore.getInstance();
        CollectionReference locationData = db.collection("locationData");

        //.orderBy("Type").limit(5)


        if(String.valueOf(postcode).length()==4 && category.length()>0){
            //locationData.whereEqualTo("Classify",type).whereEqualTo("Postcode",postcode).whereEqualTo("Type",category);
            locationData.whereEqualTo("Postcode",String.valueOf(postcode)).whereEqualTo("Classify","Art");
        }
        else if(category.length()>0){
            locationData.whereEqualTo("Classify",type).whereEqualTo("Type",category);
        }
        else if(String.valueOf(postcode).length()==4){
            locationData.whereEqualTo("Classify",type).whereEqualTo("Postcode",postcode);
        }
        else{
            locationData.whereEqualTo("Classify",type);
        }

        locationData.get()
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
                                            location.setLatitude((Double) m.getValue());
                                            break;
                                        case "Longitude":
                                            location.setLongitude((Double) m.getValue());
                                            break;
                                        case "Postcode":
                                            locationBean.setPostcode(Integer.parseInt(m.getValue().toString()));
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
}
