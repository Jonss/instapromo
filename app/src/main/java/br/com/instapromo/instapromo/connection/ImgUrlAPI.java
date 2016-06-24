package br.com.instapromo.instapromo.connection;


import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import br.com.instapromo.instapromo.model.Image;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by joao on 24/06/16.
 */
public class ImgUrlAPI {

    private static final String IMGURL = "https://api.imgur.com/3/";
    private static final String AUTHORIZATION = "Client-ID 006e897cd5ac791";

    private ImgUrlService retrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(IMGURL)
                .build();

        return retrofit.create(ImgUrlService.class);
    }

    public rx.Observable<Image> post(File file) {
        RequestBody fileRequestBody = null;
        try {
            FileInputStream stream = new FileInputStream(file);
            byte[] buf;
            buf = new byte[stream.available()];
            fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), buf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        
        return retrofit().upload(AUTHORIZATION, fileRequestBody);
    }
}
