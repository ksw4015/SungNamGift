package com.sncity.zealo.sungnamgift;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.sncity.zealo.sungnamgift.DataBase.MakeLocationDataBase;
import com.sncity.zealo.sungnamgift.Util.GPSUtil;
import com.sncity.zealo.sungnamgift.Util.NetworkStatusUtil;

import java.io.IOException;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    RelativeLayout rootView;

    Toolbar toolbar;
    ImageView btn_gps;

    ListView mSearchList;
    ArrayAdapter<String> mAdapter;
    ArrayList<String> location_list;     // 검색할 행정동과 지하철역 문자열 (맵 액티비티로 넘어갈때 인텐트로 전달 혹은 전역변수로)

    SearchView searchView;

    Button btn_info, btn_mylocation, btn_food, btn_my_reviews
            , btn_cafe, btn_fashion, btn_book, btn_beauty, btn_enter
            , btn_market, btn_life, btn_pharm;

    NetworkStatusUtil receiver;

    GPSUtil gpsUtil;
    android.app.AlertDialog mDialog;

    // 상수값이니 String 리소스로 관리하는게 어떨까 싶다
    public static final String SEARCH_MAP_MY_LOCATION = "my_location";              // 내 위치 기준으로 검색할 경우
    public static final String SEARCH_MAP_KEYWORD = "keyword_location";             // 검색한 위치 기준으로 검색할 경우

    MakeLocationDataBase myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(MainActivity.this, new Crashlytics());
        setContentView(R.layout.activity_main);

        rootView = (RelativeLayout)findViewById(R.id.root_view);

        IntentFilter intentFilter = new IntentFilter();
        receiver = new NetworkStatusUtil();

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);

        gpsUtil = new GPSUtil(MainActivity.this);
        mDialog = gpsUtil.showAlertSetting(MainActivity.this);

        myDB = new MakeLocationDataBase(MainActivity.this);

        try{
            myDB.createDatabase();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar)findViewById(R.id.m_toolbar);
        btn_gps = (ImageView)findViewById(R.id.Btn_gps);
        btn_gps.setOnClickListener(this);

        btn_info = (Button)findViewById(R.id.Btn_Info);
        btn_info.setOnClickListener(this);
        btn_mylocation = (Button)findViewById(R.id.Btn_MyLocation);
        btn_mylocation.setOnClickListener(this);
        btn_food = (Button)findViewById(R.id.Btn_Food);
        btn_food.setOnClickListener(this);
        btn_cafe = (Button)findViewById(R.id.Btn_Cafe);
        btn_cafe.setOnClickListener(this);
        btn_fashion = (Button)findViewById(R.id.Btn_Fashion);
        btn_fashion.setOnClickListener(this);
        btn_book = (Button)findViewById(R.id.Btn_Book);
        btn_book.setOnClickListener(this);
        btn_beauty = (Button)findViewById(R.id.Btn_Beauty);
        btn_beauty.setOnClickListener(this);
        btn_enter = (Button)findViewById(R.id.Btn_Enter);
        btn_enter.setOnClickListener(this);
        btn_market = (Button)findViewById(R.id.Btn_Market);
        btn_market.setOnClickListener(this);
        btn_life = (Button)findViewById(R.id.Btn_Life);
        btn_life.setOnClickListener(this);
        btn_pharm = (Button)findViewById(R.id.Btn_Pharm);
        btn_pharm.setOnClickListener(this);
        btn_my_reviews = (Button)findViewById(R.id.Btn_My_Reviews);
        btn_my_reviews.setOnClickListener(this);

        mSearchList = (ListView)findViewById(R.id.m_searchList);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        if(!gpsUtil.isGetLocation()) {
            mDialog.show();
        }

        location_list = myDB.getLocationList();
        myDB.close();

        mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, location_list);
        mSearchList.setAdapter(mAdapter);
        mSearchList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.Btn_Info :
                Intent intent = new Intent(MainActivity.this, GiftInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.Btn_MyLocation :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 0, null);
                break;

            case R.id.Btn_Food :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 1, null);
                break;

            case R.id.Btn_Cafe :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 2, null);
                break;

            case R.id.Btn_Fashion :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 3, null);
                break;

            case R.id.Btn_Book :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 4, null);
                break;

            case R.id.Btn_Beauty :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 5, null);
                break;

            case R.id.Btn_Enter :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 6, null);
                break;

            case R.id.Btn_Market :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 7, null);
                break;

            case R.id.Btn_Life :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 8, null);
                break;

            case R.id.Btn_Pharm :
                GoMapActivity(SEARCH_MAP_MY_LOCATION, 9, null);
                break;

            case R.id.Btn_gps :
                mDialog.show();
                break;

            case R.id.Btn_My_Reviews :
//                intent = new Intent(MainActivity.this, ReviewMyActivity.class);
//                startActivity(intent);
                intent = new Intent(MainActivity.this, GoogleMapActivity.class);
                intent.putExtra("mapCode", SEARCH_MAP_MY_LOCATION);
                intent.putExtra("cNumber", 0);
                intent.putStringArrayListExtra("LocationList", location_list);
                intent.putExtra("Location", gpsUtil.getCurrentLocation());
                startActivity(intent);
//                Crashlytics.getInstance().crash();
//                Log.d("180402", "Crash");
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String location_Name = (String) parent.getItemAtPosition(position);
        Toast.makeText(MainActivity.this, location_Name, Toast.LENGTH_SHORT).show();
        searchView.setQuery(location_Name, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        final MenuItem menuItem = menu.findItem(R.id.menuSearch);

        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(Html.fromHtml("<font color = #EEEEEE> 지하철역 혹은 OO동으로 검색해주세요. </font>"));
        searchView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {    // 키보드에 있는 돋보기 버튼 눌렀을 때 작동

                double[] location = myDB.getGeoCode(query);

                if(location[0] == 0.0 && location[1] == 0.0) {
                    Toast.makeText(MainActivity.this, "검색어를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    GoMapActivity(SEARCH_MAP_KEYWORD, 0, query);
                    mSearchList.setVisibility(View.GONE);
                }

                myDB.close();
                menuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(mAdapter!=null) {

                    if(newText.length() >= 2) {
                        if(mSearchList.getVisibility() == View.GONE) {
                            mSearchList.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        if(mSearchList.getVisibility() == View.VISIBLE) {
                            mSearchList.setVisibility(View.GONE);
                        }
                    }
                        mAdapter.getFilter().filter(newText);

                    }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // 매개변수로 int값을 받아서 Map 액티비티로 넘겨주기 (카테고리 분류 넘버)
    public void GoMapActivity(String mapCode, int category, String query) {

        if(receiver.isNetworkEnabled) {

            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            intent.putExtra("mapCode", mapCode);
            intent.putExtra("cNumber", category);
            intent.putStringArrayListExtra("LocationList", location_list);

            if(query != null) {
                intent.putExtra("keyWord", query);
            }

            if(gpsUtil.isGetLocation()) {

                if(gpsUtil.getLatitude() == 0.0 || gpsUtil.getLongitude() == 0.0) {
                    Toast.makeText(MainActivity.this, "위치를 잡지 못 했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    intent.putExtra("Location", gpsUtil.getCurrentLocation());
                    startActivity(intent);
                }

            }
            else {
                mDialog.show();
            }
        }
        else {
            Toast.makeText(MainActivity.this, "네트워크 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        gpsUtil.stopGps();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!gpsUtil.isGetLocation()) {
            gpsUtil.getLocation();
        }

        if(gpsUtil.isGetLocation()) {
            if(mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
/*
        if(mAdapter == null) {
            if(receiver.location_list.size() != 0) {      //

                mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, receiver.location_list);    //
                mSearchList.setAdapter(mAdapter);
                mSearchList.setOnItemClickListener(this);

            }
            else {

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, receiver.location_list);
                        mSearchList.setAdapter(mAdapter);
                        mSearchList.setOnItemClickListener(MainActivity.this);

                    }
                }, 1000);
            }
        }
*/
        if(searchView != null) {
            searchView.setQuery("", false);
            rootView.requestFocus();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
