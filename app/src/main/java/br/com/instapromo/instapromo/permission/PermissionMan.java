package br.com.instapromo.instapromo.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import br.com.instapromo.instapromo.R;

public class PermissionMan {

    public static final String TAG = "PermissionMan";

    public static boolean hasPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void request(final View view,
                               final Activity activity,
                               final String[] permissions,
                               final int msgCode,
                               final int requestCode) {
        Log.i(TAG, "Displaying camera permission rationale to provide additional context.");

        Snackbar.make(view,
                activity.getResources().getString(msgCode),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(activity,
                                permissions,
                                requestCode);
                    }
                })
                .show();
    }
}