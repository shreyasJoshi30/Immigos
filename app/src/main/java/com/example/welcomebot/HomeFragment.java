package com.example.welcomebot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class HomeFragment extends Fragment {


    //DatabaseReference databaseReference;
    View rootView;
    LinearLayout.LayoutParams layoutParams;
    LinearLayout.LayoutParams cardParams;
    ScrollView.LayoutParams scrollParams;
    ScrollView scrollView;
    LinearLayout homeLayout;
    LinearLayout containerCardLayout;
    String newsAPIKey;
    String eventBriteAPI_key;
    String country = "au";
    String newsCategory = "";

    //used for reinflating view
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    String clickable = "Tap the card to view more...";
    String clickable_chinese = "点按该卡可查看更多...";
    Button btn_business;
    Button btn_entertainment;
    Button btn_health ;
    Button btn_science;
    Button btn_sports;
    Button btn_technology;

    String defaultLanguage = "NA";

    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;


    String tr_title;
    String tr_desc;
    int cardlength = 0;
    int currentCard = 0;
    BottomNavigationView bottomNavigationView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mInflater = inflater;
        mContainer = container;
        rootView = inflater.inflate(R.layout.fragment_home,container,false);
        setHasOptionsMenu(true);
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);


        eventBriteAPI_key = getActivity().getResources().getString(R.string.eventBriteAPI_key);
        newsAPIKey = getActivity().getResources().getString(R.string.newsAPI_key);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");


        if(defaultLanguage.equals("cn")){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("新闻");

            enableBotttomNav(false);
        }
        else{
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News");

        }




        getNews(defaultLanguage,country,newsCategory);


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


        //--------------------Button onclick listeners ---------------------------


        btn_business = (Button) rootView.findViewById(R.id.btn_business);
        btn_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeLayout.removeAllViews();
                getNews(defaultLanguage,country,"business");
            }
        });


        btn_entertainment = (Button) rootView.findViewById(R.id.btn_entertainment);
        btn_entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeLayout.removeAllViews();
                getNews(defaultLanguage,country,"entertainment");
            }
        });


        btn_health = (Button) rootView.findViewById(R.id.btn_health);
        btn_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeLayout.removeAllViews();
                getNews(defaultLanguage,country,"health");
            }
        });

       btn_science = (Button) rootView.findViewById(R.id.btn_science);
        btn_science.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeLayout.removeAllViews();
                getNews(defaultLanguage,country,"science");
            }
        });
        btn_sports = (Button) rootView.findViewById(R.id.btn_sports);
        btn_sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeLayout.removeAllViews();
                getNews(defaultLanguage,country,"sports");
            }
        });
        btn_technology = (Button) rootView.findViewById(R.id.btn_technology);
        btn_technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeLayout.removeAllViews();
                getNews(defaultLanguage,country,"technology");
            }
        });


        if(defaultLanguage.equals("cn")){

            changeLabelToChinese();
        }else{
            changeLabelToEnglish();
        }



        return rootView;
    }

    public void enableBotttomNav(Boolean value){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigationid);
        bottomNavigationView.getMenu().getItem(0).setEnabled(value);
        bottomNavigationView.getMenu().getItem(1).setEnabled(value);
        bottomNavigationView.getMenu().getItem(2).setEnabled(value);
        bottomNavigationView.getMenu().getItem(3).setEnabled(value);
        bottomNavigationView.getMenu().getItem(4).setEnabled(value);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.custom_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item  = menu.findItem(R.id.app_bar_search);
        item.setVisible(false);

    }

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

            homeLayout.removeAllViews();
            country = "au";
            newsCategory="";
            changeLabelToChinese();
            enableBotttomNav(false);
            getNews(defaultLanguage,country,newsCategory);
        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            defaultLanguage = "au";

            country = "au";
            newsCategory="";
            homeLayout.removeAllViews();
            changeLabelToEnglish();
            getNews(defaultLanguage,country,newsCategory);
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


    /**
     * methods to toggle label changes
     */

    public void changeLabelToChinese(){
        btn_business.setText("商业");
        btn_entertainment.setText("娱乐");
        btn_health.setText("健康");
        btn_science.setText("科学");
        btn_sports.setText("体育");
        btn_technology.setText("技术");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("新闻");

        bottomNavigationView.getMenu().getItem(0).setTitle("家园");
        bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.tr_icon_news));
        bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.tr_icon_chat));
        bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.tr_icon_events));
        bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));



    }

    public void changeLabelToEnglish(){
        btn_business.setText("Business");
        btn_entertainment.setText("Entertainment");
        btn_health.setText("Health");
        btn_science.setText("Science");
        btn_sports.setText("Sports");
        btn_technology.setText("Technology");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("News");

        bottomNavigationView.getMenu().getItem(0).setTitle("Home");
        bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.icon_news));
        bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.icon_chat));
        bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.icon_events));
        bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.icon_explore));


    }

   /* public void run () throws IOException{

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://www.eventbriteapi.com/v3/events/49216045517/")
                .method("GET", null)//
                .addHeader("Authorization", "Bearer "+eventBriteAPI_key)
                .addHeader("content_type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {

            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();
                //TextView txtString = getActivity().findViewById(R.id.responseID);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                     //   txtString.setText(myResponse);
                    }
                });

            }
        });
    }*/


    /**
     * getnews fetches news with respect to country and category
     * @param country
     * @param newsCategory
     */
    public void getNews(String newsLanguage,String country, String newsCategory){


        enableBotttomNav(false);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        StringBuilder requestURL = new StringBuilder("http://newsapi.org/v2/top-headlines?");
        requestURL.append("country="+country);
        if(!newsCategory.equals("")){
            requestURL.append("&category="+newsCategory);
        }
        requestURL.append("&apiKey="+newsAPIKey);
        Request request = new Request.Builder()
                .url(requestURL.toString())
                .method("GET", null)
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
                        // Code here will run in UI thread


                        try {
                            JSONObject newsObject = new JSONObject(newsResponse);
                            JSONArray Jarray = newsObject.getJSONArray("articles");
                            homeLayout = (LinearLayout) rootView.findViewById(R.id.homeID);

                            scrollParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
                            scrollView = new ScrollView(getActivity());
                            scrollView.setLayoutParams(scrollParams);
                            homeLayout.addView(scrollView);
                            containerCardLayout  = new LinearLayout(getActivity());
                            containerCardLayout.setLayoutParams(scrollParams);
                            containerCardLayout.setOrientation(LinearLayout.VERTICAL);
                            scrollView.addView(containerCardLayout);
                            cardlength = Jarray.length();
                            for (int i = 0; i < Jarray.length(); i++) {
                                currentCard = i+1;
                                JSONObject object = Jarray.getJSONObject(i);

                                String title = object.getString("title");
                                String desc = object.getString("description");
                                String imageurl = object.getString("urlToImage");
                                String content = object.getString("content");
                                String newsUrl = object.getString("url");
                                //ImageView newsImage =  new ImageView(getActivity());

                                //create multiple cards....

                                layoutParams.setMargins(30, 30, 30, 30);

                                if (defaultLanguage.equals("au")) {

                                    MaterialCardView cardView = createCard(title, desc, content, newsUrl, imageurl);
                                    containerCardLayout.addView(cardView, layoutParams);
                                }
                                else{

                                    englishChineseTranslator.translate(title).addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String sr) {

                                            //tr_title = sr;

                                            englishChineseTranslator.translate(desc).addOnSuccessListener(new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String s) {

                                                    tr_title = sr;
                                                    tr_desc = s;
                                                    MaterialCardView cardView = createCard(tr_title, tr_desc, content, newsUrl, imageurl);
                                                    containerCardLayout.addView(cardView, layoutParams);
                                                }
                                            });

                                        }
                                    });



                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //   txtString.setText(myResponse);
                    }
                });

            }
        });



    }


    /**
     * The method is used to create dynamic material cardviews for displaying news
     * @param title
     * @param desc
     * @param content
     * @param newsUrl
     * @param imageurl
     * @return
     */
    public MaterialCardView  createCard(String title, String desc, String content, String newsUrl, String imageurl){



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
        TextView newsTitle = new TextView(getActivity());
        newsTitle.setLayoutParams(layoutParams);
        newsTitle.setText(title);
        newsTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        newsTitle.setTextColor(Color.BLACK);

        // Textview that will contain the short description
        TextView tv = new TextView(getActivity());
        tv.setLayoutParams(layoutParams);
        tv.setText(desc);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setTextColor(Color.BLACK);

        ImageView newsImage = new ImageView(getActivity());
        newsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(getActivity()).load(imageurl).into(newsImage);
        //newsImage.setImageResource(R.drawable.ic_whatshot_black_24dp);

        // Textview that will contain the short content

        TextView tvContent = new TextView(getActivity());
        tvContent.setLayoutParams(layoutParams);

        if(country.equals("au") || country.equals("") ){
            tvContent.setText(clickable);
        }
        else{
            tvContent.setText(clickable_chinese);
        }

        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        tvContent.setTextColor(Color.BLACK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            newsTitle.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            tv.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            tvContent.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String url = "https://www.youtube.com";
                Uri uriUrl = Uri.parse(newsUrl);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        cardLayout.addView(newsImage);
        //scrollView.setLayoutParams(layoutParams);
        //scrollView.addView(cardLayout);
        cardLayout.addView(newsTitle);
        cardLayout.addView(tv);
        cardLayout.addView(tvContent);
        cardView.addView(cardLayout);

        if(currentCard == cardlength){

            System.out.println(currentCard);
            enableBotttomNav(true);
        }

        return cardView;

    }


}
