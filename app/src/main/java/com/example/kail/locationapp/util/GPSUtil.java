package com.example.kail.locationapp.util;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

public class GPSUtil {

    /**
     * GPS坐标转百度坐标的功能等封装
     * 百度地图用了自己的坐标系统，但是我们一般上传都要是精确的GPS坐标，下面就是提供转换的方法
     * @param latlng
     * @return
     */
    public static LatLng gpsConvertToBaidu(LatLng latlng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(latlng);
        LatLng arg1 = converter.convert();
        return arg1;

    }

    public static LatLng gpsConvertToBaidu(String lagtitude, String longtitude) {
        Double dlat = new Double(lagtitude);
        Double dlot = new Double(longtitude);
        return gpsConvertToBaidu(dlat, dlot);

    }

    public static LatLng gpsConvertToBaidu(Double lagtitude, Double longtitude) {
        LatLng arg0 = new LatLng(lagtitude, longtitude);

        return gpsConvertToBaidu(arg0);
    }
    /**
     * 百度坐标转化为GPS坐标功能封装：
     *
     * 百度坐标和GPS坐标转换在很近的距离时偏差非常接近。
     *  假设你有百度坐标：x1=116.397428，y1=39.90923
     * 把这个坐标当成GPS坐标，通过接口获得他的
     * 百度坐标：x2=116.41004950566，y2=39.916979519873
     * 通过计算就可以得到GPS的坐标： x = 2*x1-x2，y = 2*y1-y2
     * x=116.38480649434001
     * y=39.901480480127
     * @return
     */
    public static LatLng baiduConvertToGps(LatLng lat1) {

        LatLng lat2 = gpsConvertToBaidu(lat1);
        Double latitude = 2 * lat1.latitude - lat2.latitude;
        Double longitude = 2 * lat1.longitude - lat2.longitude;
        return new LatLng(latitude, longitude);

    }


    public static LatLng baiduConvertToGps(Double lagtitude, Double longtitude) {
        LatLng latlng = new LatLng(lagtitude, longtitude);

        return baiduConvertToGps(latlng);
    }


}
