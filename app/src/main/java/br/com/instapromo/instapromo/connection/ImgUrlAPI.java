package br.com.instapromo.instapromo.connection;


import android.net.Uri;
import android.util.Log;

import java.io.File;

import br.com.instapromo.instapromo.model.Image;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public rx.Observable<Image> post(Uri fileUri) {
        File file = new File(fileUri.getPath());

        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        return retrofit().upload(AUTHORIZATION, fileRequestBody, nameRequestBody);
    }
}
