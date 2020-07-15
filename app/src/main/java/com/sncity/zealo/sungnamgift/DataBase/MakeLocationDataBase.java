package com.sncity.zealo.sungnamgift.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by USER on 2017-09-25.
 */
public class MakeLocationDataBase extends SQLiteOpenHelper{

    String DB_PATH = null;
    private static String DB_NAME = "location_data.db";
    private SQLiteDatabase myDB;
    private final Context mContext;

    public MakeLocationDataBase(Context context) {
        super(context, DB_NAME, null, 10);
        this.mContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
    }

    public void createDatabase() throws IOException {

        boolean dbExist = checkDatabase();
        if(dbExist) {
            Log.d("170925", "DB EXIST");
        }
        else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            }
            catch (IOException e) {
                throw new Error("Copy DB Error");
            }
        }
    }

    public boolean checkDatabase() {

        SQLiteDatabase checkDB = null;

        try{
            String MY_PAHT = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(MY_PAHT, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e) {

        }
        if(checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;           // 지정된 경로에 DB가 이미 존재하면 true 아니면 false값 리턴
    }

    private void copyDataBase() throws IOException{

        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[10];
        int length;

        while ((length = mInput.read(buffer)) > 0) {
            mOutput.write(buffer, 0, length);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();

        Log.d("170925", "DB COPY COMPLETE");
    }

    private void openDataBase() throws SQLiteException {
        String myPath = DB_PATH + DB_NAME;
        myDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(myDB != null) {
            myDB.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            try {
                copyDataBase();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getLocationList() {

        openDataBase();
        Cursor cursor = myDB.query("location_table", null, null, null, null, null, null);

        ArrayList<String> LocationList = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                LocationList.add(cursor.getString(1));
            }
            while (cursor.moveToNext());
        }

        cursor.close();

        return LocationList;
    }

    public double[] getGeoCode(String location) {

        openDataBase();

        double[] geoCode = new double[2];

        // SQL 인젝션 위험
        Cursor cursor = myDB.rawQuery("SELECT location_lat, location_lng FROM location_table WHERE location_dong = '" + location + "';", null);

        if(cursor.moveToFirst()) {
            do {
                geoCode[0] = Double.valueOf(cursor.getString(0));
                geoCode[1] = Double.valueOf(cursor.getString(1));
            }
            while (cursor.moveToNext());
        }

        cursor.close();

        return geoCode;
    }
}
