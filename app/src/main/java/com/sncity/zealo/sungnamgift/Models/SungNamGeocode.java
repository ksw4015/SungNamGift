package com.sncity.zealo.sungnamgift.Models;

/**
 * Created by zealo on 2017-09-26.
 */

public class SungNamGeocode {

    private String locationLat;
    private String locationLng;
    private String location_dong;

    public SungNamGeocode(String locationLat, String locationLng, String location_dong) {
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.location_dong = location_dong;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public String getLocationLng() {
        return locationLng;
    }

    public String getLocation_dong() {
        return location_dong;
    }
}
