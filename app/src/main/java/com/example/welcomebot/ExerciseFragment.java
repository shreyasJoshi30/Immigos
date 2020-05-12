package com.example.welcomebot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View rootview;
    Button btn_searchLocation;
    EditText postcode;
    String defaultLanguage = "NA";

    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;
    BottomNavigationView bottomNavigationView;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExerciseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExerciseFragment newInstance(String param1, String param2) {
        ExerciseFragment fragment = new ExerciseFragment();
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
        rootview = inflater.inflate(R.layout.fragment_exercise, container, false);
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);

        ArrayList<String> category = new ArrayList<String>();
        category.add("-- Select your category --");
        category.add("Cricket");
        category.add("Lawn Balls");
        setHasOptionsMenu(true);

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");
        Spinner category_spinner = (Spinner)rootview.findViewById(R.id.spinner_category);

        ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, category);
        category_spinner.setAdapter(arrayAdapter);

        postcode = (EditText)rootview.findViewById(R.id.postcode);
        btn_searchLocation = (Button)rootview.findViewById(R.id.btn_searchLocation);
        btn_searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postcode.getText().length()==4){

                    Bundle bundle = new Bundle();
                   String sportsCat =  category_spinner.getSelectedItem().toString();
                    bundle.putInt("postcode",Integer.parseInt(postcode.getText().toString()));
                    bundle.putString("category",sportsCat);

                    //Toast.makeText(getActivity(), postcode.getText().toString(), Toast.LENGTH_LONG).show();
                    ExploreFragment exploreFragment = new ExploreFragment();
                    exploreFragment.setArguments(bundle);


                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, exploreFragment)
                            .commit();



                }
                else{
                    Toast.makeText(getActivity(), "Please enter a 4 digit valid postcode", Toast.LENGTH_LONG).show();
                }
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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        if (defaultLanguage.equals("au")) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Exercise");
        } else {
            translate();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("行使");
        }

        return rootview;
    }


    public void translate() {

        if (defaultLanguage.equals("cn")) {

            bottomNavigationView.getMenu().getItem(0).setTitle("家园");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.tr_icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.tr_icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.tr_icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));


            Button btn_searchLocation = rootview.findViewById(R.id.btn_searchLocation);
        englishChineseTranslator.translate(getActivity().getResources().getString(R.string.exerciseDesc)).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView id_exercise = rootview.findViewById(R.id.id_exercise);
                        id_exercise.setText(s);
                        btn_searchLocation.setText("查找我附近的位置");
                    }
                });

            }
        });
    }else{
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
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("行使");
            //changeLabelToChinese();
            translate();
        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Exercise");
            defaultLanguage = "au";

            String text = getActivity().getResources().getString(R.string.exerciseDesc);
            TextView id_exercise = rootview.findViewById(R.id.id_exercise);
            id_exercise.setText(text);
            Button btn = rootview.findViewById(R.id.btn_searchLocation);
            btn.setText("Find Locations Near Me");
            translate();

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
