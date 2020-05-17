package com.example.welcomebot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {


    private ViewPager screenPager;
    AboutViewPageAdapter aboutViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    Button btnGetStarted;
    Animation btnAnim ;
    TextView tvSkip;

    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
                //Toast.makeText(getApplicationContext(), "English-Chinese translator downloaded", Toast.LENGTH_LONG).show();
            }
        });
        //---------------------english chinese translator-------------------


        // make the activity on full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // when this activity is about to be launch we need to check if its openened before or not

        if (restorePrefData()) {

            Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class );
            startActivity(mainActivity);
            finish();


        }

        setContentView(R.layout.activity_about);

        getSupportActionBar().hide();

        // ini views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        // fill list screen
        //------------------------------------------------------------------------------------------------------//

        //These are the screens displayed on onboarding page

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Welcome To Immigos\n  欢迎来到伊米戈斯\n","The app aims to reduce social isolation of senior Chinese immigrants with useful resources related to health, events, news and more.\n\n该应用程序旨在通过与健康，事件，新闻等相关的有用资源来减少年老中国移民的社会隔离。",R.drawable.immigos_head));
        mList.add(new ScreenItem("News Feed | 新闻提要","Stay updated with current affairs! Have a glance of latest news related to politics, health care, business and much more.\n\n保持时事更新！浏览与政治，医疗保健，商业等相关的最新新闻。",R.drawable.img_news_app));
        mList.add(new ScreenItem("Talk With Us | 与我们交谈","Engage with our chatbot which will help you in answering basic questions and indulge in short conversations.\n\n与我们的聊天机器人互动，它将帮助您回答基本问题并沉迷于简短的对话中。",R.drawable.img_bot_app));
        mList.add(new ScreenItem("Local Events | 当地活动","Browse a list of events which are planned around your neighborhood. Concert, stand-ups comedies, cooking classes and more…\n\n浏览您附近计划的活动列表。音乐会，单口喜剧，烹饪课等等",R.drawable.img_events));
        mList.add(new ScreenItem("Explore Around! | 探索周围！","Find the local markets, restaurants, art centers and libraries based on your location and start exploring Aussieland.\n\n根据您的位置找到当地市场，餐馆，艺术中心和图书馆，然后开始探索澳大利亚。",R.drawable.image_exploremaps));

        //------------------------------------------------------------------------------------------------------//

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        aboutViewPagerAdapter = new AboutViewPageAdapter(this,mList);
        screenPager.setAdapter(aboutViewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner
        //------------------------------------------------------------------------------------------------------//

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {

                    position++;
                    screenPager.setCurrentItem(position);


                }

                if (position == mList.size()-1) { // when we rech to the last screen

                    // TODO : show the GETSTARTED Button and hide the indicator and the next button

                    loaddLastScreen();


                }



            }
        });

        //------------------------------------------------------------------------------------------------------//

        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {

                    loaddLastScreen();

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //------------------------------------------------------------------------------------------------------//


        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //open main activity

                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                finish();



            }
        });
        //------------------------------------------------------------------------------------------------------//

        // skip button click listener

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });


    }
    //------------------------------------------------------------------------------------------------------//

    private boolean restorePrefData() {

        String screen = getIntent().getStringExtra("SCREEN");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);

        if(screen!=null){

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isIntroOpnend",false);
            editor.commit();
            pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
            isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",true);

        }
        return  isIntroActivityOpnendBefore;

    }

    //------------------------------------------------------------------------------------------------------//

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.putString("isChinese","au");
        editor.commit();


    }

    //------------------------------------------------------------------------------------------------------//

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);



    }

    //------------------------------------------------------------------------------------------------------//

}
