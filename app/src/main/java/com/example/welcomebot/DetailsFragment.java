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
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootview;
    String defaultLanguage = "NA";

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        rootview = inflater.inflate(R.layout.fragment_details, container, false);
        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        if (defaultLanguage.equals("au")) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mental Health And Wellbeing");
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("心理健康与幸福");
        }


        Bundle bundle = this.getArguments();
        int i = 10;
        if(bundle != null){

            i = bundle.getInt("cardNo");


        }


        return rootview;
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
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("心理健康与幸福");
            //changeLabelToChinese();
        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mental Health And Wellbeing");
            defaultLanguage = "au";
            //changeLabelToEnglish();
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


}
