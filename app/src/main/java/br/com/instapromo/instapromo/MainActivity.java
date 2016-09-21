package br.com.instapromo.instapromo;

import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static br.com.instapromo.instapromo.R.string.permission_camera_rationale;
import static br.com.instapromo.instapromo.R.string.permission_location_rationale;
import static br.com.instapromo.instapromo.R.string.permission_storage_rationale;
import static br.com.instapromo.instapromo.commons.Constants.PERMISSIONS_CAMERA;
import static br.com.instapromo.instapromo.commons.Constants.PERMISSIONS_EXTERNAL_STORAGE;
import static br.com.instapromo.instapromo.commons.Constants.PERMISSIONS_LOCATION;
import static br.com.instapromo.instapromo.commons.Constants.REQUEST_CAMERA;
import static br.com.instapromo.instapromo.commons.Constants.REQUEST_LOCATION;
import static br.com.instapromo.instapromo.commons.Constants.REQUEST_STORAGE;
import static br.com.instapromo.instapromo.commons.Constants.TAG_MAIN;
import static br.com.instapromo.instapromo.commons.Constants.TAG_TIMELINE;
import static br.com.instapromo.instapromo.permission.PermissionMan.hasPermission;
import static br.com.instapromo.instapromo.permission.PermissionMan.request;
import static br.com.instapromo.instapromo.permission.PermissionMan.requestWithSnack;

/**
 * Created by montanha on 9/21/16.
 */
public class MainActivity extends ActivityGroup implements ActivityCompat.OnRequestPermissionsResultCallback {

    private View mLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLayout = findViewById(R.id.llMain);

        Resources res = getResources();
        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup(this.getLocalActivityManager());

        // tab timeline
        Intent intentTimeline = new Intent().setClass(this, TimelineActivity.class);
        TabSpec tabSpecTimeline = host
                .newTabSpec("Timeline")
                .setContent(intentTimeline)
                .setIndicator("", res.getDrawable(R.mipmap.insta_timeline, null));
        host.addTab(tabSpecTimeline);

        // tab photo
        Intent intentPhoto = new Intent().setClass(this, PhotoActivity.class);
        TabSpec tabSpecPhoto = host
                .newTabSpec("Photo")
                .setContent(intentPhoto)
                .setIndicator("", res.getDrawable(R.mipmap.insta_pic, null));
        host.addTab(tabSpecPhoto);

        // tab about
        Intent intentAbout = new Intent().setClass(this, AboutActivity.class);
        TabSpec tabSpecAbout = host
                .newTabSpec("Sobre")
                .setContent(intentAbout)
                .setIndicator("", res.getDrawable(R.mipmap.insta_about, null));
        host.addTab(tabSpecAbout);

        //set timeline tab as default (zero based)
        host.setCurrentTab(0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!hasPermission(this, ACCESS_COARSE_LOCATION) || !hasPermission(this, ACCESS_FINE_LOCATION)) {
            Log.i(TAG_MAIN, "Location permissions has NOT been granted. Requesting permissions.");
            requestLocationPermission();
        } else  if (!hasPermission(this, READ_EXTERNAL_STORAGE) || !hasPermission(this, WRITE_EXTERNAL_STORAGE)) {
            Log.i(TAG_MAIN, "Storage permissions has NOT been granted. Requesting permissions.");
            requestStoragePermission();
        } else  if (!hasPermission(this, CAMERA)) {
            Log.i(TAG_MAIN, "Camera permissions has NOT been granted. Requesting permissions.");
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.i(TAG_MAIN, "CAMERA permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
            Log.i(TAG_MAIN, "Displaying camera permission rationale to provide additional context.");

            requestWithSnack(mLayout, MainActivity.this, PERMISSIONS_CAMERA, permission_camera_rationale, REQUEST_CAMERA);
        } else {
            request(MainActivity.this, PERMISSIONS_CAMERA, REQUEST_CAMERA);
        }
    }

    private void requestLocationPermission() {
        Log.i(TAG_MAIN, "LOCATION permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            Log.i(TAG_MAIN, "Displaying location permission rationale to provide additional context.");

            requestWithSnack(mLayout, MainActivity.this, PERMISSIONS_LOCATION, permission_location_rationale, REQUEST_LOCATION);
        } else {
            request(MainActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    private void requestStoragePermission() {
        Log.i(TAG_MAIN, "STORAGE permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) {
            Log.i(TAG_MAIN, "Displaying storage permission rationale to provide additional context.");

            requestWithSnack(mLayout, MainActivity.this, PERMISSIONS_EXTERNAL_STORAGE, permission_storage_rationale, REQUEST_STORAGE);
        } else {
            request(MainActivity.this, PERMISSIONS_EXTERNAL_STORAGE, REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            Log.i(TAG_TIMELINE, "Received response for Camera permission requestWithSnack.");

            if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
                Log.i(TAG_TIMELINE, "CAMERA permission has now been granted.");
                makeText(this.getApplicationContext(), "Obrigado por liberar o acesso a camera. Pressione o botao da camera para compartilhar promocoes! =)", LENGTH_LONG);
            } else {
                Log.i(TAG_TIMELINE, "CAMERA permission was NOT granted.");
            }
        } else if (requestCode == REQUEST_STORAGE) {
            Log.i(TAG_TIMELINE, "Received response for read external permissions requestWithSnack.");

            if (grantResults.length == 2 && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                Log.i(TAG_TIMELINE, "READ/WRITE permission has now been granted.");
                makeText(this.getApplicationContext(), "Obrigado por liberar o acesso de escrita. Pressione novamente o botao de salvar ", LENGTH_LONG);
            } else {
                Log.i(TAG_TIMELINE, "READ/WRITE permission was NOT granted.");
            }
        } else if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG_TIMELINE, "Received response for access fine external permissions requestWithSnack.");

            if (grantResults.length == 2 && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                Log.i(TAG_TIMELINE, "LOCATION permission has now been granted.");
                makeText(this.getApplicationContext(), "Obrigado por liberar o acesso de localizacao, assim mostraremos as promo ao seu redor! =)", LENGTH_LONG);
            } else {
                Log.i(TAG_TIMELINE, "LOCATION permission was NOT granted.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}