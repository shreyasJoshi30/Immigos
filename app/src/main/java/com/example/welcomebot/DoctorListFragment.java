package com.example.welcomebot;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This fragment is used to display a webview to show a list of doctors who can speak mandarin based on users current location
 */
public class DoctorListFragment extends Fragment {

    WebView doctor_webview;
    View rootview;
    LocationManager locationManager;

    //------------------------------------------------------------------------------------------------------//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Location currentLocation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_doctor_list, container, false);
        doctor_webview = rootview.findViewById(R.id.doctor_view);
        doctor_webview.setWebViewClient(new WebViewClient());
        doctor_webview.getSettings().setJavaScriptEnabled(true);

        //capturing current location
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                    50, mLocationListener);
        }

        return rootview;
    }
    //------------------------------------------------------------------------------------------------------//
    /**
     * this method is used to acquire users current location.
     */

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            currentLocation = location;
            getNearbyDoctors();
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
     * Method to load the webview and display results based on current location
     */

    public void getNearbyDoctors(){

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        // lat,lng, your current location

        try {
            //getting the address from the users current location
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            addresses.get(0).getPostalCode();
            System.out.println(addresses.size());
            System.out.println(addresses.get(0).getPostalCode());


            StringBuilder urlString = new StringBuilder("");
            urlString.append("https://www.chinesedoctor.com.au/search_results?");
            urlString.append("q=&");
            //urlString.append("sid=27&");
            urlString.append("location_value="+addresses.get(0).getPostalCode()+"&");
            urlString.append("lang=&");
            urlString.append("postal_code="+addresses.get(0).getPostalCode()+"&");
            urlString.append("adm_lvl_1_sn=VIC&");
            urlString.append("country_sn=AU&");
            urlString.append("location_type=postal_code&");
            urlString.append("stateSearch=VIC&");
            urlString.append("lat=" + currentLocation.getLatitude()+ "&");
            urlString.append("lng=" + currentLocation.getLongitude()+ "&");
            //urlString.append("lng=145.0548502&");
            urlString.append("place_id=ChIJb4uHlo5p1moRsD0uRnhWBBw&");
            urlString.append("google=Glen%20Huntly%20VIC%20Australia%20Doctors,%20Dentists%20&%20Allied%20Health%20GP");
            doctor_webview.loadUrl(urlString.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //------------------------------------------------------------------------------------------------------//

}
