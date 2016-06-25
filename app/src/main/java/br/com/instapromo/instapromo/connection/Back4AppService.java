package br.com.instapromo.instapromo.connection;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Back4AppService {

    @Headers({
            "Content-Type: application/json"
    })
    @POST("promo")
    rx.Observable<String> postPromo(@Header("X-Parse-Application-Id") String applicationId,
                           @Header("X-Parse-REST-API-Key") String restApiKey,
                           @Body String data);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("promo")
    Call<String> getPromo(@Header("X-Parse-Application-Id") String applicationId,
                          @Header("X-Parse-REST-API-Key") String restApiKey,
                          @Body String data);
}