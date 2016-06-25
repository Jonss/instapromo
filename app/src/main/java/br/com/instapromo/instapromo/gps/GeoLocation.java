package br.com.instapromo.instapromo.gps;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.location.LocationManager.GPS_PROVIDER;

public class GeoLocation {

    private Activity activity;
    private LocationManager locationManager;

    public GeoLocation(Activity activity) {
        this.activity = activity;
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
    }

    public Location getLocation() {
        if (ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            return locationManager.getLastKnownLocation(GPS_PROVIDER);
        }

        return null;
    }
}