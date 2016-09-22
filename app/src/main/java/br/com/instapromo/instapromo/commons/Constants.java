package br.com.instapromo.instapromo.commons;

import static android.Manifest.permission.*;

/**
 * Created by montanha on 9/21/16.
 */

public class Constants {

    public static final String TAG_MAIN     = "[IP] MainActivity";
    public static final String TAG_TIMELINE = "[IP] TimelineActivity";
    public static final String TAG_PHOTO    = "[IP] PhotoActivity";
    public static final String TAG_ABOUT    = "[IP] AboutActivity";

    public static final String TAG_PERMISSION = "PermissionMan";

    public static final String URL_NO_PROMOTION = "http://i.imgur.com/INAUbtO.png";
    public static final String TITLE_CHOOSE_LOCAL = "Escolha o Local";

    public static String[] PERMISSIONS_CAMERA = {CAMERA};

    public static String[] PERMISSIONS_EXTERNAL_STORAGE = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    public static String[] PERMISSIONS_LOCATION = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION};

    public static final int USE_CAMERA = 255;

    public static final int REQUEST_CAMERA = 0;
    public static final int REQUEST_STORAGE = 1;
    public static final int REQUEST_LOCATION = 2;
}