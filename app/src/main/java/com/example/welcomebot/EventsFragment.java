package com.example.welcomebot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class EventsFragment extends Fragment {

    View rootivew;
    LinearLayout.LayoutParams layoutParams;
    LinearLayout.LayoutParams cardParams;
    ScrollView.LayoutParams scrollParams;
    ScrollView scrollView;
    LinearLayout homeLayout;
    LinearLayout containerCardLayout;
    MaterialCardView searchEventFilter;
    String defaultLanguage = "NA";


    //translator
    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;
    //String imageUrl;

    String tr_eventName;
    String tr_eventDesc;
    String tr_startTime;
    String tr_endTime;
    String tr_address;
    BottomNavigationView bottomNavigationView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootivew =inflater.inflate(R.layout.fragment_events,container,false);

        setHasOptionsMenu(true);

        homeLayout = (LinearLayout) rootivew.findViewById(R.id.v_events);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        scrollParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(scrollParams);
        homeLayout.addView(scrollView);
        containerCardLayout  = new LinearLayout(getActivity());
        containerCardLayout.setLayoutParams(scrollParams);
        containerCardLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(containerCardLayout);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        searchEventFilter = rootivew.findViewById(R.id.searchEventFilter);
        //searchEventFilter.setContentPadding(30, 30, 30, 30);

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");
        translateLabels();

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
                //Toast.makeText(getContext(), "English-Chinese translator downloaded", Toast.LENGTH_LONG).show();
            }
        });
        //---------------------english chinese translator-------------------


        try {
            getEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootivew;
    }

    public void translateLabels(){
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);

        if(defaultLanguage.equals("cn")){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getResources().getString(R.string.tr_icon_events));

            bottomNavigationView.getMenu().getItem(0).setTitle("家园");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.tr_icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.tr_icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.tr_icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));

        }
        else{
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Events");

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
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item  = menu.findItem(R.id.app_bar_search);
        item.setVisible(false);
    }

    /**
     * overridden method to manage the menu options on the top
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
            translateLabels();
            //Toast.makeText(getActivity(), "Language switched to Chinese", Toast.LENGTH_LONG).show();
            containerCardLayout.removeAllViews();
            try {
                getEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            defaultLanguage = "au";
            //Toast.makeText(getActivity(), "Language switched to English", Toast.LENGTH_LONG).show();
            containerCardLayout.removeAllViews();
            translateLabels();
            try {
                getEvents();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //changeLabelToEnglish();
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


    public void getEvents() throws Exception{

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        StringBuilder requestURL = new StringBuilder("http://api.eventfinda.com.au/v2/events.json?");
        requestURL.append("fields=event:(url,name,description,sessions,point,datetime_start,datetime_end,address,images),session:(timezone,datetime_start)");
        requestURL.append("&order=date&row=20&location=4");

        /*if(!searchString.getText().toString().equals("")){
            requestURL.append("&q="+searchString);
        }*/

        Request request = new Request.Builder()
                .url(requestURL.toString())
                .method("GET", null)
                .addHeader("Authorization", "Basic aW1taWdvc2FwcDptajhjZnh3ZDlxd3Y=")
                .build();


        client.newCall(request).enqueue(new Callback() {

            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String newsResponse = response.body().string();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject newsObject = new JSONObject(newsResponse);
                            JSONArray Jarray = newsObject.getJSONArray("events");

                            homeLayout = (LinearLayout) rootivew.findViewById(R.id.v_events);
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject object = Jarray.getJSONObject(i);


                                String eventUrl = object.getString("url");
                                String eventName = object.getString("name");
                                String eventDesc = object.getString("description");
                                JSONObject location = object.getJSONObject("point");
                                String startTime = object.getString("datetime_start");
                                String endTime = object.getString("datetime_end");
                                String address = object.getString("address");
                                //final String imageUrl;
                                JSONArray imageArray = object.getJSONObject("images").getJSONArray("images").getJSONObject(0).getJSONObject("transforms").getJSONArray("transforms");


                                /*for (int j = 0; j < imageArray.length(); j++) {

                                    JSONObject imageObj = imageArray.getJSONObject(j);
                                    if (imageObj.getInt("transformation_id") == 7) {
                                        imageUrl = imageObj.getString("url");
                                        break;
                                    }

                                }*/

                                String imageUrl = imageArray.getJSONObject(imageArray.length()-1).getString("url");


                                layoutParams.setMargins(30, 30, 30, 30);


                                //ImageView newsImage =  new ImageView(getActivity());
                                //create multiple cards....
                                if (defaultLanguage.equals("au")) {


                                MaterialCardView cardView = createCard(eventName, eventDesc, startTime, endTime, address, location, eventUrl, imageUrl);
                                containerCardLayout.addView(cardView, layoutParams);
                                }
                                else{

                                    /*StringBuilder message = new StringBuilder("");
                                    message.append(eventName).append("||")
                                            .append(eventDesc).append("||")
                                            .append(startTime).append("||")
                                            .append(endTime).append("||")
                                            .append(address);*/

                                    englishChineseTranslator.translate(eventName).addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String translated_eventName) {

                                            englishChineseTranslator.translate(eventDesc).addOnSuccessListener(new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String translated_eventDesc) {


                                                    englishChineseTranslator.translate(address).addOnSuccessListener(new OnSuccessListener<String>() {
                                                        @Override
                                                        public void onSuccess(String translated_address) {
                                                            tr_eventName = translated_eventName;
                                                            tr_eventDesc = translated_eventDesc;
                                                            tr_address = translated_address;
                                                            MaterialCardView cardView = null;
                                                            try {
                                                                cardView = createCard(tr_eventName, tr_eventDesc, startTime, endTime, tr_address, location, eventUrl, imageUrl);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            containerCardLayout.addView(cardView, layoutParams);

                                                        }
                                                    });

                                                }
                                            });


                                     /*       String tr_eventname=s.split("\\|\\|")[0];
                                            String tr_eventDesc = s.split("\\|\\|")[1];
                                            String tr_eventAddr = s.split("\\|\\|")[4];
                                            MaterialCardView cardView = null;
                                            try {
                                                cardView = createCard(tr_eventname, tr_eventDesc, startTime, endTime, tr_eventAddr, location, eventUrl, imageUrl);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            containerCardLayout.addView(cardView, layoutParams);*/

                                        }
                                    });


                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });




    }

    public MaterialCardView createCard(String eventName,String eventDesc, String startTime,String endTime,String address,JSONObject
            location, String eventUrl,String imageUrl) throws Exception{


        LinearLayout cardLayout = new LinearLayout(getActivity());
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,LinearLayout.LayoutParams.MATCH_PARENT);
        cardLayout.setLayoutParams(LLParams);

        MaterialCardView cardView = new MaterialCardView(getActivity());
        cardView.setRadius(15);

        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        cardView.setContentPadding(30, 30, 30, 30);
        cardView.setMaxCardElevation(15);
        cardView.setCardElevation(9);

        //Textview that will contain the title of news bulletin
        TextView eventTitle = new TextView(getActivity());
        eventTitle.setLayoutParams(layoutParams);
        eventTitle.setText(eventName);
        eventTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        eventTitle.setTextColor(Color.BLACK);

        // Textview that will contain the short description
        TextView tv = new TextView(getActivity());
        tv.setLayoutParams(layoutParams);
        tv.setText(eventDesc);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setTextColor(Color.BLACK);

        ImageView eventsImage = new ImageView(getActivity());
        eventsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(getActivity()).load(imageUrl).into(eventsImage);
        //newsImage.setImageResource(R.drawable.home_image);

        // Textview that will contain the short content

        TextView tvContent = new TextView(getActivity());
        tvContent.setLayoutParams(layoutParams);

            tvContent.setText("View more...");
            tvContent.setGravity(Gravity.RIGHT);

        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tvContent.setTextColor(Color.GRAY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            eventTitle.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            tv.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            //tvContent.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        TextView eventDate = new TextView(getActivity());
        eventDate.setLayoutParams(layoutParams);
        eventDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        eventDate.setTextColor(Color.BLACK);
        eventDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_date_range_black_24dp, 0, 0, 0);
        String datePart_startTime = startTime.split(" ")[0];
        String datePart_endTime = endTime.split(" ")[0];

        SimpleDateFormat _oldDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat _newDate = new SimpleDateFormat("dd-MMM-yy");
        Date parse_datePart_startTime = _oldDate.parse(datePart_startTime);
        Date parse_datePart_endTime = _oldDate.parse(datePart_endTime);

        if(datePart_startTime.equals(datePart_endTime)){

            eventDate.setText(" "+_newDate.format(parse_datePart_startTime));
        }
        else{
            eventDate.setText(" "+_newDate.format(parse_datePart_startTime)+" to "+_newDate.format(parse_datePart_endTime));
        }


        TextView timings = new TextView(getActivity());
        timings.setLayoutParams(layoutParams);
        timings.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        timings.setTextColor(Color.BLACK);
        timings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_watch_later_blue_24dp, 0, 0, 0);

        String timepart_startTime = startTime.split(" ")[1];
        String timepart_endtTime = endTime.split(" ")[1];

        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
        Date startTimeDate = _24HourSDF.parse(timepart_startTime);
        Date endTimeDate = _24HourSDF.parse(timepart_endtTime);

        timings.setText(" "+_12HourSDF.format(startTimeDate) + " - "+ _12HourSDF.format(endTimeDate));

        TextView eventAddress = new TextView(getActivity());
        eventAddress.setLayoutParams(layoutParams);
        eventAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        eventAddress.setTextColor(Color.BLACK);
        eventAddress.setText(" "+address);
        eventAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_on_blue_24dp, 0, 0, 0);




        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(eventUrl);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        cardLayout.addView(eventsImage);
        //scrollView.setLayoutParams(layoutParams);
        //scrollView.addView(cardLayout);
        cardLayout.addView(eventTitle);
        cardLayout.addView(tv);
        cardLayout.addView(eventDate);
        cardLayout.addView(timings);
        cardLayout.addView(eventAddress);
        cardLayout.addView(tvContent);
        cardView.addView(cardLayout);

      /* m_eventName.observe(getActivity(), new Observer<String>() {
           @Override
           public void onChanged(String s) {
               eventTitle.setText(m_eventName.getValue());
           }
       });*/


        return cardView;
    }
}
