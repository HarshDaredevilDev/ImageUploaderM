package app.tmo.com.marsplaytest;

import java.util.ArrayList;
import java.util.List;

import app.tmo.com.marsplaytest.models.ImageListModel;

public interface MainContract {
    interface View {
        void initLayout();

        void showError(String message);

        void loadImageInList(ArrayList<ImageListModel> images);
    }

    interface Presenter {

        void start();

        void loadImages();
    }

}