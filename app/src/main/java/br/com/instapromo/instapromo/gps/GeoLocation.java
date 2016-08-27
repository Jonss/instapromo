package br.com.instapromo.instapromo.gps;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class GeoLocation {

    private Activity activity;
    private LocationManager mLocationManager;

    public GeoLocation(Activity activity) {
        this.activity = activity;
        this.mLocationManager = (LocationManager) activity.getApplicationContext().getSystemService(LOCATION_SERVICE);
    }

    public Location getLocation() {
        List<String> providers = this.mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (checkSelfPermission(this.activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                    && checkSelfPermission(this.activity, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                Location l = this.mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }
}