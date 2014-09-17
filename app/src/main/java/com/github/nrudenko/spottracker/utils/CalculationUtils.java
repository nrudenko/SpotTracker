package com.github.nrudenko.spottracker.utils;

import com.google.android.gms.maps.model.LatLng;

public class CalculationUtils {

    public static double EARTH_RADIUS = 6378.1 * 1000; //Earth radius in m

    /**
     * Make calculation for distant point
     *
     * @param locationFrom point from
     * @param distance     in meters
     * @param bearing      in degree
     * @return distant point
     */
    public static LatLng getDistantPoint(LatLng locationFrom, double distance, double bearing) {
        double bearingRad = Math.toRadians(bearing);

        double lat1 = Math.toRadians(locationFrom.latitude); //Current lat point converted to radians
        double lon1 = Math.toRadians(locationFrom.longitude); //Current long point converted to radians

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / EARTH_RADIUS) +
                Math.cos(lat1) * Math.sin(distance / EARTH_RADIUS) * Math.cos(bearingRad));

        double lon2 = lon1 + Math.atan2(Math.sin(bearingRad) * Math.sin(distance / EARTH_RADIUS) * Math.cos(lat1),
                Math.cos(distance / EARTH_RADIUS) - Math.sin(lat1) * Math.sin(lat2));

        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);

        return new LatLng(lat2, lon2);
    }

    /**
     * Make calculation for distance between two points
     *
     * @param first  point
     * @param second point
     * @return calculated distance between two points in meters
     */
    public static double getDistance(LatLng first, LatLng second) {
        double fLat = first.latitude;
        double fLon = first.longitude;

        double sLat = second.latitude;
        double sLon = second.longitude;

        double dLat = Math.toRadians(sLat - fLat);
        double dLon = Math.toRadians(sLon - fLon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(fLat)) * Math.cos(Math.toRadians(fLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = EARTH_RADIUS * c;

        return valueResult;
    }
}
