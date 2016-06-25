package br.com.instapromo.instapromo.connection;

import br.com.instapromo.instapromo.model.Image;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by joao on 24/06/16.
 */
public interface ImgUrlService {

    @POST("upload")
    Observable<Image> upload(@Header("Authorization") String authorization,
                             @Body RequestBody photo
    );
}