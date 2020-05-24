package com.example.welcomebot;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The adapter used for displaying results from google Places API json in cardviews
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<CardLayoutAdapter.ViewHolder> {


    private LayoutInflater layoutInflater;
    private List<PlacesListBean> data;
    private String languageCode;
    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;
    Context activityContext;


    //------------------------------------------------------------------------------------------------------//

    CardLayoutAdapter(Context context,List<PlacesListBean> data,String languageCode){

        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.languageCode = languageCode;
        this.activityContext = context;

    }
    //------------------------------------------------------------------------------------------------------//
//handling the translation for cardviews
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_card_layout,parent,false);

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


        return new ViewHolder(view);
    }

    //------------------------------------------------------------------------------------------------------//

    /**
     * overridden method to handle each carview onclick and setting the data into cardview
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String name = data.get(position).getName();
        String address = data.get(position).getAddress();
        Double rating = data.get(position).getRating();
        float distance = data.get(position).getDistance();
        String total_ratings = data.get(position).getTotal_user_ratings();
        Location location = data.get(position).getLocation();

        holder.h_location.setText(String.valueOf(location.getLatitude())+","+ String.valueOf(location.getLongitude()));
        holder.h_address.setText(address);
        holder.h_rating.setRating(rating.floatValue());
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.UP);
        holder.h_distance.setText(df.format(distance) + " km");
        holder.h_total_user_ratings.setText("("+total_ratings+")");

        if(languageCode.equals("au")){
            holder.h_name.setText(name);
            /*holder.h_address.setText(address);
            holder.h_rating.setRating(rating.floatValue());
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.UP);
            holder.h_distance.setText(df.format(distance) + " km");
            holder.h_total_user_ratings.setText("("+total_ratings+")");*/
        }
        else{

            englishChineseTranslator.translate(name).addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {

                    holder.h_name.setText(s);



                }
            });

        }


    }

    //------------------------------------------------------------------------------------------------------//

    @Override
    public int getItemCount() {
        return data.size();
    }

    //------------------------------------------------------------------------------------------------------//

    /**
     * Viewholder class to instantiate the cardview elements
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView h_name,h_address,h_distance,h_total_user_ratings,h_location;
        RatingBar h_rating;
        CardView circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView textView = (TextView) v.findViewById(R.id.id_location);
                    String locationString = textView.getText().toString();
                  /*  Double lat = Double.parseDouble( locationString.split(",")[0]);
                    Double lng = Double.parseDouble( locationString.split(",")[1]);
                  */

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr="+locationString));
                    activityContext.startActivity(intent);


                }
            });

            h_name = itemView.findViewById(R.id.h_name);
            h_address = itemView.findViewById(R.id.h_address);
            h_rating = itemView.findViewById(R.id.ratingBar);
            h_distance = itemView.findViewById(R.id.h_distance);
            h_total_user_ratings = itemView.findViewById(R.id.h_total_user_ratings);
            h_location = itemView.findViewById(R.id.id_location);

        }
    }


}
