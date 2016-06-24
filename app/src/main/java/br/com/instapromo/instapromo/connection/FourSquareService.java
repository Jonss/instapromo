package br.com.instapromo.instapromo.connection;

import br.com.instapromo.instapromo.model.FourSquareResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FourSquareService {

    @POST("v2/venues/search")
    Call<FourSquareResponse> search(@Query("v") String version, @Query("limit") String limit, @Query("client_id") String clientId,
                                    @Query("client_secret") String clientScret, @Query("ll") String latitudeLongitude);
}
