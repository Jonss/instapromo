package br.com.instapromo.instapromo.connection;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

/**
 * Created by montanha on 25/06/16.
 */
public class Back4AppAPI {

    private static final String URL = "https://parseapi.back4app.com/classes/";
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

    public rx.Observable<String> post(String local, String desc, String preco, String urlImg, double latitude, double longitude) {
        String json =new StringBuilder()
                .append("{\"local\":\"").append(local)
                .append("\",\"desc\":\"").append(desc)
                .append("\",\"preco\":\"").append(preco)
                .append("\",\"urlImg\":\"").append(urlImg)
                .append("\",\"loc\":{\"__type\":\"GeoPoint\",\"latitude\":").append(latitude)
                .append(",\"longitude\":").append(longitude)
                .append("}}")
                .toString();

        return retrofit().postPromo(PARSE_APPLICATION_ID, PARSE_REST_API_KEY, json);
    }

    public Call<String> get(double latitude, double longitude, int raio) {
        String json =new StringBuilder()
                .append("where={\"loc\":{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"latitude\":").append(String.valueOf(latitude))
                .append(",\"longitude\":").append(String.valueOf(longitude))
                .append("},\"$maxDistanceInKilometers\":").append(String.valueOf(raio))
                .append("}}")
                .toString();

        return retrofit().getPromo(PARSE_APPLICATION_ID, PARSE_REST_API_KEY, json);
    }
}
