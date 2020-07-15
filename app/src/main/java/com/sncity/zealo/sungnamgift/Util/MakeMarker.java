package com.sncity.zealo.sungnamgift.Util;

import com.sncity.zealo.sungnamgift.Models.ShopItem;
import com.sncity.zealo.sungnamgift.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

/**
 * Created by zealo on 2017-09-17.
 */

public class MakeMarker {

    public static MapPOIItem shopMarker(ShopItem item) {

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(item.getStoreName());
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.valueOf(item.getStoreLat()), Double.valueOf(item.getStoreLng())));
        marker.setUserObject(item);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.marker_choice);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomSelectedImageResourceId(R.drawable.marker_non_choice);
        marker.setShowDisclosureButtonOnCalloutBalloon(false);
        marker.setTag(1);

        return marker;

    }

}
