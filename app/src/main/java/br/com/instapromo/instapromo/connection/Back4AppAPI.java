package br.com.instapromo.instapromo.connection;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class Back4AppAPI {

    private static final String URL = "https://parseapi.back4app.com/classes/promo";
    private static final String PARSE_APPLICATION_ID = "5V0YmBQlDDM7omVHZjZ8OXBJW723oUzwXmym2KVS";
    private static final String PARSE_REST_API_KEY   = "lLW27jlnrBZXuXlSXLxn4HQzH7aEDTNZgnPty3Or";

    private Back4AppService retrofit() {
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

        return retrofit.create(Back4AppService.class);
    }

    public Call<String> post(String dataJson){
        return retrofit().post(PARSE_APPLICATION_ID, PARSE_REST_API_KEY, dataJson);
    }

    public Call<String> get(String queryJson){
        return retrofit().get(PARSE_APPLICATION_ID, PARSE_REST_API_KEY, queryJson);
    }
}
