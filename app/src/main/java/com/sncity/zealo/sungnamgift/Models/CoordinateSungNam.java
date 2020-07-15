package com.sncity.zealo.sungnamgift.Models;

/**
 * Created by zealo on 2017-09-11.
 */

public class CoordinateSungNam {

    private String locationLat;
    private String locationLng;

    public CoordinateSungNam(String sungNamLat, String sungNamLng) {
        this.locationLat = sungNamLat;
        this.locationLng = sungNamLng;
    }

    public double getSungNamLat() {
        return Double.parseDouble(locationLat);
    }

    public double getSungNamLng() {
        return Double.parseDouble(locationLng);
    }
}
