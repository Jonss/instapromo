package br.com.instapromo.instapromo.connection;




import br.com.instapromo.instapromo.model.Image;
import okhttp3.RequestBody;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by joao on 24/06/16.
 */
public interface ImgUrlService {

    @Multipart
    @POST("upload")
    Observable<Image> upload(
            @Header("Authorization") String authorization,
            @Part("file") RequestBody file,
            @Part("name") RequestBody name
    );
}
