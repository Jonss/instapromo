package br.com.instapromo.instapromo.connection;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Back4AppService {

    @Headers({
            "Content-Type: application/json"
    })
    @POST("promo")
    rx.Observable<ResponseBody> postPromo(@Header("X-Parse-Application-Id") String applicationId,
                                          @Header("X-Parse-REST-API-Key") String restApiKey,
                                          @Body RequestBody data);

    @Headers({
            "Content-Type: application/json"
    })
    @FormUrlEncoded
    @GET("promo")
    Call<ResponseBody> where(@Header("X-Parse-Application-Id") String applicationId,
                             @Header("X-Parse-REST-API-Key") String restApiKey,
                             @Field("where") String where);
}