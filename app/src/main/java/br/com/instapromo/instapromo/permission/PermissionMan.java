package br.com.instapromo.instapromo.permission;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import br.com.instapromo.instapromo.R;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class PermissionMan {

    public static final String TAG = "PermissionMan";

    public static boolean hasPermission(Activity activity, String permission) {
        return checkSelfPermission(activity, permission) == PERMISSION_GRANTED;
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
                        requestPermissions(activity,
                                permissions,
                                requestCode);
                    }
                })
                .show();
    }
}