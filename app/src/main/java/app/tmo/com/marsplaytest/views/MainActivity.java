package app.tmo.com.marsplaytest.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import app.tmo.com.marsplaytest.MainContract;
import app.tmo.com.marsplaytest.R;
import app.tmo.com.marsplaytest.models.ImageListModel;
import app.tmo.com.marsplaytest.presenters.ImageListAdapter;
import app.tmo.com.marsplaytest.presenters.ImageListPresenter;
import app.tmo.com.marsplaytest.presenters.RealPathUri;
import app.tmo.com.marsplaytest.presenters.UploadImages;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ablanco.zoomy.Zoomy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.viven.imagezoom.ImageZoomHelper;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainContract.View , ImageListAdapter.SetZoomableView , UploadImages.TaskCompletion {

    FloatingActionButton cameraButton;
    RecyclerView imageRecycler;
    ImageListAdapter adapter;
    ArrayList<ImageListModel> list;
    private ImageZoomHelper imageZoomHelper;
    ImageListPresenter presenter;
    private ViewAnimation viewAnimation;
    private boolean isRotate=false;
    private FloatingActionButton capture,gallery;
    private int REQUEST_GET_SINGLE_FILE=10;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewAnimation=new ViewAnimation();

        presenter=new ImageListPresenter(this);
        presenter.start();

    }




    @Override
    public void setUpZoomView(ImageView cardView) {
        Zoomy.Builder builder = new Zoomy.Builder(MainActivity.this).target(cardView);
        builder.register();

    }

    @Override
    public void initLayout() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
        setstatusbarcolor(this,getWindow(),R.color.color_white);

        cameraButton=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        capture=(FloatingActionButton)findViewById(R.id.capture);
        gallery=(FloatingActionButton)findViewById(R.id.gallery);
        imageRecycler=(RecyclerView)findViewById(R.id.image_recycler);
        list=new ArrayList<>();
        imageZoomHelper = new ImageZoomHelper(this);
        imageRecycler.setItemAnimator(null);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent i=new Intent(MainActivity.this, Camera.class);
                startActivityForResult(i,23);

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(pickPhoto, REQUEST_GET_SINGLE_FILE);

            }
        });
        viewAnimation.init(capture);
        viewAnimation.init(gallery);

        StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        adapter=new ImageListAdapter(list,MainActivity.this,"");
        imageRecycler.setAdapter(adapter);

        imageRecycler.setLayoutManager(gridLayoutManager);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate= viewAnimation.rotateFab(v, !isRotate);
                if(isRotate){
                    viewAnimation.showIn(capture);
                    viewAnimation.showIn(gallery);
                }else{
                    viewAnimation.showOut(capture);
                    viewAnimation.showOut(gallery);
                }

            }
        });

        presenter.loadImages();
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void loadImageInList(ArrayList<ImageListModel> images) {

        list.clear();
        list.addAll(images);
        adapter.notifyDataSetChanged();
    }



    public static void setstatusbarcolor(Context ctx, Window win, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            Window window = win;
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#757575"));

        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Window window = win;
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ctx.getResources().getColor(color));

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {
                    Log.e("path", "is not nnull");

                    Uri selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = RealPathUri.getPath(MainActivity.this, selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                        CropImage.activity(selectedImageUri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(MainActivity.this);


                    }else {
                        Log.e("path","is nnull");
                    }

                }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        //handleCropResult(result);if
                        UploadImages images=new UploadImages(MainActivity.this);
                        images.uploadFile(result.getUri().toString());
                        // uploadFile(result.getUri().toString());
                        dialog = new Dialog(MainActivity.this);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.uploading);
                        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                        lp.dimAmount = 0.85f;
                        Window window = dialog.getWindow();
                        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        dialog.show();


                     // Set the image in ImageView
                }else{
                    presenter.loadImages();

                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }

           }


    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    @Override
    public void results(boolean result) {
        if(result)
        {
            dialog.cancel();
            presenter.loadImages();

        }else{

            dialog.cancel();

        }
    }
}
