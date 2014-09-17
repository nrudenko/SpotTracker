package com.github.nrudenko.spottracker.utils;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;


public class LocationUtils {

    public static boolean isFineLocationAvailable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int locationMode;
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                locationMode = Settings.Secure.LOCATION_MODE_OFF;
                e.printStackTrace();
            }
            return locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
        } else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            String providerName = LocationManager.GPS_PROVIDER;
            return providerName != null && locationManager.isProviderEnabled(providerName);
        }
    }
}