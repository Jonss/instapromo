package br.com.instapromo.instapromo.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import br.com.instapromo.instapromo.model.ImgurResponse;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.MediaType.parse;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

/**
 * Created by joao on 24/06/16.
 */
public class ImgurAPI {

    private static final String IMGURL = "https://api.imgur.com/3/";
    private static final String AUTHORIZATION = "Client-ID 006e897cd5ac791";


    private ImgurService retrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(IMGURL)
                .client(httpClient.build())
                .build();

        return retrofit.create(ImgurService.class);
    }

    public rx.Observable<ImgurResponse> post(File file) {
        RequestBody requestBody = null;

        try {
            InputStream in = new FileInputStream(file);
            byte[] buf = new byte[in.available()];
            while (in.read(buf) != -1);
            requestBody = RequestBody.create(parse("application/octet-stream"), buf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retrofit().upload(AUTHORIZATION, requestBody);
    }
}
