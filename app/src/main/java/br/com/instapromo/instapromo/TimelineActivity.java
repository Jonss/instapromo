package br.com.instapromo.instapromo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
import br.com.instapromo.instapromo.model.FourSquareResponse;
import br.com.instapromo.instapromo.model.ImgurResponse;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TimelineActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        adapter = new ProductAdapter(this, fakeProdList());

        recyclerView = (RecyclerView) findViewById(R.id.timeline);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

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

        //Location
        final Location location = new GeoLocation(this).getLocation();

        //Text Local
        textLocal = (EditText) findViewById(R.id.textLocal);
        textLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                picturePath = getExternalFilesDir(null) + "/instapromo-" + System.currentTimeMillis() + ".jpg";
                picturefile = new File(picturePath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
                startActivityForResult(intent, 255);
            }
        });

        //Float Save
        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }

    private List<Product> fakeProdList() {
        List<Product> products = new ArrayList<>();
        for (int i=0; i < 5; i++) {
            Product product = new Product();
            product.setDesc("desc " + i);
            product.setPrice("" + i);
            product.setStore("loja " + i);
            product.setImageUrl("http://mdemulher.abril.com.br/sites/mdemulher/files/styles/retangular_horizontal_2/public/migracao/receita-macarrao-aromatico.jpg");
            products.add(product);
        }
        return products;
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
}
