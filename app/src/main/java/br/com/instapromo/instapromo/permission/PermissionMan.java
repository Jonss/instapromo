package br.com.instapromo.instapromo.permission;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import br.com.instapromo.instapromo.R;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;
import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static br.com.instapromo.instapromo.commons.Constants.TAG_PERMISSION;

public class PermissionMan {

    public static boolean hasPermission(Activity activity, String permission) {
        return checkSelfPermission(activity, permission) == PERMISSION_GRANTED;
    }

    public static void requestWithSnack(final View view, final Activity activity, final String[] permissions,
                                        final int msgCode, final int requestCode) {
        Log.i(TAG_PERMISSION, "Creating a snake to requestWithSnack a permission.");

        Snackbar.make(view,
                activity.getResources().getString(msgCode),
                LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        request(activity, permissions, requestCode);
                    }
                })
                .show();
    }

    public static void request(final Activity activity, final String[] permissions, final int requestCode) {
        requestPermissions(activity, permissions, requestCode);
    }
}