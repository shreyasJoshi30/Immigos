package com.example.welcomebot;

import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LandingPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LandingPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    GridLayout mainGrid;


    public LandingPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LandingPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LandingPageFragment newInstance(String param1, String param2) {
        LandingPageFragment fragment = new LandingPageFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_landing_page, container, false);



        mainGrid = (GridLayout) rootView.findViewById(R.id.mainGrid);
        setSingleEvent(mainGrid);
        TextView gridlayout = rootView.findViewById(R.id.textGrid);

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int timeOfDay = cal.get(Calendar.HOUR_OF_DAY);

        /*TextView greeting =  rootView.findViewById(R.id.tv_greeting);
        TextView introTV = rootView.findViewById(R.id.id_intro);

        StringBuilder introText =  new StringBuilder("");
        introText.append("The application is designed keeping in mind the users who want to get out of self isolation and loneliess.It will recommend various activities based on user interests and track their emotions to show a progress and little victories to motivate the user.");
        introText.append("\n\n The application users can: \n\n1) Get a glimpse of latest news of Australia and Home country China \n\n2) Chat with our smart bot to ask about basic things \n\n3) Explore events nearby \n\n4) Explore nearby places around Australia ");
        introTV.setText(introText.toString());*/

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





        // Inflate the layout for this fragment
        return rootView;
    }


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

    private void setSingleEvent(GridLayout mainGrid) {
        //Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   /* Intent intent = new Intent(getActivity(), EventsFragment.class);
                    intent.putExtra("info","This is activity from card item index  "+finalI);
                    startActivity(intent);
*/
                }
            });
        }
    }
}
