package br.com.instapromo.instapromo.domain.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionMan {

    public static boolean ask(Activity activity, String permission){
        return ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED;

    }

    public static void request(Activity activity, String permission){
        if(!ask(activity, permission)){
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
        }
    }
}
