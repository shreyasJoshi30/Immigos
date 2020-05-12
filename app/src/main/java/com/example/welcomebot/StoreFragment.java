package com.example.welcomebot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootview;
    String defaultLanguage = "NA";

    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;

    String translated_text;

    public StoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootview = inflater.inflate(R.layout.fragment_store, container, false);
        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        if (defaultLanguage.equals("au")) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Asian Stores");
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("亚洲商店");
        }

        Button btn_search = rootview.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://www.google.com/search?source=hp&ei=0PWyXuqtGriL4-EPirKlsAc&q=asian+stores+near+me&oq=asian+stores+near+me&gs_lcp=CgZwc3ktYWIQAzICCAAyAggAMgIIADICCAAyBggAEBYQHjIGCAAQFhAeMgYIABAWEB4yBggAEBYQHjIGCAAQFhAeMgYIABAWEB46BQgAEIMBOgQIABAKUIkNWMggYLohaABwAHgAgAH6AYgBjxqSAQYwLjE2LjSYAQCgAQGqAQdnd3Mtd2l6sAEA&sclient=psy-ab&ved=0ahUKEwjq1c6645_pAhW4xTgGHQpZCXYQ4dUDCAk&uact=5"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

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


        TextView id_store = rootview.findViewById(R.id.id_store);
        if (defaultLanguage.equals("cn")) {
            translate();
        }
        else{
            String text = getActivity().getResources().getString(R.string.storeDesc);
            id_store.setText(text);
        }

        return rootview;
    }


    public void translate(){

        TextView id_store = rootview.findViewById(R.id.id_store);
        Button btn_search = rootview.findViewById(R.id.btn_search);
        englishChineseTranslator.translate(getActivity().getResources().getString(R.string.storeDesc)).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView id_stores = rootview.findViewById(R.id.id_store);
                        id_stores.setText(s);
                        btn_search.setText("寻找我附近的亚洲专卖店");
                    }
                });

            }
        });


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
            translate();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("亚洲商店");
        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            defaultLanguage = "au";
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Asian Stores");
            String text = getActivity().getResources().getString(R.string.storeDesc);
            TextView id_store = rootview.findViewById(R.id.id_store);
            id_store.setText(text);
            Button btn = rootview.findViewById(R.id.btn_search);
            btn.setText("Find Asian Stores Near Me");
            //changeLabelToEnglish();
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
}
