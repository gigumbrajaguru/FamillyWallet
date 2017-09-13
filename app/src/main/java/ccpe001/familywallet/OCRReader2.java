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

        snackbar = Snackbar
                .make(layout, R.string.ocrreader_snackbar_takepic, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ocrreader_snackbar_takepicbtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth = FirebaseAuth.getInstance();
                        storageReference = FirebaseStorage.getInstance().getReference().child("ScannedBills").child(mAuth.getCurrentUser().getUid());
                        final String[] CROPCAMPERARR = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
                        if(!CustomAlertDialogs.hasPermissions(OCRReader2.this,CROPCAMPERARR)){
                            alert = new CustomAlertDialogs();
                            alert.initPermissionPage(OCRReader2.this,getString(R.string.permit_only_camera)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(OCRReader2.this,CROPCAMPERARR,CROP_CAM);
                                }
                            }).show();
                        }else {
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(OCRReader2.this);
                        }
                    }
                });
        snackbar.show();

        camera_view = (SurfaceView) findViewById(R.id.camera_view);
        bill_data = (TextView)  findViewById(R.id.bill_data);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(false)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        alert = new CustomAlertDialogs();

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                billImageUri = result.getUri();
                cropImageView.setImageURI(billImageUri);
                StorageReference referenceTransId = storageReference.child("putTransIDhere"+".jpg");
                referenceTransId.putFile(billImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),R.string.ocrreader_onactivityresult_uploaddone,Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alert.initCommonDialogPage(OCRReader2.this,getString(R.string.common_error),true);                    }
                });

            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                alert.initCommonDialogPage(OCRReader2.this,getString(R.string.common_error),true);
            }
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
        }else if(requestCode == CROP_CAM){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1]==PackageManager.PERMISSION_GRANTED||grantResults[2]==PackageManager.PERMISSION_GRANTED){
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(OCRReader2.this);
            }else {
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(OCRReader2.this,getString(R.string.error_permitting),true);
            }
        }
    }

}

