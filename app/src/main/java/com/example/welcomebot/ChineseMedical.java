package com.example.welcomebot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * The fragment displays the list of chinese medicine centers based onusers locatoin
 */
public class ChineseMedical extends Fragment {

    View rootview;
    LocationManager locationManager;
    RecyclerView recyclerView;

    CardLayoutAdapter cardLayoutAdapter;
    Location currentLocation;
    List<PlacesListBean> placeList = new ArrayList<>();
    String defaultLanguage = "NA";
    ProgressBar progressBar;
    //------------------------------------------------------------------------------------------------------//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_chinese_medical, container, false);

        recyclerView = rootview.findViewById(R.id.medList_recycler_view);
        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");
        setHasOptionsMenu(true);


        //capturing current location
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
                    50, mLocationListener);
        }


        progressBar = rootview.findViewById(R.id.loading_chineseMed);
        progressBar.setVisibility(View.VISIBLE);
        //method call to fetch nearby hospitals

        Location loc = new Location("anc");
        PlacesListBean placesListBean = new PlacesListBean("placeid","name","addr",loc,3.0,true,0.0f,"(0)");
        placeList.add(placesListBean);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cardLayoutAdapter = new CardLayoutAdapter(getActivity(),placeList,defaultLanguage); // our adapter takes two string array
        recyclerView.setAdapter(cardLayoutAdapter);
        placeList.clear();

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
            getNearbyChineseCenters();
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
            //translate();
            //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("中医");
            placeList.clear();
            getNearbyChineseCenters();

        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            defaultLanguage = "au";
           // ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chinese Medicine");
            placeList.clear();
            getNearbyChineseCenters();

        }

        if(id == R.id.app_bar_about){
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            intent.putExtra("SCREEN", "about");
            startActivity(intent);
        }

        if(id == android.R.id.home){

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LandingPageFragment())
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    //------------------------------------------------------------------------------------------------------//

/**
 * this method is used to fetch chinese medical centers near the users current location
 * params- current location
 * makes an api call to google places api and parses the response on the screen.
 */
public void getNearbyChineseCenters(){

    //-37.895078
    //145.05485
    String placesAPI_key = getActivity().getResources().getString(R.string.placesAPI_key);
    OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
    StringBuilder requestURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
    requestURL.append("location="+currentLocation.getLatitude()+","+currentLocation.getLongitude());
    //requestURL.append("&radius=10000");
    requestURL.append("&rankby=distance");
    requestURL.append("&type=hospital");
    requestURL.append("&keyword=chinese medicine");
    requestURL.append("&key="+placesAPI_key);

    Request request = new Request.Builder()
            .url(requestURL.toString())
            .method("GET", null)
            .build();

    client.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            call.cancel();
            Toast.makeText(getActivity(),"There was a problem in fetching results! Please try again later",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {


            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {


                    try {
                        JSONObject newsObject = new JSONObject(response.body().string());
                        JSONArray Jarray = newsObject.getJSONArray("results");

                        if(newsObject.has("error_message")){
                            Toast.makeText(getActivity(),"You have crossed the application's daily limit. Please try again after sometime.",Toast.LENGTH_LONG).show();
                        }

                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        cardLayoutAdapter = new CardLayoutAdapter(getActivity(),placeList,defaultLanguage); // our adapter takes two string array
                        recyclerView.setAdapter(cardLayoutAdapter);
                        placeList.clear();

                        if(Jarray.length()>0){

                        for (int i = 0; i < Jarray.length(); i++) {

                            PlacesListBean placesListBean = new PlacesListBean();

                            JSONObject object = Jarray.getJSONObject(i);

                            String placeId = object.getString("place_id");
                            String name = object.getString("name");
                            boolean openNow = true;
                            if(object.has("opening_hours") && !object.isNull("opening_hours")){

                                openNow = object.getJSONObject("opening_hours").getBoolean("open_now");
                            }


                            Location placeLoc = new Location("");
                            placeLoc.setLatitude(Double.parseDouble(object.getJSONObject("geometry").getJSONObject("location").getString("lat")));
                            placeLoc.setLongitude(Double.parseDouble(object.getJSONObject("geometry").getJSONObject("location").getString("lng")));

                            double rating = 0.0;
                            if(object.has("rating") && !object.isNull("rating")){
                                rating = object.getDouble("rating");
                            }

                            int total_user_rating = 0;
                            if(object.has("user_ratings_total") && !object.isNull("user_ratings_total")){
                                total_user_rating = object.getInt("user_ratings_total");
                            }

                            String address = object.getString("vicinity");

                            float distance = currentLocation.distanceTo(placeLoc)/1000;


                            placesListBean.setName(name);
                            placesListBean.setAddress(address);
                            placesListBean.setRating(rating);
                            placesListBean.setPlaceId(placeId);
                            placesListBean.setOpenNow(openNow);
                            placesListBean.setLocation(placeLoc);
                            placesListBean.setDistance(distance);
                            placesListBean.setTotal_user_ratings(String.valueOf(total_user_rating));

                            placeList.add(placesListBean);
                        }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            cardLayoutAdapter = new CardLayoutAdapter(getActivity(),placeList,defaultLanguage); // our adapter takes two string array
                            recyclerView.setAdapter(cardLayoutAdapter);
                            progressBar.setVisibility(View.GONE);

                    }else{
                        if (newsObject.has("error_message")) {

                            Toast.makeText(getActivity(), "You have crossed the application's daily limit. Please try again after sometime.", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), "Error loading data. Please try again after sometime.", Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);

                    }





                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    });


}
    //------------------------------------------------------------------------------------------------------//


}

