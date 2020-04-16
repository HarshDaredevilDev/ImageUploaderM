package app.tmo.com.marsplaytest.presenters;

import android.util.Log;

import java.util.ArrayList;

import app.tmo.com.marsplaytest.MainContract;
import app.tmo.com.marsplaytest.models.ImageApiResponse;
import app.tmo.com.marsplaytest.models.ImageListModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageListPresenter implements MainContract.Presenter {
    MainContract.View mView;

    public ImageListPresenter(MainContract.View mView) {
        this.mView = mView;
    }


    @Override
    public void start() {
        mView.initLayout();
    }

    @Override
    public void loadImages() {

        RetrofitInterface getResponse = AppConfig.getRetrofit().create(RetrofitInterface.class);
        Call<ArrayList<ImageApiResponse>> call = getResponse.getImageList();
        call.enqueue(new Callback<ArrayList<ImageApiResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ImageApiResponse>> call, Response<ArrayList<ImageApiResponse>> response) {
                Log.e("Response", response.raw().toString()+" is the res");

                ArrayList<ImageApiResponse> res=response.body();
                ArrayList<ImageListModel> list=new ArrayList<>();
                for(int i=0;i<res.size();i++)
                {

                    ImageListModel model=new ImageListModel();
                    model.setImageUrl(res.get(i).getImageUrl());
                    model.setTitle(res.get(i).getTitle());
                    list.add(model);

                }
                mView.loadImageInList(list);

            }

            @Override
            public void onFailure(Call<ArrayList<ImageApiResponse>> call, Throwable t) {
                Log.e("Response", t.getMessage().toString());

                mView.showError("Image fetch failed");

            }
        });


    }

}