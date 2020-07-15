package com.sncity.zealo.sungnamgift;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.sncity.zealo.sungnamgift.Adapter.MyReviewAdapter;
import com.sncity.zealo.sungnamgift.Models.MyReviewData;
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
 * Created by USER on 2017-08-28.
 */
public class ReviewMyActivity extends AppCompatActivity {

    public static int MYREVIEWACTIVITY_CODE = 101;
    public static AppCompatActivity reViewMyActivity;

    LinearLayoutManager layoutManager;

    RecyclerView list_My_reView;
    ArrayList<MyReviewData> mDatas;

    MyReviewAdapter mAdapter;

    String jsonDatas;

    TextView tx_None_my;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_mine);

        reViewMyActivity = ReviewMyActivity.this;

        layoutManager = new LinearLayoutManager(this);
        list_My_reView = (RecyclerView)findViewById(R.id.List_My_reView);
        tx_None_my = (TextView)findViewById(R.id.Tx_None_my);

        list_My_reView.setLayoutManager(layoutManager);
        list_My_reView.setHasFixedSize(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color = #2C1300>나의 리뷰 관리</font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        NetworkTask.showMyReview(ReviewMyActivity.this, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                jsonDatas = response.body().string();

                if(jsonDatas.equals("None")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tx_None_my.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else {

                    JsonObject jsonObject = new JsonParser().parse(jsonDatas).getAsJsonObject();
                    JsonArray jsonArray = jsonObject.get("reviewData").getAsJsonArray();

                    Gson gson = new Gson();

                    mDatas = new ArrayList<MyReviewData>();

                    for(int i = 0 ; i < jsonArray.size() ; i++) {
                        mDatas.add(gson.fromJson(jsonArray.get(i), MyReviewData.class));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tx_None_my.setVisibility(View.GONE);

                            mAdapter = new MyReviewAdapter(ReviewMyActivity.this, mDatas);
                            list_My_reView.setAdapter(mAdapter);
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
