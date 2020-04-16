/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tmo.com.marsplaytest.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import app.tmo.com.marsplaytest.R;
import app.tmo.com.marsplaytest.models.ServerResponse;
import app.tmo.com.marsplaytest.presenters.AppConfig;
import app.tmo.com.marsplaytest.presenters.RetrofitInterface;
import app.tmo.com.marsplaytest.presenters.UploadImages;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Camera extends AppCompatActivity implements Camera2BasicFragment.CropAndUpload, UploadImages.TaskCompletion {

    private Dialog dialog;
    private boolean isUploadDone=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam);
        if (null == savedInstanceState) {

            if (ContextCompat.checkSelfPermission(Camera.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted

                ActivityCompat.requestPermissions(Camera.this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        12);

            }else{
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new Camera2BasicFragment(Camera.this))
                        .commit();

            }

             }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 12: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new Camera2BasicFragment(Camera.this))
                            .commit();

                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    ActivityCompat.requestPermissions(Camera.this,
                            new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                            12);

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            //handleCropResult(result);if
            if(resultCode==RESULT_OK)
            {
                UploadImages images=new UploadImages(Camera.this);
                Log.e("fileUri",result.getUri().toString());
                images.uploadFile(result.getUri().toString());
               // uploadFile(result.getUri().toString());
                 dialog = new Dialog(Camera.this);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.uploading);
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.dimAmount = 0.85f;
                Window window = dialog.getWindow();
                window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();


            }
        }
    }


    @Override
    public void openCropActivity(File file) {
        CropImage.activity(Uri.fromFile(file))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Camera.this);

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }

    @Override
    public void results(boolean result) {
        if(result)
        {

            isUploadDone=true;
            if(isUploadDone) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                // setResult(200);
            }

            dialog.cancel();

        }else{
            dialog.cancel();
            isUploadDone=false;

        }
    }
}
