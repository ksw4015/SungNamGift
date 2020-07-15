package com.sncity.zealo.sungnamgift.Models;

import java.io.Serializable;

/**
 * Created by zealo on 2017-09-13.
 */

public class ShopItem implements Serializable{

    private String storeName;
    private String storeOwner;
    private String storePhone;
    private String storeAddress;
    private String storeItem;
    private String storeCategory;
    private String storeLat;
    private String storeLng;
    private String distance;

    public ShopItem(String storeName, String storeOwner, String storePhone, String storeAddress, String storeItem, String storeCategory, String storeLat, String storeLng, String distance) {
        this.storeName = storeName;
        this.storeOwner = storeOwner;
        this.storePhone = storePhone;
        this.storeAddress = storeAddress;
        this.storeItem = storeItem;
        this.storeCategory = storeCategory;
        this.storeLat = storeLat;
        this.storeLng = storeLng;
        this.distance = distance;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreOwner() {
        return storeOwner;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getStoreItem() {
        return storeItem;
    }

    public String getStoreLat() {
        return storeLat;
    }

    public String getStoreLng() {
        return storeLng;
    }

    public String getDistance() {
        return distance;
    }

    public String getStoreCategory() {
        return storeCategory;
    }
}
