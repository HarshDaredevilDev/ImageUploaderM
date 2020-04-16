package app.tmo.com.marsplaytest.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import app.tmo.com.marsplaytest.models.ServerResponse;
import app.tmo.com.marsplaytest.views.Camera;
import app.tmo.com.marsplaytest.views.MainActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImages {

    private final Context context;
    private File file;
    TaskCompletion taskCompletion;

    public UploadImages(Context context)
    {
        this.context=context;
        if(context instanceof Camera)
        {
            this.taskCompletion=(Camera)context;
        }else if(context instanceof MainActivity)
        {
            this.taskCompletion=(MainActivity)context;
        }

    }

    public void uploadFile(String mediaPath) {
        // progressDialog.show();

        // Map is used to multipart the file using okhttp3.RequestBody
        File file = null;
        try {
            file = new File(new URI(mediaPath));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("Response", e.getMessage());

        }

        Log.e("fileUri",file.getAbsolutePath()+"   "+mediaPath);
        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        RetrofitInterface getResponse = AppConfig.getRetrofit().create(RetrofitInterface.class);
        Call<ServerResponse> call = getResponse.uploadFile(fileToUpload, filename);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.e("Response", response.raw().toString()+" is the res");

                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    Log.e("Response", response.body().message+" is the res");

                    if (serverResponse.getSuccess()) {
                        Toast.makeText(context.getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        taskCompletion.results(true);
                    } else {
                        taskCompletion.results(false);

                        Toast.makeText(context.getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    taskCompletion.results(false);

                    //assert serverResponse != null;
                }
                // progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                taskCompletion.results(false);

                Log.e("Response", t.getMessage().toString());

            }
        });
    }


    public interface TaskCompletion{

        public void results(boolean result);

    }

}
