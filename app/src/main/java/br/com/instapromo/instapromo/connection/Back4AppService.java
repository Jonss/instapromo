package br.com.instapromo.instapromo.connection;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Back4AppService {

    @FormUrlEncoded
    @POST
    Call<String> post(@Header("X-Parse-Application-Id") String applicationId,
                      @Header("X-Parse-REST-API-Key") String restApiKey,
                      @Body String data);

    @FormUrlEncoded
    @GET
    Call<String> get(@Header("X-Parse-Application-Id") String applicationId,
                     @Header("X-Parse-REST-API-Key") String restApiKey,
                     @Body String data);
}