package br.com.instapromo.instapromo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;

import java.io.File;
import java.io.IOException;

import br.com.instapromo.instapromo.connection.Back4AppAPI;
import br.com.instapromo.instapromo.connection.ImgurAPI;
import br.com.instapromo.instapromo.model.ImgurResponse;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TimelineActivity extends AppCompatActivity {

    private ImgurAPI apiImgur = new ImgurAPI();
    private Back4AppAPI apiBack = new Back4AppAPI();

    private ImageView imageView;
    private EditText textLocal;
    private EditText textDesc;
    private EditText textPreco;

    private File picturefile;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

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

        //Texts
        textLocal = (EditText) findViewById(R.id.textLocal);
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

                                rx.Observable<ResponseBody> postJson =  apiBack.post(local, desc, preco, image.getData().getLink(), -43.00, -43.00);
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
