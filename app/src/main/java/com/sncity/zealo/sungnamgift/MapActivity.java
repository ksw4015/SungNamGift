package com.sncity.zealo.sungnamgift;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sncity.zealo.sungnamgift.Models.CoordinateSungNam;
import com.sncity.zealo.sungnamgift.Models.ShopItem;
import com.sncity.zealo.sungnamgift.Network.NetworkTask;
import com.sncity.zealo.sungnamgift.Util.FeedBackDialog;
import com.sncity.zealo.sungnamgift.Util.GPSUtil;
import com.sncity.zealo.sungnamgift.Util.MakeMarker;
import com.sncity.zealo.sungnamgift.Util.ToggleListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-09-05.
 */

public class MapActivity extends AppCompatActivity implements View.OnClickListener, MapView.MapViewEventListener, MapView.POIItemEventListener {

    Toolbar map_toolbar;

    ArrayList<String> location_list;
    ArrayAdapter<String> mAdapter;
    ListView map_location_list;

    SearchView searchView;
    ImageView btn_gps_map, btn_category;
    HorizontalScrollView sv_category;

    ToggleButton[] toggle_cateGory;
    ToggleListener mListener;
    int[] tBtn_ID = {R.id.tBtn_Food, R.id.tBtn_Cafe, R.id.tBtn_Fashion, R.id.tBtn_Book,
                      R.id.tBtn_Beauty, R.id.tBtn_Enter, R.id.tBtn_Market, R.id.tBtn_Life,
                      R.id.tBtn_Pharm};

    MapView mMapView;
    ShopItem currentItem;

    ArrayList<ShopItem> shopItems;

    Location currentlocation;
    private String mapCode;
    private int cateGory;

    RelativeLayout ry_store_info;
    TextView storeName, itemName, storePhone, storeAddress;
    Button btn_reviews, btn_request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map_toolbar = (Toolbar)findViewById(R.id.map_toolbar);
        setSupportActionBar(map_toolbar);

        ry_store_info = (RelativeLayout)findViewById(R.id.Ry_Store_Info);
        storeName = (TextView)findViewById(R.id.StoreName);
        storeAddress = (TextView)findViewById(R.id.StoreAddress);
        storePhone = (TextView)findViewById(R.id.StorePhone);
        itemName = (TextView)findViewById(R.id.ItemName);

        sv_category = (HorizontalScrollView) findViewById(R.id.Sv_Category);
        sv_category.setTag(getResources().getString(R.string.closed));

        shopItems = new ArrayList<>();

        map_location_list = (ListView)findViewById(R.id.map_location_list);
        btn_gps_map = (ImageView)findViewById(R.id.Btn_gps_map);
        btn_gps_map.setOnClickListener(this);
        btn_category = (ImageView)findViewById(R.id.Btn_Category);
        btn_category.setOnClickListener(this);
        btn_reviews = (Button)findViewById(R.id.Btn_Reviews);
        btn_reviews.setOnClickListener(this);
        btn_request = (Button)findViewById(R.id.Btn_Request);
        btn_request.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        mapCode = getIntent().getStringExtra("mapCode");
        cateGory = getIntent().getIntExtra("cNumber", 0);

        location_list = getIntent().getStringArrayListExtra("LocationList");

        currentlocation = getIntent().getParcelableExtra("Location");

        if(currentlocation != null) {
            Log.d("170911", "OnCreate 위도 : " + currentlocation.getLatitude() + " 경도 : " + currentlocation.getLongitude());
        }

        if(location_list.size() != 0) {     //
            mAdapter = new ArrayAdapter<String>(MapActivity.this, android.R.layout.simple_list_item_1, location_list);
            map_location_list.setAdapter(mAdapter);

            map_location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String location_Name = (String) parent.getItemAtPosition(position);
                    Toast.makeText(MapActivity.this, location_Name, Toast.LENGTH_SHORT).show();
                    searchView.setQuery(location_Name, true);
                }
            });
        }

        mMapView = (MapView)findViewById(R.id.map);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);

        toggle_cateGory = new ToggleButton[9];
        mListener = (new ToggleListener(tBtn_ID, toggle_cateGory, MapActivity.this, MapActivity.this, mMapView, ry_store_info));

        for(int i = 0 ; i < tBtn_ID.length ; i++) {
            toggle_cateGory[i] = (ToggleButton)findViewById(tBtn_ID[i]);
            toggle_cateGory[i].setOnClickListener(mListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // GPS셋팅이 맵액티비티에서는 의미가 없어보여서 현재위치로 복귀하는게 어떨지 고민중
            case R.id.Btn_gps_map :

                for(ToggleButton toggleButton : toggle_cateGory) {
                    toggleButton.setChecked(false);
                }

                if(ry_store_info.getVisibility() == View.VISIBLE) {    // 10.5 수정
                    ry_store_info.setVisibility(View.GONE);
                }

                // 위치 못잡을 때가 있음
                if(GPSUtil.location == null) {
                    Toast.makeText(MapActivity.this, "위치를 잡지 못 했습니다. 위치 설정을 다시하거나"+"\n"+"잠시후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    break;
                }

                NetworkTask.getStoreDatas(GPSUtil.location.getLatitude(), GPSUtil.location.getLongitude(), 0, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();
                        final String toast_msg;

                        Gson gson = new Gson();

                        if(json.equals("None")) {
                            toast_msg = "해당하는 가맹점이 없습니다.";

                            mMapView.removeAllPOIItems();

                            MapPOIItem currentCenterMarker = myLocationMarker(MapPoint.mapPointWithGeoCoord(GPSUtil.location.getLatitude(), GPSUtil.location.getLongitude()));
                            mMapView.addPOIItem(currentCenterMarker);

                            mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(GPSUtil.location.getLatitude(), GPSUtil.location.getLongitude()), true);
                        }
                        else {

                            ShopItem[] items = gson.fromJson(json, ShopItem[].class);
                            toast_msg = items.length + "개의 가맹점을 찾았습니다.";

                            mMapView.removeAllPOIItems();

                            for(int i = 0 ; i < items.length ; i++) {

                                MapPOIItem shopItem = MakeMarker.shopMarker(items[i]);
                                mMapView.addPOIItem(shopItem);

                            }

                            mMapView.fitMapViewAreaToShowAllPOIItems();   // ArrayIndex 에러 있음
                            mMapView.zoomOut(true);

                            MapPOIItem currentCenterMarker = myLocationMarker(MapPoint.mapPointWithGeoCoord(GPSUtil.location.getLatitude(), GPSUtil.location.getLongitude()));
                            mMapView.addPOIItem(currentCenterMarker);

                            mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(GPSUtil.location.getLatitude(), GPSUtil.location.getLongitude()), true);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this, toast_msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                break;

            case R.id.Btn_Category :

                if(map_location_list.getVisibility() == View.VISIBLE) {
                    map_location_list.setVisibility(View.GONE);
                }

                if(sv_category.getTag().equals(getResources().getString(R.string.closed))) {
                    sv_category.setVisibility(View.VISIBLE);
                    sv_category.setTag(getResources().getString(R.string.open));
                }
                else {
                    sv_category.setVisibility(View.GONE);
                    sv_category.setTag(getResources().getString(R.string.closed));
                }

                break;

            case R.id.Btn_Reviews :
                Intent intent = new Intent(MapActivity.this, StoreInfoActivity.class);
                intent.putExtra("ShopObject", currentItem);
                startActivity(intent);
                break;

            case R.id.Btn_Request :
                AlertDialog mDialog = FeedBackDialog.makeDialog(MapActivity.this, MapActivity.this, currentItem.getStoreName());
                mDialog.show();
                break;
        }
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
            public boolean onQueryTextSubmit(String query) {

                // 동네 검색시 카테고리 레이아웃을 없앨까 생각중

                for(ToggleButton toggleButton : toggle_cateGory) {
                    toggleButton.setChecked(false);
                }

                // 네트워크 상태 구분
                NetworkTask.getStoreDatas(query, 0, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String json = response.body().string();
                        final String msg;

                        Log.d("180813", json);

                        if(json.equals("None")) {
                            msg = "해당하는 가맹점이 없습니다.";
                        }
                        else if(json.equals("Fail")) {
                            msg = "검색어를 다시 입력해 주세요.";
                        }
                        else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(ry_store_info.getVisibility() == View.VISIBLE) {
                                        ry_store_info.setVisibility(View.GONE);
                                    }

                                    if(map_location_list.getVisibility() == View.VISIBLE) {
                                        map_location_list.setVisibility(View.GONE);
                                    }

                                    if(sv_category.getVisibility() == View.VISIBLE) {
                                        sv_category.setVisibility(View.GONE);
                                    }
                                }
                            });

                            Gson gson = new Gson();

                            JsonObject storeJson = new JsonParser().parse(json).getAsJsonObject();
                            JsonArray storeArray = storeJson.get("storeData").getAsJsonArray();
                            JsonObject coordiante = storeJson.getAsJsonObject("Location");

                            CoordinateSungNam geocode = gson.fromJson(coordiante, CoordinateSungNam.class);
                            mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(geocode.getSungNamLat(), geocode.getSungNamLng()), false);

                            if(mMapView.getPOIItems().length != 0) {
                                mMapView.removeAllPOIItems();
                                shopItems.clear();
                            }

                            for(int i = 0 ; i < storeArray.size() ; i++) {

                                ShopItem shop = gson.fromJson(storeArray.get(i), ShopItem.class);
                                MapPOIItem item = MakeMarker.shopMarker(shop);
                                mMapView.addPOIItem(item);
                            }

                            msg = storeArray.size() + "개의 가맹점을 찾았습니다.";
                            mMapView.fitMapViewAreaToShowAllPOIItems();
                            mMapView.zoomOut(true);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                menuItem.collapseActionView();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(mAdapter != null) {

                    if(newText.length() >= 2) {
                        if(map_location_list.getVisibility() == View.GONE) {
                            map_location_list.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        if(map_location_list.getVisibility() == View.VISIBLE) {
                            map_location_list.setVisibility(View.GONE);
                        }
                    }

                    mAdapter.getFilter().filter(newText);
                }

                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchView.isIconified()) {

                    if(sv_category.getVisibility() == View.VISIBLE) {
                        sv_category.setVisibility(View.GONE);
                    }

                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapViewInitialized(final MapView mapView) {

        // 내 위치 주변버튼 및 품목눌렀을시에 현재 내위치 필요 But 특정장소 검색시에는 불필요
        if(mapCode.equals(MainActivity.SEARCH_MAP_MY_LOCATION)) {

            MapPoint currentPoint = MapPoint.mapPointWithGeoCoord(currentlocation.getLatitude(), currentlocation.getLongitude());  //  currentlocation.getLatitude(), currentlocation.getLongitude()

            MapPOIItem currentMarker = myLocationMarker(currentPoint);
            mapView.addPOIItem(currentMarker);

            NetworkTask.getStoreDatas(currentlocation.getLatitude(), currentlocation.getLongitude(), cateGory, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String json = response.body().string();
                    final String msg;

                    Gson gson = new Gson();

                    if(json.equals("None")) {
                        msg = "해당하는 가맹점이 없습니다.";
                    }
                    else {

                        ShopItem[] items = gson.fromJson(json, ShopItem[].class);
                        msg = items.length + "개의 가맹점을 찾았습니다.";

                        for(int i = 0 ; i < items.length ; i++) {

                            MapPOIItem shopItem = MakeMarker.shopMarker(items[i]);
                            mapView.addPOIItem(shopItem);

                        }

                        mapView.fitMapViewAreaToShowAllPOIItems();
                        mapView.zoomOut(true);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Toast.makeText(MapActivity.this, "현재위치 마커를 움직여 다른 가맹점을 찾아보세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            mapView.setMapCenterPoint(currentPoint, true);
        }
        else {

            String location_keyword = getIntent().getStringExtra("keyWord");

            if(location_keyword != null) {
                searchView.setQuery(location_keyword, true);
            }
        }

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        if(ry_store_info.getVisibility() == View.VISIBLE) {
            ry_store_info.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        if(mapPOIItem.getTag() == 0) {
            if(ry_store_info.getVisibility() == View.VISIBLE) {    // 10.5 수정
                ry_store_info.setVisibility(View.GONE);
            }
            Toast.makeText(MapActivity.this, "현재위치 마커를 움직여 다른 가맹점을 찾아보세요.", Toast.LENGTH_SHORT).show();
        }
        else {
            int Item_count = 0;
            MapPOIItem[] currnetItems = mapView.getPOIItems();

            for(int i = 0 ; i < currnetItems.length ; i++) {
                if(currnetItems[i].getMapPoint().getMapPointGeoCoord().latitude == mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude
                        && currnetItems[i].getMapPoint().getMapPointGeoCoord().longitude == mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude) {
                    Item_count++;
                }
            }

            if(Item_count > 1) {
                Toast.makeText(MapActivity.this, Item_count + "개의 가맹점이 해당 위치에 있습니다.", Toast.LENGTH_SHORT).show();
            }

            currentItem = (ShopItem)mapPOIItem.getUserObject();

            ry_store_info.setVisibility(View.VISIBLE);
            itemName.setText("품목 : " + currentItem.getStoreItem());
            storePhone.setText("전화번호 : " + currentItem.getStorePhone());
            storeName.setText("상호 : " + currentItem.getStoreName());
            storeAddress.setText("주소 : " + currentItem.getStoreAddress());
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        mapView.removePOIItem(mapPOIItem);
        if(ry_store_info.getVisibility() == View.VISIBLE) {
            ry_store_info.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDraggablePOIItemMoved(final MapView mapView, MapPOIItem mapPOIItem, final MapPoint mapPoint) {

        if(mapPOIItem.getTag() == 0 && mapPOIItem.isDraggable()) {

            int cateGoryNum = cateGory;                  // 10.5 수정
            boolean toggleChk = false;

            for(int i = 0 ; i < toggle_cateGory.length ; i++) {
                if(toggle_cateGory[i].isChecked()) {
                    cateGoryNum = i+1;
                    toggleChk = true;
                }
            }

            if(!toggleChk) {
                cateGoryNum = 0;
            }

            Log.d("171005-1", "카테고리 : " + cateGoryNum);

            NetworkTask.getStoreDatas(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude, cateGoryNum, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String json = response.body().string();
                    final String msg;

                    Gson gson = new Gson();

                    if(json.equals("None")) {
                        msg = "해당하는 가맹점이 없습니다.";
                    }
                    else {

                        ShopItem[] items = gson.fromJson(json, ShopItem[].class);
                        msg = items.length + "개의 가맹점을 찾았습니다.";

                        for(MapPOIItem i : mapView.getPOIItems()) {  // 10.5 수정
                            if(i.getTag() != 0) {
                                mapView.removePOIItem(i);
                            }
                        }

                       // mapView.removeAllPOIItems();

                        for(int i = 0 ; i < items.length ; i++) {

                            MapPOIItem shopItem = MakeMarker.shopMarker(items[i]);
                            mapView.addPOIItem(shopItem);

                        }
                        /*
                        mapView.fitMapViewAreaToShowAllPOIItems();
                        mapView.zoomOut(true);
                        */
                        mapView.setMapCenterPoint(mapPoint, true);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }

    public MapPOIItem myLocationMarker(MapPoint currentPoint) {

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("현재위치");
        marker.setMapPoint(currentPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.marker_mylocation);
        marker.setShowDisclosureButtonOnCalloutBalloon(false);
        marker.setDraggable(true);
        marker.setTag(0);

        return marker;
    }

    @Override
    protected void onDestroy() {
        // Added 2018.08.04
        mMapView = null;
        super.onDestroy();
    }
}
