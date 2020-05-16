package com.example.welcomebot;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CardLayoutAdapter extends RecyclerView.Adapter<CardLayoutAdapter.ViewHolder> {


    private LayoutInflater layoutInflater;
    private List<PlacesListBean> data;
    private String languageCode;
    FirebaseTranslator englishChineseTranslator;
    FirebaseModelDownloadConditions englishToChineseConditions;



    CardLayoutAdapter(Context context,List<PlacesListBean> data,String languageCode){

        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.languageCode = languageCode;

    }

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String name = data.get(position).getName();
        String address = data.get(position).getAddress();
        Double rating = data.get(position).getRating();
        float distance = data.get(position).getDistance();
        String total_ratings = data.get(position).getTotal_user_ratings();

        if(languageCode.equals("au")){
            holder.h_name.setText(name);
            holder.h_address.setText(address);
            holder.h_rating.setRating(rating.floatValue());
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.UP);
            holder.h_distance.setText(df.format(distance) + " km");
            holder.h_total_user_ratings.setText("("+total_ratings+")");
        }
        else{

            englishChineseTranslator.translate(name).addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {

                    holder.h_name.setText(s);
                    holder.h_address.setText(address);
                    holder.h_rating.setRating(rating.floatValue());
                    DecimalFormat df = new DecimalFormat("0.00");
                    df.setRoundingMode(RoundingMode.UP);
                    holder.h_distance.setText(df.format(distance) + " km");
                    holder.h_total_user_ratings.setText("("+total_ratings+")");

                }
            });

        }




    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView h_name,h_address,h_distance,h_total_user_ratings;
        RatingBar h_rating;
        CardView circle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent i = new Intent(v.getContext(),Details.class);
                    // send story title and contents through recyclerview to detail activity
                    //i.putExtra("titleOfStory",sTitles[getAdapterPosition()]);
                    //i.putExtra("contentOfStory",sContent[getAdapterPosition()]);
                    //v.getContext().startActivity(i);
                }
            });

            h_name = itemView.findViewById(R.id.h_name);
            h_address = itemView.findViewById(R.id.h_address);
            h_rating = itemView.findViewById(R.id.ratingBar);
            h_distance = itemView.findViewById(R.id.h_distance);
            h_total_user_ratings = itemView.findViewById(R.id.h_total_user_ratings);

        }
    }


}
