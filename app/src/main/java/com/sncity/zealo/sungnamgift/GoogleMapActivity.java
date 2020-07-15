package com.sncity.zealo.sungnamgift;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sncity.zealo.sungnamgift.Models.CoordinateSungNam;
import com.sncity.zealo.sungnamgift.Models.ShopItem;
import com.sncity.zealo.sungnamgift.Network.NetworkTask;
import com.sncity.zealo.sungnamgift.Util.FeedBackDialog;
import com.sncity.zealo.sungnamgift.Util.GPSUtil;
import com.sncity.zealo.sungnamgift.Util.GoogleMapToggleListener;
import com.sncity.zealo.sungnamgift.Util.MakeMarker;
import com.sncity.zealo.sungnamgift.Util.ToggleListener;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2018-08-13.
 */

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback
        ,GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener
        , View.OnClickListener{

    Toolbar map_toolbar;

    ArrayList<String> location_list;
    ArrayAdapter<String> mAdapter;
    ListView map_location_list;

    SearchView searchView;
    ImageView btn_gps_map, btn_category;
    HorizontalScrollView sv_category;

    ToggleButton[] toggle_cateGory;
    GoogleMapToggleListener mListener;
    int[] tBtn_ID = {R.id.tBtn_Food, R.id.tBtn_Cafe, R.id.tBtn_Fashion, R.id.tBtn_Book,
            R.id.tBtn_Beauty, R.id.tBtn_Enter, R.id.tBtn_Market, R.id.tBtn_Life,
            R.id.tBtn_Pharm};

    private GoogleMap mMap;

    ShopItem currentItem;

    ArrayList<ShopItem> shopItems;
    private ArrayList<Marker> shopMarkers;
    private Marker myLocationMarker;

    private String mapCode;
    private int cateGory;

    RelativeLayout ry_store_info;
    TextView storeName, itemName, storePhone, storeAddress;
    Button btn_reviews, btn_request;

    private Location mLastKnownLocation = null;
    private CameraPosition mCurrentCameraPosition;

    private float DEFAULT_ZOOM = 16.0f;
    private LatLng mDefaultLocation;

    // 맵 상태 저장
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        map_toolbar = (Toolbar)findViewById(R.id.google_map_toolbar);
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

        // 검색 구분
        mapCode = getIntent().getStringExtra("mapCode");
        cateGory = getIntent().getIntExtra("cNumber", 0);

        // 서치뷰에 붙일 어댑터 리스트
        location_list = getIntent().getStringArrayListExtra("LocationList");

        // 현재 나의 위치(마지막 위치)
        mLastKnownLocation = getIntent().getParcelableExtra("Location");

        shopMarkers = new ArrayList<>();

        if(location_list.size() != 0) {     //
            mAdapter = new ArrayAdapter<String>(GoogleMapActivity.this, android.R.layout.simple_list_item_1, location_list);
            map_location_list.setAdapter(mAdapter);

            map_location_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String location_Name = (String) parent.getItemAtPosition(position);
                    Toast.makeText(GoogleMapActivity.this, location_Name, Toast.LENGTH_SHORT).show();
                    searchView.setQuery(location_Name, true);
                }
            });
        }

        toggle_cateGory = new ToggleButton[9];
        mListener = (new GoogleMapToggleListener(tBtn_ID, toggle_cateGory, GoogleMapActivity.this, GoogleMapActivity.this, mMap, ry_store_info));

        for(int i = 0 ; i < tBtn_ID.length ; i++) {
            toggle_cateGory[i] = (ToggleButton)findViewById(tBtn_ID[i]);
            toggle_cateGory[i].setOnClickListener(mListener);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mDefaultLocation = new LatLng(37.539432, 126.992290);   // 처음 시작 위치 좌표
        if(mLastKnownLocation != null) {
            Log.d("180813", "onMapReady");
            mDefaultLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        }
        else {
            Toast.makeText(this, "현재위치를 갱신하지 못 했습니다.", Toast.LENGTH_SHORT).show();
        }

        mMap.addMarker(new MarkerOptions().position(mDefaultLocation).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

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
    public void onInfoWindowClick(Marker marker) {

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
                    Toast.makeText(GoogleMapActivity.this, "위치를 잡지 못 했습니다. 위치 설정을 다시하거나"+"\n"+"잠시후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    mLastKnownLocation = GPSUtil.location;
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

                            if(shopMarkers.size() > 0) {
                                for(Marker m : shopMarkers) {
                                    m.remove();
                                }
                            }

                            MarkerOptions myMarker = new MarkerOptions();
                            LatLng myPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                            myMarker.position(myPosition);
                            myMarker.icon(BitmapDescriptorFactory.fromBitmap(createMyMarker(R.drawable.marker_mylocation)));

                            myLocationMarker = mMap.addMarker(myMarker);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                        }
                        else {

                            ShopItem[] items = gson.fromJson(json, ShopItem[].class);
                            toast_msg = items.length + "개의 가맹점을 찾았습니다.";

                            if(shopMarkers.size() > 0) {
                                for(Marker m : shopMarkers) {
                                    m.remove();
                                }
                            }

                            for(int i = 0 ; i < items.length ; i++) {

                                ShopItem item = items[i];

                                LatLng shopPosition = new LatLng(Double.valueOf(item.getStoreLat()), Double.valueOf(item.getStoreLng()));

                                MarkerOptions markers = new MarkerOptions();
                                markers.position(shopPosition);
                                markers.title(item.getStoreName());
                                markers.icon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_choice)));

                                Marker marker = mMap.addMarker(markers);
                                marker.setTag(item);
                                shopMarkers.add(marker);

                            }

                            MarkerOptions myMarker = new MarkerOptions();
                            LatLng myPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                            myMarker.position(myPosition);
                            myMarker.icon(BitmapDescriptorFactory.fromBitmap(createMyMarker(R.drawable.marker_mylocation)));

                            myLocationMarker = mMap.addMarker(myMarker);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GoogleMapActivity.this, toast_msg, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(GoogleMapActivity.this, StoreInfoActivity.class);
                intent.putExtra("ShopObject", currentItem);
                startActivity(intent);
                break;

            case R.id.Btn_Request :
                AlertDialog mDialog = FeedBackDialog.makeDialog(GoogleMapActivity.this, GoogleMapActivity.this, currentItem.getStoreName());
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(geocode.getSungNamLat(), geocode.getSungNamLng())));

                            if(shopMarkers.size() > 0) {
                                for(Marker m : shopMarkers) {
                                    m.remove();
                                }
                            }

                            for(int i = 0 ; i < storeArray.size() ; i++) {
                                ShopItem shop = gson.fromJson(storeArray.get(i), ShopItem.class);

                                LatLng shopPosition = new LatLng(Double.valueOf(shop.getStoreLat()), Double.valueOf(shop.getStoreLng()));

                                MarkerOptions markers = new MarkerOptions();
                                markers.position(shopPosition);
                                markers.title(shop.getStoreName());
                                markers.icon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_choice)));

                                Marker marker = mMap.addMarker(markers);
                                marker.setTag(shop);
                                shopMarkers.add(marker);
                            }

                            msg = storeArray.size() + "개의 가맹점을 찾았습니다.";
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GoogleMapActivity.this, msg, Toast.LENGTH_SHORT).show();
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
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public Bitmap createMarker(int drawable_id) {

        BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(drawable_id);
        Bitmap tempBitmap = drawable.getBitmap();
        Bitmap Marker_Img = Bitmap.createScaledBitmap(tempBitmap, 50, 70, false);

        return Marker_Img;
    }

    public Bitmap createMyMarker(int drawable_id) {

        BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(drawable_id);
        Bitmap tempBitmap = drawable.getBitmap();
        Bitmap Marker_Img = Bitmap.createScaledBitmap(tempBitmap, 64, 90, false);

        return Marker_Img;
    }
}
