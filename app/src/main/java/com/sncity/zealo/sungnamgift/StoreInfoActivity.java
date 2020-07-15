package com.sncity.zealo.sungnamgift;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.sncity.zealo.sungnamgift.Adapter.CardViewAdapter;
import com.sncity.zealo.sungnamgift.Models.CardViewItem;
import com.sncity.zealo.sungnamgift.Models.ReviewData;
import com.sncity.zealo.sungnamgift.Models.ShopItem;
import com.sncity.zealo.sungnamgift.Network.NetworkTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-09-20.
 */

public class StoreInfoActivity extends AppCompatActivity {

    public static int STORE_INFO_ACTIVITY = 100;
    public static AppCompatActivity storeInfoActivity;

    ShopItem currentItem;
    TextView tx_info_name, tx_info_owner, tx_info_phone, tx_info_address, tx_none, tx_avg;
    Button btn_reviewWrite;

    RecyclerView reView_ListView;
    ArrayList<CardViewItem> reviewItems;
    CardViewAdapter mAdapter;

    RatingBar rb_avg;
    int[] imgID = {R.drawable.quickicon_active_01, R.drawable.quickicon_active_02, R.drawable.quickicon_active_03, R.drawable.quickicon_active_04,
                   R.drawable.quickicon_active_05, R.drawable.quickicon_active_06, R.drawable.quickicon_active_07, R.drawable.quickicon_active_08,
                   R.drawable.quickicon_active_09};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_storeinfo);

        currentItem = (ShopItem)getIntent().getSerializableExtra("ShopObject");
        storeInfoActivity = StoreInfoActivity.this;

        int cateGory = Integer.parseInt(currentItem.getStoreCategory()) - 1;
        final int currentID = imgID[cateGory];

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color = #2C1300>"+currentItem.getStoreName()+"</font>"));

        TabHost tabHost = (TabHost)findViewById(R.id.m_tabhost);
        tabHost.setup();

        TabHost.TabSpec review = tabHost.newTabSpec("Review");
        review.setContent(R.id.content_review);
        review.setIndicator("리뷰");
        tabHost.addTab(review);

        TabHost.TabSpec infomation = tabHost.newTabSpec("InfoMation");
        infomation.setContent(R.id.content_info);
        infomation.setIndicator("정보");
        tabHost.addTab(infomation);

        tx_info_name = (TextView)findViewById(R.id.Tx_info_name);
        tx_info_name.setText("상호 : " + currentItem.getStoreName());
        tx_info_owner = (TextView)findViewById(R.id.Tx_info_owner);
        tx_info_owner.setText("대표자 : " + currentItem.getStoreOwner());
        tx_info_phone = (TextView)findViewById(R.id.Tx_info_phone);
        tx_info_phone.setText("전화번호 : " + currentItem.getStorePhone());
        tx_info_address = (TextView)findViewById(R.id.Tx_info_address);
        tx_info_address.setText("주소 : " + currentItem.getStoreAddress());

        tx_none = (TextView)findViewById(R.id.Tx_None);
        tx_avg = (TextView)findViewById(R.id.Tx_AVG);
        rb_avg = (RatingBar)findViewById(R.id.Rb_AVG);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        reView_ListView = (RecyclerView)findViewById(R.id.reView_List);
        reView_ListView.setLayoutManager(layoutManager);
        reView_ListView.setHasFixedSize(true);

        btn_reviewWrite = (Button)findViewById(R.id.Btn_reViewWrite);
        btn_reviewWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreInfoActivity.this, ReviewWriteActivity.class);
                intent.putExtra("ACTIVITY_CODE", STORE_INFO_ACTIVITY);
                intent.putExtra("ShopObject", currentItem);
                startActivity(intent);
            }
        });

        NetworkTask.showReviews(currentItem.getStoreName(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();

                if(json.equals("None")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reView_ListView.setVisibility(View.GONE);
                            tx_none.setVisibility(View.VISIBLE);
                        }
                    });

                }
                else {
                    JsonObject object = new JsonParser().parse(json).getAsJsonObject();
                    JsonArray jsonArr = object.get("reviewData").getAsJsonArray();

                    Gson gson = new Gson();

                    reviewItems = new ArrayList<CardViewItem>();

                    for(int i = 0 ; i < jsonArr.size() ; i++) {

                        ReviewData data = gson.fromJson(jsonArr.get(i), ReviewData.class);

                        reviewItems.add(new CardViewItem(currentID, data.getReviewText(), data.getNickName(), data.getReviewDate(), Float.parseFloat(data.getReviewScore())));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tx_none.setVisibility(View.GONE);
                            reView_ListView.setVisibility(View.VISIBLE);
                            Log.d("170922", ""+reviewItems.size());

                            mAdapter = new CardViewAdapter(StoreInfoActivity.this, reviewItems);
                            reView_ListView.setAdapter(mAdapter);

                            float sumStar = 0;
                            for(int i = 0 ; i < reviewItems.size() ; i++) {
                                sumStar+=reviewItems.get(i).getStar();
                            }

                            rb_avg.setRating(sumStar/reviewItems.size());
                            tx_avg.setText("("+String.format("%.2f", sumStar/reviewItems.size())+")");
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home :
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
