package br.com.instapromo.instapromo.connection;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import br.com.instapromo.instapromo.model.Back4AppResponse;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static br.com.instapromo.instapromo.commons.Constants.TAG_BACK4APP;
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
//        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL)
                .client(httpClient.build())
                .build();

        return retrofit.create(Back4AppService.class);
    }

    public rx.Observable<ResponseBody> post(String local, String desc, String preco, String urlImg, double latitude, double longitude) {
        JSONObject json = new JSONObject();
        try {
            json.put("local", local)
                    .put("desc", desc)
                    .put("preco", preco)
                    .put("urlImg", urlImg)
                    .put("loc", new JSONObject()
                            .put("__type", "GeoPoint")
                            .put("latitude", latitude)
                            .put("longitude", longitude));
        } catch (JSONException e) {
            Log.e(TAG_BACK4APP, e.getMessage());
        }

        RequestBody data = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                json.toString());

        return retrofit().postPromo(PARSE_APPLICATION_ID, PARSE_REST_API_KEY, data);
    }

    public rx.Observable<Back4AppResponse> get(double latitude, double longitude, int raio) {
        JSONObject json = new JSONObject();

        try {
            json.put("loc", new JSONObject()
                    .put("$nearSphere", new JSONObject()
                            .put("__type", "GeoPoint")
                            .put("latitude", latitude)
                            .put("longitude", longitude))
                    .put("$maxDistanceInKilometers", raio)
            );
        } catch (JSONException e) {
            Log.e(TAG_BACK4APP, e.getMessage());
        }

        Log.i(TAG_BACK4APP, json.toString());

        String query = null;
        try {
            query = URLEncoder.encode(json.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return retrofit().where(PARSE_APPLICATION_ID, PARSE_REST_API_KEY, query);
    }
}