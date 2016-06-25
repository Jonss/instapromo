

package br.com.instapromo.instapromo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import br.com.instapromo.instapromo.connection.ImgUrlAPI;
import br.com.instapromo.instapromo.model.Image;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TimelineActivity extends AppCompatActivity {

    private ImgUrlAPI api = new ImgUrlAPI();
    private ImageView imageView;
    private File picturefile;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        imageView = (ImageView) findViewById(R.id.image_photo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                picturePath = getExternalFilesDir(null) + "/instapromo-" + System.currentTimeMillis() + ".jpg";
                picturefile = new File(picturePath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
                startActivityForResult(intent, 255);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 255) {
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            rx.Observable<Image> post = api.post(picturefile);
            post.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Image>() {
                        @Override
                        public void onCompleted() {
                            Log.d("Deu certo mew", "Tcha-rá");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("Deu ruim meixmo", e.getMessage());
                        }

                        @Override
                        public void onNext(Image image) {
                            Log.d("On next meixmo", image.imgUrlLink());
                        }
                    });
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
