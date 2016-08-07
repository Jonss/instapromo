package br.com.instapromo.instapromo.connection;

import br.com.instapromo.instapromo.model.FourSquareResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class FourSquareAPI {

    private static final String URL = "https://api.foursquare.com";
    private static final String VERSION = "20160301";
    private static final String CLIENT_ID = "VZOV13XTCAUVXLARQYN4L5OSGTUEIMJZNUUL4U11FAGN2MM2";
    private static final String CLIENT_SECRET = "MHP1PC313HSS4QCV40JDK43Y40SAY1HV4DFVNPWLCYL104PT";

    private FourSquareService retrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL)
                .client(httpClient.build())
                .build();

        return retrofit.create(FourSquareService.class);
    }

    public rx.Observable<FourSquareResponse> search(double latitude, double longitude, int limit){
        return retrofit().search(VERSION,
                String.valueOf(limit),
                CLIENT_ID,
                CLIENT_SECRET,
                formatLocation(latitude, longitude));
    }

    private String formatLocation(double latitude, double longitude) {
        return new StringBuilder()
                .append(latitude)
                .append(",")
                .append(longitude)
                .toString();
    }
}