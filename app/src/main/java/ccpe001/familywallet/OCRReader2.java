package ccpe001.familywallet;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

/**
 * Created by harithaperera on 7/31/17.
 */
public class OCRReader2 extends AppCompatActivity {


    private SurfaceView camera_view;
    private TextView bill_data;
    private ImageView cropImageView;
    private CameraSource cameraSource;
    private static final int GENARAL_CAM = 3;
    private static final int CROP_CAM = 4;

    private StorageReference storageReference;
    private Uri billImageUri;
    private FirebaseAuth mAuth;
    private RelativeLayout layout;
    private Snackbar snackbar;
    private CustomAlertDialogs alert;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.ocrreader2_setttile);
        setContentView(R.layout.ocrreader);
        cropImageView = (ImageView) findViewById(R.id.cropImageView);
        layout = (RelativeLayout) findViewById(R.id.layout);


        camera_view = (SurfaceView) findViewById(R.id.camera_view);
        bill_data = (TextView)  findViewById(R.id.bill_data);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            camera_view.getHolder().addCallback(new SurfaceHolder.Callback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    final String[] CAMPERARR = {Manifest.permission.CAMERA};
                    if (!CustomAlertDialogs.hasPermissions(OCRReader2.this,CAMPERARR)) {
                        alert = new CustomAlertDialogs();
                        alert.initPermissionPage(OCRReader2.this,getString(R.string.permit_only_camera)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                requestPermissions(CAMPERARR,GENARAL_CAM);
                            }
                        }).show();
                    }else {
                        try {
                            checkSelfPermission(Manifest.permission.CAMERA);
                            cameraSource.start(camera_view.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
                });


            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(final Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();

                    if(items.size()!= 0){
                        bill_data.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder str = new StringBuilder();

                                for(int i=0;i<items.size();i++) {
                                    Log.d("fdf", "fd");

                                    TextBlock item = items.valueAt(i);
                                    str.append(item.getValue());
                                    str.append("\n");
                                }

                                bill_data.setText(str);
                            }
                        });
                    }
                }
            });
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==GENARAL_CAM){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                try {
                    checkSelfPermission(Manifest.permission.CAMERA);
                    cameraSource.start(camera_view.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(OCRReader2.this,getString(R.string.error_permitting),true);
            }
        }
    }

}

