package com.example.welcomebot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import static android.content.Context.MODE_PRIVATE;


/**
 * A static fragment which dislays information about mental health and well being
 */
public class DetailsFragment extends Fragment {


    View rootview;
    String defaultLanguage = "NA";
    BottomNavigationView bottomNavigationView;

    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;
    //------------------------------------------------------------------------------------------------------//


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_details, container, false);
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);
        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

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

       translateContent();

    /*    Bundle bundle = this.getArguments();
        int i = 10;
        if(bundle != null){

            i = bundle.getInt("cardNo");
        }*/


        return rootview;
    }

    //------------------------------------------------------------------------------------------------------//


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
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("心灵与健康");
            //changeLabelToChinese();
            translateContent();
        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mind & Health");
            defaultLanguage = "au";
            //changeLabelToEnglish();
            translateContent();
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


    /**
     * Method to tranlsate the content based on user's language prefernece
     */
    public void translateContent(){

        if(defaultLanguage.equals("cn")){




            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("心灵与健康");


            bottomNavigationView.getMenu().getItem(0).setTitle("家园");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.tr_icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.tr_icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.tr_icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));

            translate();

        }
        else{
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mind & Health");

            bottomNavigationView.getMenu().getItem(0).setTitle("Home");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.icon_explore));

            TextView id_intro = rootview.findViewById(R.id.id_intro);
            TextView id_body = rootview.findViewById(R.id.id_body);

            id_intro.setText(getActivity().getResources().getString(R.string.mentalHealth));
            id_body.setText(getActivity().getResources().getString(R.string.mentalProblems));



        }




    }
    //------------------------------------------------------------------------------------------------------//


    public void translate(){

        englishChineseTranslator.translate(getActivity().getResources().getString(R.string.mentalHealth)).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView id_intro = rootview.findViewById(R.id.id_intro);
                        id_intro.setText(s);
                    }
                });

            }
        });       englishChineseTranslator.translate(getActivity().getResources().getString(R.string.mentalProblems)).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView id_body = rootview.findViewById(R.id.id_body);
                        id_body.setText(s);
                    }
                });

            }
        });


    }
    //------------------------------------------------------------------------------------------------------//


}
