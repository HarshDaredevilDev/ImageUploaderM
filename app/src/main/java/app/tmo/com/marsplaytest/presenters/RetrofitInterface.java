package app.tmo.com.marsplaytest.presenters;

import java.lang.reflect.Array;
import java.util.ArrayList;

import app.tmo.com.marsplaytest.models.ImageApiResponse;
import app.tmo.com.marsplaytest.models.ServerResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitInterface {
    @Multipart
    @POST("/fileUpload.php")
    Call<ServerResponse> uploadFile(@Part MultipartBody.Part file, @Part("file") RequestBody name);

    @GET("/getImageList.php")
    Call<ArrayList<ImageApiResponse>> getImageList();


}