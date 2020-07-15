package com.sncity.zealo.sungnamgift.Network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sncity.zealo.sungnamgift.PreferencesManager.SharedPreferenceManager;
import com.sncity.zealo.sungnamgift.R;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zealo on 2017-09-05.
 */

public class NetworkTask {

    private OkHttpClient client = new OkHttpClient();
    private static  NetworkTask INSTANCE = new NetworkTask();

    public static NetworkTask getInstance() {

        if(INSTANCE == null) {
            INSTANCE = new NetworkTask();
        }

        if(INSTANCE.client == null) {
            INSTANCE.client = new OkHttpClient();
        }

        return INSTANCE;
    }

    private static final String BASE_URL = "http://ksw4015.cafe24.com/";
    private static final String SELECT_STORE = BASE_URL + "select_store_data_dev.php";    // 지도에 띄울 가맹점 마커정보
    private static final String SELECT_STORE_NO_CATEGORY = BASE_URL + "select_store_data_no_dev.php";    // 지도에 띄울 가맹점 마커정보
    private static final String SELECT_STORE_SEARCH = BASE_URL + "select_store_data_location_dev.php";
    private static final String SEARCH_LOCATION = BASE_URL + "SearchLocation.php";        // select_store_data_location.php
    private static final String SELECT_STORE_REVIEWS = BASE_URL + "SelectReviews.php";
    private static final String INSERT_REVIEW = BASE_URL + "reviewInsert.php";
    private static final String SELECT_MY_REVIEWS = BASE_URL + "SelectMyReviews.php";    // 나의 리뷰 관리와 관련해서는 하나의 PHP파일 작성으로 해결할 수 있을지 고민해보기
    private static final String REVISE_MY_REVIEW = BASE_URL + "ReviseMyReview.php";
    private static final String DELETE_MY_REVIEW = BASE_URL + "DeleteMyReview.php";
    private static final String SELECT_LOCATION = BASE_URL + "SelectLocation_test.php";

    private NetworkTask() {

    }

    public static void downLocationData(final Callback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                Request request = new Request.Builder().url(SELECT_LOCATION).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();

    }

    public static void showReviews(final String storeName, final Callback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("storeName", storeName).build();

                Request request = new Request.Builder().url(SELECT_STORE_REVIEWS).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void insertReview(final String storeName, final String nickName, final String text, final String score, final Context context, final Callback callback) {

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                String AD_ID = SharedPreferenceManager.getPreference(context, context.getString(R.string.GOOGLE_ADVERTISE_ID), "None");

                if(AD_ID.equals("None")) {
                    AD_ID = "Undefined Device";
                }

                RequestBody body = new FormBody.Builder()
                                               .add("reviewStoreName", storeName)
                                               .add("nickName", nickName)
                                               .add("reviewText", text)
                                               .add("reviewScore", score)
                                               .add("reviewID", AD_ID)
                                               .build();

                Request request = new Request.Builder()
                                             .url(INSERT_REVIEW)
                                             .post(body)
                                             .build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void searchLocation(final String location_Name, final Callback callback) {   // 아마 getStore 메소드로 대체될듯

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                                                .add("location_Name", location_Name)
                                                .build();

                Request request = new Request.Builder()
                                            .url(SEARCH_LOCATION)
                                            .post(body)
                                            .build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();

    }

    public static void getStoreDatas(final double lat, final double lng, final int category, final Callback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                Request request;
                RequestBody body;

                if(category != 0) {

                    body = new FormBody.Builder()
                            .add("cateGory", String.valueOf(category))
                            .add("Lat", String.valueOf(lat))
                            .add("Lng", String.valueOf(lng))
                            .build();

                    request = new Request.Builder()
                            .url(SELECT_STORE)
                            .post(body)
                            .build();
                }
                else {

                    body = new FormBody.Builder()
                            .add("Lat", String.valueOf(lat))
                            .add("Lng", String.valueOf(lng))
                            .build();

                    request = new Request.Builder()
                            .url(SELECT_STORE_NO_CATEGORY)
                            .post(body)
                            .build();

                    Log.d("170922", "No Category");
                }
                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void getStoreDatas(final String location, final int category, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body;

                if(category != 0) {

                    body = new FormBody.Builder()
                            .add("cateGory", String.valueOf(category))
                            .add("location_dong", location)
                            .build();

                }
                else {

                    body = new FormBody.Builder()
                            .add("location_dong", location)
                            .build();

                }

                Request request = new Request.Builder()
                        .url(SELECT_STORE_SEARCH)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void showMyReview(final Context context, final Callback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                String AD_ID = SharedPreferenceManager.getPreference(context, context.getString(R.string.GOOGLE_ADVERTISE_ID), context.getString(R.string.No_value));

                if(AD_ID.equals(context.getString(R.string.No_value))) {
                    AD_ID = "Undefined Device";
                }

                RequestBody body = new FormBody.Builder().add("myAdid", AD_ID).build();
                Request request = new Request.Builder().url(SELECT_MY_REVIEWS).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void deleteMyReview(final String storeName, final Context context, final Callback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                String AD_ID = SharedPreferenceManager.getPreference(context, context.getString(R.string.GOOGLE_ADVERTISE_ID), context.getString(R.string.No_value));

                if(AD_ID.equals(context.getString(R.string.No_value))) {
                    AD_ID = "Undefined Device";
                }

                RequestBody body = new FormBody.Builder()
                                            .add("reviewID", AD_ID)
                                            .add("reviewStoreName", storeName)
                                            .build();

                Request request = new Request.Builder().url(DELETE_MY_REVIEW).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void reviseMyReview(final String storeName, final String nickName, final String text, final String score, final Context context, final Callback callback) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                String AD_ID = SharedPreferenceManager.getPreference(context, context.getString(R.string.GOOGLE_ADVERTISE_ID), context.getString(R.string.No_value));

                if(AD_ID.equals(context.getString(R.string.No_value))) {
                    AD_ID = "Undefined Device";
                }

                RequestBody body = new FormBody.Builder()
                                                .add("reviewStoreName", storeName)
                                                .add("nickName", nickName)
                                                .add("reviewText", text)
                                                .add("reviewScore", score)
                                                .add("reviewID", AD_ID)
                                                .build();

                Request request = new Request.Builder().url(REVISE_MY_REVIEW).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }
}
