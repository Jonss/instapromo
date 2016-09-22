package br.com.instapromo.instapromo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.desmond.squarecamera.CameraActivity;
import com.desmond.squarecamera.ImageUtility;

import java.io.File;
import java.io.IOException;

import br.com.instapromo.instapromo.connection.Back4AppAPI;
import br.com.instapromo.instapromo.connection.FourSquareAPI;
import br.com.instapromo.instapromo.connection.ImgurAPI;
import br.com.instapromo.instapromo.gps.GeoLocation;
import br.com.instapromo.instapromo.model.FourSquareResponse;
import br.com.instapromo.instapromo.model.ImgurResponse;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
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
import static br.com.instapromo.instapromo.commons.Constants.TAG_PHOTO;
import static br.com.instapromo.instapromo.commons.Constants.TITLE_CHOOSE_LOCAL;
import static br.com.instapromo.instapromo.commons.Constants.USE_CAMERA;
import static br.com.instapromo.instapromo.permission.PermissionMan.hasPermission;
import static br.com.instapromo.instapromo.permission.PermissionMan.requestWithSnack;

/**
 * Created by montanha on 9/21/16.
 */
public class PhotoActivity extends AppCompatActivity {

    private ImgurAPI apiImgur = new ImgurAPI();
    private Back4AppAPI apiBack = new Back4AppAPI();
    private FourSquareAPI apiFoursquare = new FourSquareAPI();

    private ImageView imageView;
    private EditText textLocal;
    private EditText textDesc;
    private EditText textPreco;

    private File picturefile;

    private View viewForSnack;

    private Point mSize;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_tab_photo);

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        Display display = getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);

        viewForSnack = findViewById(R.id.tab2);

        //Text Local
        textLocal = (EditText) findViewById(R.id.textLocal);
        textLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location;

                if (!hasPermission(PhotoActivity.this, ACCESS_COARSE_LOCATION) || !hasPermission(PhotoActivity.this, ACCESS_FINE_LOCATION)) {
                    requestWithSnack(viewForSnack, PhotoActivity.this, PERMISSIONS_LOCATION, permission_location_rationale, REQUEST_LOCATION);
                } else {
                    location = new GeoLocation(PhotoActivity.this).getLocation();

                    rx.Observable<FourSquareResponse> venues = apiFoursquare.search(location.getLatitude(), location.getLongitude(), 10);
                    venues.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<FourSquareResponse>() {
                                @Override
                                public void onCompleted() {
                                    Log.d(TAG_PHOTO, "Completo");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG_PHOTO, e.getMessage());
                                }

                                @Override
                                public void onNext(FourSquareResponse fourSquareResponse) {
                                    final CharSequence venuesStr[] = new CharSequence[10];

                                    Log.d(TAG_PHOTO, String.valueOf(fourSquareResponse.getVenues().size()));
                                    for (int i = 0; i < fourSquareResponse.getVenues().size(); i++) {
                                        venuesStr[i] = fourSquareResponse.getVenues().get(i).getName();
                                    }

                                    AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);
                                    builder.setTitle(TITLE_CHOOSE_LOCAL);
                                    builder.setItems(venuesStr, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            textLocal.setText(venuesStr[which]);
                                        }
                                    });
                                    builder.show();
                                }
                            });
                }
            }
        });

        //Texts
        textDesc  = (EditText) findViewById(R.id.textDesc);
        textPreco = (EditText) findViewById(R.id.textPreco);

        //Image
        imageView = (ImageView) findViewById(R.id.image_photo);

        //Float Cam
        FloatingActionButton fabCam = (FloatingActionButton) findViewById(R.id.fabCam);
        fabCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasPermission(PhotoActivity.this, CAMERA)) {
                    requestWithSnack(viewForSnack, PhotoActivity.this, PERMISSIONS_CAMERA , permission_camera_rationale, REQUEST_CAMERA);
                } else {
                    Intent startCustomCameraIntent = new Intent(PhotoActivity.this, CameraActivity.class);
                    startActivityForResult(startCustomCameraIntent, USE_CAMERA);
                }
            }
        });

        //Float Save
        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(textLocal) || isEmpty(textPreco) || picturefile == null) {
                    makeText(PhotoActivity.this, "Vc deve tirar uma foto e preencher os campos Local e Preco", LENGTH_LONG).show();
                    return;
                }

                if (!hasPermission(PhotoActivity.this, READ_EXTERNAL_STORAGE) || !hasPermission(PhotoActivity.this, WRITE_EXTERNAL_STORAGE)) {
                    requestWithSnack(viewForSnack, PhotoActivity.this, PERMISSIONS_EXTERNAL_STORAGE, permission_storage_rationale, REQUEST_STORAGE);
                } else {

                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                    final Location location;

                    if (!hasPermission(PhotoActivity.this, ACCESS_COARSE_LOCATION) || !hasPermission(PhotoActivity.this, ACCESS_FINE_LOCATION)) {
                        requestWithSnack(viewForSnack, PhotoActivity.this, PERMISSIONS_LOCATION, permission_location_rationale, REQUEST_LOCATION);
                    } else {
                        location = new GeoLocation(PhotoActivity.this).getLocation();

                        rx.Observable<ImgurResponse> post = apiImgur.post(picturefile);
                        post.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<ImgurResponse>() {
                                    @Override
                                    public void onCompleted() {
                                        Log.d(TAG_PHOTO, "Completo");

                                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                                        imageView.setImageResource(R.mipmap.ic_launcher);
                                        textLocal.setText("");
                                        textDesc.setText("");
                                        textPreco.setText("");

                                        //Back to timeline
                                        switchTabInActivity(0);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                                        Log.e(TAG_PHOTO, e.getMessage());
                                    }

                                    @Override
                                    public void onNext(ImgurResponse image) {
                                        Log.d(TAG_PHOTO, image.getData().getLink());

                                        String local = textLocal.getText().toString();
                                        String desc  = textDesc.getText().toString();
                                        String preco = textPreco.getText().toString();

                                        rx.Observable<ResponseBody> postJson =  apiBack.post(local, desc, preco, image.getData().getLink(),
                                                location.getLatitude(), location.getLongitude());
                                        postJson.subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Subscriber<ResponseBody>() {
                                                    @Override
                                                    public void onCompleted() {
                                                        Log.d(TAG_PHOTO, "Completo");
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        Log.e(TAG_PHOTO, e.getMessage());
                                                    }

                                                    @Override
                                                    public void onNext(ResponseBody responseBody) {
                                                        try {
                                                            Log.d(TAG_PHOTO, responseBody.string());
                                                        } catch (IOException e) {
                                                            Log.e(TAG_PHOTO, e.getMessage());
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                }
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private void switchTabInActivity(int indexTabToSwitchTo){
        MainActivity parentActivity = (MainActivity) this.getParent();
        parentActivity.switchTab(indexTabToSwitchTo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == USE_CAMERA) {
            Uri photoUri = data.getData();

            picturefile = new File(photoUri.getPath());

            Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), mSize.x, mSize.x);
            imageView.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == USE_CAMERA) {
//            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}