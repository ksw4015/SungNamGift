package com.sncity.zealo.sungnamgift.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import com.sncity.zealo.sungnamgift.Models.MyReviewData;
import com.sncity.zealo.sungnamgift.Models.SetData;
import com.sncity.zealo.sungnamgift.Network.NetworkTask;
import com.sncity.zealo.sungnamgift.R;
import com.sncity.zealo.sungnamgift.ReviewMyActivity;
import com.sncity.zealo.sungnamgift.ReviewWriteActivity;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by USER on 2017-08-29.
 */
public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.MyReviewHolder>{

    private Context context;
    private ArrayList<MyReviewData> items;

    public MyReviewAdapter(Context context, ArrayList<MyReviewData> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public MyReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_review_item, null);

        MyReviewHolder holder = new MyReviewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyReviewHolder holder, final int position) {

        final String storeName;
        String reviewDate;
        String reviewText;
        float myScore;

        storeName = items.get(position).getReviewStore();
        reviewDate = items.get(position).getReviewDate();
        reviewText = items.get(position).getReviewText();
        myScore = Float.parseFloat(items.get(position).getReviewScore());

        holder.tx_my_store.setText(storeName);
        holder.tx_my_date.setText(reviewDate);
        holder.tx_my_review_text.setText(reviewText);
        holder.rb_my_score.setRating(myScore);

        final SetData data = new SetData(storeName, reviewText, items.get(position).getNickName(), myScore);

        holder.btn_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), ReviewWriteActivity.class);
                intent.putExtra("ACTIVITY_CODE", ReviewMyActivity.MYREVIEWACTIVITY_CODE);
                intent.putExtra("My Data", data);
                context.startActivity(intent);
            }
        });

        holder.btn_my_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("리뷰삭제");
                builder.setMessage("리뷰를 삭제하시겠습니까?")
                                    .setCancelable(false)
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            NetworkTask.deleteMyReview(storeName, context, new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {

                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) throws IOException {

                                                    if(response.code() == 404) {

                                                    }
                                                    else {

                                                        if(response.body().string().equals("Success")) {

                                                            items.remove(position);

                                                            ((ReviewMyActivity)context).runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    notifyDataSetChanged();
                                                                }
                                                            });

                                                        }
                                                        else {
                                                            Log.d("delete", "Failed");
                                                        }

                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyReviewHolder extends RecyclerView.ViewHolder {

        TextView tx_my_store;
        TextView tx_my_date;
        TextView tx_my_review_text;
        RatingBar rb_my_score;
        Button btn_revise;
        Button btn_my_delete;

        public MyReviewHolder(View itemView) {
            super(itemView);

            tx_my_store = (TextView)itemView.findViewById(R.id.Tx_my_store);
            tx_my_date = (TextView)itemView.findViewById(R.id.Tx_my_date);
            tx_my_review_text = (TextView)itemView.findViewById(R.id.Tx_my_review_text);
            rb_my_score = (RatingBar)itemView.findViewById(R.id.Rb_my_score);
            btn_revise = (Button)itemView.findViewById(R.id.Btn_revise);
            btn_my_delete = (Button)itemView.findViewById(R.id.Btn_my_delete);
        }
    }
}
