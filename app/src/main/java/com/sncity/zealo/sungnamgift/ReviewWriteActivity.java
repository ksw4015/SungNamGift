package com.sncity.zealo.sungnamgift;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.sncity.zealo.sungnamgift.Models.SetData;
import com.sncity.zealo.sungnamgift.Models.ShopItem;
import com.sncity.zealo.sungnamgift.Network.NetworkTask;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-09-22.
 */

public class ReviewWriteActivity extends AppCompatActivity {

    RatingBar rb_star;

    EditText edt_nickName, edt_reviewText;
    Button btn_insert;

    private int ACTIVITY_CODE;
    String toast;

    ShopItem currentItem;
    SetData myData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        rb_star = (RatingBar)findViewById(R.id.Rb_star);
        edt_nickName = (EditText)findViewById(R.id.Edt_nickName);
        edt_reviewText = (EditText)findViewById(R.id.Edt_reviewText);
        btn_insert = (Button)findViewById(R.id.Btn_Insert);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ACTIVITY_CODE = getIntent().getIntExtra("ACTIVITY_CODE", 0);

        if (ACTIVITY_CODE == StoreInfoActivity.STORE_INFO_ACTIVITY) {
            actionBar.setTitle(Html.fromHtml("<font color = #2C1300>리뷰쓰기</font>"));
            currentItem = (ShopItem)getIntent().getSerializableExtra("ShopObject");
        }
        else {
            actionBar.setTitle(Html.fromHtml("<font color = #2C1300>리뷰수정하기</font>"));

            myData = (SetData)getIntent().getSerializableExtra("My Data");

            edt_nickName.setText(myData.getSetMyNick());
            edt_reviewText.setText(myData.getSetMyText());
            rb_star.setRating(myData.getSetMyScore());
        }

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nickName = edt_nickName.getText().toString();
                String reViewText = edt_reviewText.getText().toString();
                final String reViewScore = String.valueOf(rb_star.getRating());

                if(nickName.equals("")) {
                    Toast.makeText(ReviewWriteActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(reViewText.equals("")) {
                    Toast.makeText(ReviewWriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(reViewScore.equals("0.0")) {
                    Toast.makeText(ReviewWriteActivity.this, "점수는 0.5점이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {

                    if(ACTIVITY_CODE == StoreInfoActivity.STORE_INFO_ACTIVITY) {

                        String storeName = currentItem.getStoreName();

                        NetworkTask.insertReview(storeName, nickName, reViewText, reViewScore, getApplicationContext(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                String msg = response.body().string();

                                if(msg.equals("Success")) {

                                    toast = "저장되었습니다!";

                                    StoreInfoActivity.storeInfoActivity.finish();

                                    Intent intent = new Intent(ReviewWriteActivity.this, StoreInfoActivity.class);
                                    intent.putExtra("ShopObject", currentItem);
                                    startActivity(intent);
                                    finish();
                                }
                                else if(msg.equals("Fail")) {
                                    toast = "실패하였습니다. 다시 시도해주세요.";
                                }
                                else if(msg.equals("Exist")) {
                                    toast = "리뷰가 이미 존재합니다. 나의 리뷰에서 확인해주세요.";
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ReviewWriteActivity.this, toast, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    else if(ACTIVITY_CODE == ReviewMyActivity.MYREVIEWACTIVITY_CODE){

                        String storeName = myData.getSetMyStore();

                        NetworkTask.reviseMyReview(storeName, nickName, reViewText, reViewScore, getApplicationContext(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                String code = response.body().string();

                                if(code.equals("Success")) {

                                    toast = "수정되었습니다.";

                                    ReviewMyActivity.reViewMyActivity.finish();

                                    Intent intent = new Intent(ReviewWriteActivity.this, ReviewMyActivity.class);
                                    startActivity(intent);

                                    finish();
                                }
                                else if(code.equals("Fail")){
                                    toast = "실패하였습니다. 다시 시도해주세요.";
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ReviewWriteActivity.this, toast, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
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
