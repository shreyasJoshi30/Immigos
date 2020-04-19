package com.example.welcomebot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;





import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home,container,false);

        /*containerCardLayout  = new LinearLayout(getActivity());
        containerCardLayout.setLayoutParams(scrollParams);
        scrollParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(scrollParams);
        homeLayout.addView(scrollView);
*/
        eventBriteAPI_key = getActivity().getResources().getString(R.string.eventBriteAPI_key);
        newsAPIKey = getActivity().getResources().getString(R.string.newsAPI_key);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getNews();

       return rootView;
    }




    public void run () throws IOException{

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


    }

    public void getNews(){



        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://newsapi.org/v2/top-headlines?country=au&apiKey="+newsAPIKey)
                .method("GET", null)
               // .addHeader("content_type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {

            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String newsResponse = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

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



                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject object = Jarray.getJSONObject(i);

                                String title  = object.getString("title");
                                String desc = object.getString("description");
                                String url = object.getString("urlToImage");
                                String content = object.getString("content");
                                //ImageView newsImage =  new ImageView(getActivity());

                                /*URL newurl = new URL(url);
                                Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                                newsImage.setImageBitmap(mIcon_val);*/
                                //create multiple cards....

                                layoutParams.setMargins(30,30,30,30);

                                MaterialCardView cardView = createCard(title,desc,content);
                                containerCardLayout.addView(cardView,layoutParams);


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




    public MaterialCardView  createCard(String title, String desc,String content){
        //,String imageURL,String content

        LinearLayout cardLayout = new LinearLayout(getActivity());

        cardLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,LinearLayout.LayoutParams.MATCH_PARENT);
       // LL.setWeightSum(6f);
        cardLayout.setLayoutParams(LLParams);
        //ImageView ladder = new ImageView(this);
        //ladder.setImageResource(R.drawable.ic_launcher);



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
        newsTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        newsTitle.setTextColor(Color.BLACK);


        // Textview that will contain the short description
        TextView tv = new TextView(getActivity());
        tv.setLayoutParams(layoutParams);
        tv.setText(desc);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setTextColor(Color.BLACK);

        ImageView newsImage = new ImageView(getActivity());
        newsImage.setLayoutParams(layoutParams);
        newsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        newsImage.setImageResource(R.drawable.ic_whatshot_black_24dp);



        // Textview that will contain the short description
        TextView tvContent = new TextView(getActivity());
        tvContent.setLayoutParams(layoutParams);
        tvContent.setText(content);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        tvContent.setTextColor(Color.BLACK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            newsTitle.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            tv.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            tvContent.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        //cardLayout.addView(newsImage);
        //scrollView.setLayoutParams(layoutParams);
        //scrollView.addView(cardLayout);
        cardLayout.addView(newsTitle);
        cardLayout.addView(tv);
        cardLayout.addView(tvContent);
        cardView.addView(cardLayout);


        return cardView;

    }


}
