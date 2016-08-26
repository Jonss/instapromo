package br.com.instapromo.instapromo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.instapromo.instapromo.connection.Back4AppAPI;
import br.com.instapromo.instapromo.connection.FourSquareAPI;
import br.com.instapromo.instapromo.connection.ImgurAPI;
import br.com.instapromo.instapromo.gps.GeoLocation;
import br.com.instapromo.instapromo.model.Back4AppResponse;
import br.com.instapromo.instapromo.model.FourSquareResponse;
import br.com.instapromo.instapromo.model.ImgurResponse;
import br.com.instapromo.instapromo.model.Product;
import br.com.instapromo.instapromo.permission.PermissionMan;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static br.com.instapromo.instapromo.R.string.permission_camera_rationale;
import static br.com.instapromo.instapromo.R.string.permission_location_rationale;
import static br.com.instapromo.instapromo.R.string.permission_storage_rationale;

public class TimelineActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "TimelineActivity";

    private ImgurAPI apiImgur = new ImgurAPI();
    private Back4AppAPI apiBack = new Back4AppAPI();
    private FourSquareAPI apiFoursquare = new FourSquareAPI();

    private ImageView imageView;
    private EditText textLocal;
    private EditText textDesc;
    private EditText textPreco;

    private File picturefile;
    private String picturePath;

    private RecyclerView recyclerView;

    private ProductAdapter adapter;

    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    private static String[] PERMISSIONS_CAMERA = {CAMERA};

    private static String[] PERMISSIONS_EXTERNAL_STORAGE = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    private static String[] PERMISSIONS_LOCATION = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION};

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;
    private static final int REQUEST_LOCATION = 2;

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Permissions
        if (!PermissionMan.hasPermission(this, ACCESS_COARSE_LOCATION)
                || !PermissionMan.hasPermission(this, ACCESS_FINE_LOCATION)) {
            PermissionMan.request(host, this, PERMISSIONS_LOCATION, permission_location_rationale, REQUEST_LOCATION);
        }

        Resources res = getResources();

        //Tab 1
        TabHost.TabSpec sPromo = host.newTabSpec("Promocao");
        sPromo.setContent(R.id.tab1);
        sPromo.setIndicator("", res.getDrawable(R.mipmap.insta_timeline, null));
        host.addTab(sPromo);

        //Tab 2
        TabHost.TabSpec sPhoto = host.newTabSpec("Photo");
        sPhoto.setContent(R.id.tab2);
        sPhoto.setIndicator("", res.getDrawable(R.mipmap.insta_pic, null));
        host.addTab(sPhoto);

        //Tab 3
        TabHost.TabSpec sAbout = host.newTabSpec("Sobre");
        sAbout.setContent(R.id.tab3);
        sAbout.setIndicator("", res.getDrawable(R.mipmap.insta_about, null));
        host.addTab(sAbout);

        //Text Local
        textLocal = (EditText) findViewById(R.id.textLocal);
        textLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location = new GeoLocation(TimelineActivity.this).getLocation();

                rx.Observable<FourSquareResponse> venues = apiFoursquare.search(location.getLatitude(), location.getLongitude(), 10);
                venues.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<FourSquareResponse>() {
                            @Override
                            public void onCompleted() {
                                Log.d("[IP] Foursquare", "Completo");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("[IP] Foursquare", e.getMessage());
                            }

                            @Override
                            public void onNext(FourSquareResponse fourSquareResponse) {
                                final CharSequence venuesStr[] = new CharSequence[10];

                                Log.d("[IP] Foursquare", String.valueOf(fourSquareResponse.getVenues().size()));
                                for (int i = 0; i < fourSquareResponse.getVenues().size(); i++) {
                                    venuesStr[i] = fourSquareResponse.getVenues().get(i).getName();
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(TimelineActivity.this);
                                builder.setTitle("Escolha o Local");
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
                if (!PermissionMan.hasPermission(TimelineActivity.this, CAMERA)) {
                    PermissionMan.request(host, TimelineActivity.this, PERMISSIONS_CAMERA , permission_camera_rationale, REQUEST_CAMERA);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    picturePath = getExternalFilesDir(null) + "/instapromo-" + System.currentTimeMillis() + ".jpg";
                    picturefile = new File(picturePath);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
                    startActivityForResult(intent, 255);
                }
            }
        });

        //Float Save
        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PermissionMan.hasPermission(TimelineActivity.this, READ_EXTERNAL_STORAGE)
                        || !PermissionMan.hasPermission(TimelineActivity.this, WRITE_EXTERNAL_STORAGE)) {
                    PermissionMan.request(host, TimelineActivity.this, PERMISSIONS_EXTERNAL_STORAGE, permission_storage_rationale, REQUEST_STORAGE);
                } else {
                    rx.Observable<ImgurResponse> post = apiImgur.post(picturefile);
                    post.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ImgurResponse>() {
                                @Override
                                public void onCompleted() {
                                    Log.d("[IP] Imgur", "Completo");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e("[IP] Imgur", e.getMessage());
                                }

                                @Override
                                public void onNext(ImgurResponse image) {
                                    Log.d("[IP] Imgur", image.getData().getLink());

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
                                                    Log.d("[IP] Back4App", "Completo");
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Log.e("[IP] Back4App", e.getMessage());
                                                }

                                                @Override
                                                public void onNext(ResponseBody responseBody) {
                                                    try {
                                                        Log.d("[IP] Back4App", responseBody.string());
                                                    } catch (IOException e) {
                                                        Log.e("[IP] Back4App", e.getMessage());
                                                    }
                                                }
                                            });
                                }
                            });
                }
            }
        });
    }

    private List<Product> fakeProdList() {
        List<Product> products = new ArrayList<>();
        for (int i=0; i < 5; i++) {
            Product product = new Product();
            product.setDesc("desc " + i);
            product.setPreco("" + i);
            product.setLocal("loja " + i);
            product.setUrlImg("http://mdemulher.abril.com.br/sites/mdemulher/files/styles/retangular_horizontal_2/public/migracao/receita-macarrao-aromatico.jpg");
            products.add(product);
        }
        return products;
    }

    @Override
    protected void onResume() {
        super.onResume();
        location = new GeoLocation(TimelineActivity.this).getLocation();

        rx.Observable<Back4AppResponse> promos = apiBack.get(location.getLatitude(), location.getLongitude(), 5);
        promos.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Back4AppResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d("[IP] Back4App Get", "Completo");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("[IP] Back4App Get", e.getMessage());
                    }

                    @Override
                    public void onNext(Back4AppResponse back4AppResponse) {
                        adapter = new ProductAdapter(TimelineActivity.this, back4AppResponse.getResults());

                        recyclerView = (RecyclerView) findViewById(R.id.timeline);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 255) {
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            Log.i(TAG, "Received response for Camera permission request.");

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "CAMERA permission has now been granted.");
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
            }
        } else if (requestCode == REQUEST_STORAGE) {
            Log.i(TAG, "Received response for read external permissions request.");

            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "READ/WRITE permission has now been granted.");
            } else {
                Log.i(TAG, "READ/WRITE permission was NOT granted.");
            }
        } else if (requestCode == REQUEST_LOCATION) {
            Log.i(TAG, "Received response for access fine external permissions request.");

            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "LOCATION permission has now been granted.");
            } else {
                Log.i(TAG, "LOCATION permission was NOT granted.");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
