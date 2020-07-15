package com.sncity.zealo.sungnamgift.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sncity.zealo.sungnamgift.Models.CardViewItem;
import com.sncity.zealo.sungnamgift.R;

import java.util.ArrayList;

/**
 * Created by zealo on 2017-09-20.
 */

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.MyHolder>{

    private Context context;
    private ArrayList<CardViewItem> items;

    public CardViewAdapter(Context context, ArrayList<CardViewItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, null);

        MyHolder holder = new MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        holder.reViewImg.setImageResource(items.get(position).getImgID());
        holder.nickName.setText(items.get(position).getNick());
        holder.reViewDate.setText(items.get(position).getDate());
        holder.reViewText.setText(items.get(position).getText());
        holder.reViewScore.setRating(items.get(position).getStar());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView reViewImg;
        TextView nickName;
        TextView reViewDate;
        TextView reViewText;
        RatingBar reViewScore;

        public MyHolder(View view) {
            super(view);

            reViewImg = (ImageView)view.findViewById(R.id.reViewImg);
            nickName = (TextView)view.findViewById(R.id.nickName);
            reViewDate = (TextView)view.findViewById(R.id.reViewDate);
            reViewText = (TextView)view.findViewById(R.id.reViewText);
            reViewScore = (RatingBar)view.findViewById(R.id.reViewScore);
        }
    }
}
