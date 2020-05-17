package com.example.welcomebot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


/**
 * This is the landingpage fragment which hangles the card grid design and manages navigation between avtivities oncard selection
 */
public class LandingPageFragment extends Fragment {

    private String mParam1;
    private String mParam2;
    GridLayout mainGrid;
    BottomNavigationView bottomNavigationView;
    String defaultLanguage = "NA";
    View rootView;
    int timeOfDay = 0;
    TextView gridlayout;


    //------------------------------------------------------------------------------------------------------//


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_landing_page, container, false);
        bottomNavigationView  = getActivity().findViewById(R.id.bottom_navigationid);
        mainGrid = (GridLayout) rootView.findViewById(R.id.mainGrid);
        setSingleEvent(mainGrid);
        gridlayout = rootView.findViewById(R.id.textGrid);

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        defaultLanguage =pref.getString("isChinese","au");

        setHasOptionsMenu(true);

        if (defaultLanguage.equals("au")) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Immigos");
        } else {
            translateLabels();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("伊米戈斯");
        }



        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.mipmap.launcher_icon);

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        timeOfDay = cal.get(Calendar.HOUR_OF_DAY);
        translateLabels();


        Button btn_breathe = rootView.findViewById(R.id.btn_breathe);
        btn_breathe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the breathing activity
                Intent mainActivity = new Intent(getActivity(),BreathingActivity.class );
                startActivity(mainActivity);

            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }
    //------------------------------------------------------------------------------------------------------//

    /**
     * Method to translate the content based on language preference
     */
    void translateLabels(){

        Button btn_breathe = rootView.findViewById(R.id.btn_breathe);
        TextView tv_news = rootView.findViewById(R.id.tv_news);
        TextView tv_events = rootView.findViewById(R.id.tv_events);
        TextView tv_exercise = rootView.findViewById(R.id.tv_exercise);
        TextView tv_hospitals = rootView.findViewById(R.id.tv_hospitals);
        TextView tv_stores = rootView.findViewById(R.id.tv_stores);
        TextView tv_health = rootView.findViewById(R.id.tv_health);
        TextView tv_chat = rootView.findViewById(R.id.tv_chat);
        TextView tv_explore = rootView.findViewById(R.id.tv_explore);


        if(defaultLanguage.equals("cn")){

            bottomNavigationView.getMenu().getItem(0).setTitle("家园");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.tr_icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.tr_icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.tr_icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.tr_icon_explore));


            if(timeOfDay >= 0 && timeOfDay < 12){
                gridlayout.setText("早上好!");
            }else if(timeOfDay >= 12 && timeOfDay < 16){
                gridlayout.setText("下午好");
            }else if(timeOfDay >= 16 && timeOfDay < 21){
                gridlayout.setText("晚上好");
            }else if(timeOfDay >= 21 && timeOfDay < 24){
                gridlayout.setText("问候!");
            }else{
                gridlayout.setText("问候!");
            }

            tv_news.setText(getActivity().getResources().getString(R.string.tr_icon_news));
            tv_events.setText(getActivity().getResources().getString(R.string.tr_icon_events));
            tv_exercise.setText(getActivity().getResources().getString(R.string.tr_icon_exercise));
            tv_hospitals.setText(getActivity().getResources().getString(R.string.tr_icon_hospitals));
            tv_stores.setText(getActivity().getResources().getString(R.string.tr_icon_stores));
            tv_health.setText(getActivity().getResources().getString(R.string.tr_icon_health));
            tv_chat.setText(getActivity().getResources().getString(R.string.tr_icon_translate));
            tv_explore.setText(getActivity().getResources().getString(R.string.tr_icon_explore));
            btn_breathe.setText(getActivity().getResources().getString(R.string.tr_breathing));

        }
        else{

            bottomNavigationView.getMenu().getItem(0).setTitle("Home");
            bottomNavigationView.getMenu().getItem(1).setTitle(getActivity().getResources().getString(R.string.icon_news));
            bottomNavigationView.getMenu().getItem(2).setTitle(getActivity().getResources().getString(R.string.icon_chat));
            bottomNavigationView.getMenu().getItem(3).setTitle(getActivity().getResources().getString(R.string.icon_events));
            bottomNavigationView.getMenu().getItem(4).setTitle(getActivity().getResources().getString(R.string.icon_explore));


            if(timeOfDay >= 0 && timeOfDay < 12){
                gridlayout.setText("Good Morning!");
            }else if(timeOfDay >= 12 && timeOfDay < 16){
                gridlayout.setText("Good Afternoon");
            }else if(timeOfDay >= 16 && timeOfDay < 21){
                gridlayout.setText("Good Evening");
            }else if(timeOfDay >= 21 && timeOfDay < 24){
                gridlayout.setText("Greetings!");
            }else{
                gridlayout.setText("Greetings!");
            }

            tv_news.setText(getActivity().getResources().getString(R.string.icon_news));
            tv_events.setText(getActivity().getResources().getString(R.string.icon_events));
            tv_exercise.setText(getActivity().getResources().getString(R.string.icon_exercise));
            tv_hospitals.setText(getActivity().getResources().getString(R.string.icon_hospitals));
            tv_stores.setText(getActivity().getResources().getString(R.string.icon_stores));
            tv_health.setText(getActivity().getResources().getString(R.string.icon_health));
            tv_chat.setText(getActivity().getResources().getString(R.string.icon_translate));
            tv_explore.setText(getActivity().getResources().getString(R.string.icon_explore));
            btn_breathe.setText(getActivity().getResources().getString(R.string.breathing));

        }

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
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("伊米戈斯");
            //changeLabelToChinese();
            translateLabels();
        }

        if(id == R.id.app_bar_australia){

            SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("isChinese","au");
            editor.commit();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Immigos");
            defaultLanguage = "au";
            translateLabels();
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
    //------------------------------------------------------------------------------------------------------//

    /**
     * manages the card onclick
     * @param mainGrid
     */
    private void setToggleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            final CardView cardView = (CardView) mainGrid.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cardView.getCardBackgroundColor().getDefaultColor() == -1) {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FF6F00"));
                        Toast.makeText(getActivity(), "State : True", Toast.LENGTH_SHORT).show();

                    } else {
                        //Change background color
                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                        Toast.makeText(getActivity(), "State : False", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //------------------------------------------------------------------------------------------------------//

    /**
     * method is used to handle navigation based on card press
     * @param mainGrid
     */
    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   // System.out.println(finalI);
                    Fragment selectedFragment = null;

                    cardView.setCardBackgroundColor(Color.GRAY);

                    switch (finalI){
                        case 0:
                            selectedFragment = new HomeFragment();
                            bottomNavigationView.setSelectedItemId(R.id.nav_home);
                            break;
                        case 1:
                            selectedFragment = new EventsFragment();
                            bottomNavigationView.setSelectedItemId(R.id.nav_events);
                            break;
                        case 2:
                            selectedFragment = new ExerciseFragment();
                            break;
                        case 3:
                            selectedFragment =  new HospitalFragment();
                            break;
                        case 4:
                            selectedFragment = new StoreFragment();
                            break;
                        case 5:
                            selectedFragment =  new DetailsFragment();
                            break;
                        case 6:
                            selectedFragment = new TranslateFragment();
                            bottomNavigationView.setSelectedItemId(R.id.nav_chat);
                            break;
                        case 7:
                            selectedFragment = new ExploreFragment();
                            bottomNavigationView.setSelectedItemId(R.id.nav_explore);
                            break;

                    }

                    Bundle bundle = new Bundle();
                    bundle.putInt("cardNo",finalI); // Put anything what you want

                    DetailsFragment detailsFragment = new DetailsFragment();
                    detailsFragment.setArguments(bundle);


                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).addToBackStack(null)
                            .commit();

                   /* Intent intent = new Intent(getfr(), DetailsFragment.class);
                    intent.putExtra("info","This is activity from card item index  "+finalI);
                    startActivity(intent);
*/
                }
            });
        }
    }
    //------------------------------------------------------------------------------------------------------//

}
