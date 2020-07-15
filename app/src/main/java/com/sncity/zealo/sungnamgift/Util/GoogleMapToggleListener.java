package com.sncity.zealo.sungnamgift.Util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.sncity.zealo.sungnamgift.Models.ShopItem;
import com.sncity.zealo.sungnamgift.Network.NetworkTask;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2018-08-13.
 */

public class GoogleMapToggleListener implements View.OnClickListener {

    int[] ToggleButtonID;

    ToggleButton[] toggleButtons;
    Context context;
    Activity mActivity;
    RelativeLayout ry_info;

    int cateGory;

    GoogleMap mapView;

    ShopItem[] items;

    MapPOIItem[] removeStores = null;
    MapPOIItem[] addStores = null;

    public GoogleMapToggleListener(int[] toggleButtonID, ToggleButton[] toggleButtons, Context context, Activity mActivity, GoogleMap mapView, RelativeLayout infolayout) {
        ToggleButtonID = toggleButtonID;
        this.toggleButtons = toggleButtons;
        this.context = context;
        this.mActivity = mActivity;
        this.mapView = mapView;
        ry_info = infolayout;
    }

    @Override
    public void onClick(View v) {

        if(ry_info.getVisibility() == View.VISIBLE) {
            ry_info.setVisibility(View.GONE);
        }

        boolean ButtonOn = false;

        // 클릭한 버튼을 제외한 나머지 버튼은 해제
        for(int i = 0 ; i < ToggleButtonID.length ; i++) {

            if(ToggleButtonID[i] != v.getId()) {
                toggleButtons[i].setChecked(false);
            }

            if(toggleButtons[i].isChecked()) {
                ButtonOn = true;
            }
        }

        if(ButtonOn) {

            if(v.getId() == ToggleButtonID[0]) {
                cateGory = 1;
            }
            else if (v.getId() == ToggleButtonID[1]) {
                cateGory = 2;
            }
            else if (v.getId() == ToggleButtonID[2]) {
                cateGory = 3;
            }
            else if (v.getId() == ToggleButtonID[3]) {
                cateGory = 4;
            }
            else if (v.getId() == ToggleButtonID[4]) {
                cateGory = 5;
            }
            else if (v.getId() == ToggleButtonID[5]) {
                cateGory = 6;
            }
            else if (v.getId() == ToggleButtonID[6]) {
                cateGory = 7;
            }
            else if (v.getId() == ToggleButtonID[7]) {
                cateGory = 8;
            }
            else if (v.getId() == ToggleButtonID[8]) {
                cateGory = 9;
            }

            if(mapView == null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "맵 정보를 가져오지 못 했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {

                NetworkTask.getStoreDatas(mapView.getCameraPosition().target.latitude, mapView.getCameraPosition().target.longitude, cateGory,
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String jsonData = response.body().string();
                                final String msg;

                                Gson gson = new Gson();

                                final ArrayList<MapPOIItem> storeItems = new ArrayList<>();

                                if(jsonData.equals("None")) {
                                    msg = "해당하는 가맹점이 없습니다.";
                                }
                                else {
                                    items = gson.fromJson(jsonData, ShopItem[].class);
                                    msg = items.length + "개의 가맹점을 찾았습니다.";

                                    // mapView.removeAllPOIItems();
                                    // 내 위치 마커를 제외한 나머지 마커 삭제
//                                    for(MapPOIItem i : mapView.getPOIItems()) {
//                                        if(i.getTag() != 0) {
//                                            //mapView.removePOIItem(i);
//                                            storeItems.add(i);
//                                        }
//                                    }

                                    if(storeItems.size() > 0) {
                                        removeStores = storeItems.toArray(new MapPOIItem[storeItems.size()]);
                                        storeItems.clear();
                                    }

                                    for(int i = 0 ; i < items.length ; i++) {
                                    /*
                                        MapPOIItem storeItem = new MapPOIItem();
                                        storeItem = MakeMarker.shopMarker(items[i]);

                                        mapView.addPOIItem(storeItem);
                                    */
                                        storeItems.add(MakeMarker.shopMarker(items[i]));
                                    }

                                    if(storeItems.size() > 0) {
                                        addStores = storeItems.toArray(new MapPOIItem[storeItems.size()]);
                                        storeItems.clear();
                                    }

                                }

//                                mActivity.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if(removeStores != null) {
//                                            mapView.removePOIItems(removeStores);
//                                        }
//                                        if(addStores != null) {
//                                            mapView.addPOIItems(addStores);
//                                        }
//
//                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                            }
                        });
            }
        }
        else {
//            for(MapPOIItem i : mapView.getPOIItems()) {
//                if(i.getTag() != 0) {
//                    mapView.removePOIItem(i);
//                }
//            }
            // mapView.removeAllPOIItems();
        }
    }
}
