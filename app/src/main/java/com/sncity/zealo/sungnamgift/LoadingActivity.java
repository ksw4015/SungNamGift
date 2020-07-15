package com.sncity.zealo.sungnamgift;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.sncity.zealo.sungnamgift.PreferencesManager.SharedPreferenceManager;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

/**
 * Created by zealo on 2017-09-05.
 */

public class LoadingActivity extends AppCompatActivity {

    boolean PERMISSION_CHECKED = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            PERMISSION_CHECKED = false;
        }

        if(!PERMISSION_CHECKED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    // 구글 플레이 스토어 ID값 저장

                    if(SharedPreferenceManager.getPreference(getApplicationContext(), getString(R.string.GOOGLE_ADVERTISE_ID), getString(R.string.No_value)).equals(getString(R.string.No_value))) {
                        new AsyncTask<Void, Void, String>() {

                            @Override
                            protected String doInBackground(Void... params) {

                                String adid = null;

                                try {
                                    adid = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext()).getId();
                                }
                                catch (IOException e) {

                                }
                                catch (GooglePlayServicesNotAvailableException e){
                                    Toast.makeText(LoadingActivity.this, "구글 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                catch (GooglePlayServicesRepairableException e) {

                                }

                                return adid;
                            }

                            @Override
                            protected void onPostExecute(String adid) {
                                super.onPostExecute(adid);
                                SharedPreferenceManager.setPreference(getApplicationContext(), getString(R.string.GOOGLE_ADVERTISE_ID), adid);
                                GoMain();
                            }
                        }.execute();
                    }
                    else {
                        GoMain();
                    }

                    Log.d("170922", "퍼미션확인됨");
                }
            }, 1000);
        }
    }

    // 메인 액티비티로 이동 함수
    private void GoMain() {
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            if(grantResults.length != 0) {
                boolean checked = true;

                for(int code : grantResults) {
                    if(code == PackageManager.PERMISSION_DENIED) {
                        checked = false;
                    }
                }

                if (checked) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // 구글 플레이 스토어 ID값 저장

                            if(SharedPreferenceManager.getPreference(getApplicationContext(), getString(R.string.GOOGLE_ADVERTISE_ID), getString(R.string.No_value)).equals(getString(R.string.No_value))) {
                                new AsyncTask<Void, Void, String>() {

                                    @Override
                                    protected String doInBackground(Void... params) {

                                        String adid = null;

                                        try {
                                            adid = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext()).getId();
                                        }
                                        catch (IOException e) {

                                        }
                                        catch (GooglePlayServicesNotAvailableException e){
                                            Toast.makeText(LoadingActivity.this, "구글 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        catch (GooglePlayServicesRepairableException e) {

                                        }

                                        return adid;
                                    }

                                    @Override
                                    protected void onPostExecute(String adid) {
                                        super.onPostExecute(adid);
                                        SharedPreferenceManager.setPreference(getApplicationContext(), getString(R.string.GOOGLE_ADVERTISE_ID), adid);
                                        GoMain();
                                    }
                                }.execute();
                            }
                            else {
                                GoMain();
                            }

                            Log.d("170922", "퍼미션확인안됨");
                        }
                    }, 2000);
                }
                else {
                    Toast.makeText(LoadingActivity.this, "권한이 거부되었습니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else {
                Toast.makeText(LoadingActivity.this, "권한이 거부되었습니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
