package com.example.welcomebot;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class EventsFragment extends Fragment {

    View rootivew;
    LinearLayout.LayoutParams layoutParams;
    LinearLayout.LayoutParams cardParams;
    ScrollView.LayoutParams scrollParams;
    ScrollView scrollView;
    LinearLayout homeLayout;
    LinearLayout containerCardLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootivew =inflater.inflate(R.layout.fragment_events,container,false);

        homeLayout = (LinearLayout) rootivew.findViewById(R.id.v_events);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        scrollParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(scrollParams);
        homeLayout.addView(scrollView);
        containerCardLayout  = new LinearLayout(getActivity());
        containerCardLayout.setLayoutParams(scrollParams);
        containerCardLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(containerCardLayout);

        String urlArray[] = new String[5];
        String contentArray[] = new String[5];
        String titleArray[] = new String[5];


        titleArray[0] = "The Australian Ballet's free cinema-quality digital season brings their full-length performances to Australians at home";
        contentArray[0]="The Australian Ballet's At Home with Ballet TV digital season beams their premium-quality productions to your couch for free, with productions of The Sleeping Beauty, Cinderella Romeo & Juliet.";
        urlArray[0]= "https://whatson.melbourne.vic.gov.au/Whatson/ArtsandCulture/Dance/PublishingImages/1Le3b52575-a795-45bf-9405-18e9c870655e.jpg";

        titleArray[1] = "Kaya Health Club's doors are closed right now, but their free online workouts will keep you moving during this challenging time. People need mind and body programmes more than ever right now.";
        contentArray[1]="Try a five-minute opening stretch before diving into the 20-minute workout, or ease into Pilates with a five-minute warm-up and 15-minute mat Pilates session or the Pilates mat glute-burner series.";
        urlArray[1]= "https://whatson.melbourne.vic.gov.au/Whatson/LearnandSee/WorkshopsandSeminars/PublishingImages/1L588f7ee7-70c5-440f-a41e-066a4ee2e45d.jpg";

        titleArray[2] = "Enjoy an evening of live entertainment with one of Australia's best comedy clubs, Kings of Comedy, and get a backstage pass to the club's brand-new format.";
        contentArray[2]="Previous headline acts have included Dave O'Neil, Dave Hughes, Lehmo, Bob Franklin, Denise Scott, Fiona O'Loughlin, Tony Martin and Richard Stubbs, so join in and make history together – this is going to be a ripper!";
        urlArray[2]= "https://whatson.melbourne.vic.gov.au/Whatson/ArtsandCulture/Comedy/PublishingImages/1L4c7cb3c4-c007-4000-bf20-1ca29b5d9d09.jpg";

        titleArray[3] = "Zoos Victoria has launched a live streaming service so animal lovers can stay connected with some of their favourite animals from home, work or anywhere else they want";
        contentArray[3]="Melbourne Zoo is live streaming from the den of its adorable snow leopard cubs, who were born on Australia Day and are increasingly active and exploring their world close to mum Miska. The Zoo's penguins are also starring in their very own live stream, as are Melbourne Zoo's graceful giraffes, including recent arrival Klintun. Lion lovers can also keep an eye on Werribee Open Range Zoo's lion pride.";
        urlArray[3]= "https://whatson.melbourne.vic.gov.au/Whatson/Community/Community/PublishingImages/1L1829f6f8-1cc5-4173-b5fc-fef3de2b437a.jpg";


        titleArray[4] = "Laneway Learning offer fun and cheap classes in almost anything and everything – now online.";
        contentArray[4]="Laneway Learning hosts informal, accessible classes in a diverse range of topics. No matter that the venues are closed due to COVID-19 as they've introduced new online classes you can do virtually.\n";
        urlArray[4]= "https://whatson.melbourne.vic.gov.au/Whatson/LearnandSee/WorkshopsandSeminars/PublishingImages/1L097ff69f-1572-432c-aafe-c5c93afec0cd.jpg";

    /*    titleArray[0] = "";
        contentArray[0]="";
        urlArray[0]= "";

*/



        for(int i =0;i<5;i++){


            layoutParams.setMargins(30,30,30,30);
            MaterialCardView cardView = createCard(titleArray[i],contentArray[i],urlArray[i]);
            containerCardLayout.addView(cardView,layoutParams);


            //cardRoot.addView(cardView);
        }


        return rootivew;
    }


    public MaterialCardView createCard(String title,String content, String imageURL){


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
        tv.setText(content);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tv.setTextColor(Color.BLACK);

        ImageView newsImage = new ImageView(getActivity());
        //newsImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(getActivity()).load(imageURL).into(newsImage);
        //newsImage.setImageResource(R.drawable.home_image);

        // Textview that will contain the short content

        /*TextView tvContent = new TextView(getActivity());
        tvContent.setLayoutParams(layoutParams);

            tvContent.setText("owinfwordcn vr ght onovnorendworenbvwefortnfdqpweirnbpweg rbjrenbflrjogrklnforipogjklf;dp[orjghklnv;mdfkepojpgrklnfvdm;feojpgrkldbfnv;mfeojphglrjbfm, v ");

        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        tvContent.setTextColor(Color.BLACK);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            newsTitle.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            tv.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            //tvContent.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        cardLayout.addView(newsImage);
        //scrollView.setLayoutParams(layoutParams);
        //scrollView.addView(cardLayout);
        cardLayout.addView(newsTitle);
        cardLayout.addView(tv);
        //cardLayout.addView(tvContent);
        cardView.addView(cardLayout);
        return cardView;
    }
}
