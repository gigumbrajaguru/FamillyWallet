package ccpe001.familywallet;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
public class OCRReader extends AppCompatActivity {


    private SurfaceView camera_view;
    private TextView bill_data;
    private ImageView cropImageView;
    private CameraSource cameraSource;
    private static final int CAMERA_PERMIT = 55;
    private static final int EXTERNAL_READ_PERMIT = 3;
    private static final int EXTERNAL_WRITE_PERMIT = 4;
    private StorageReference storageReference;
    private Uri billImageUri;
    private FirebaseAuth mAuth;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocrreader);
        cropImageView = (ImageView) findViewById(R.id.cropImageView);

        if(!checkPermit()){
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},EXTERNAL_READ_PERMIT);
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_WRITE_PERMIT);
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMIT);
        }else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("ScannedBills").child(mAuth.getCurrentUser().getUid());

        /*camera_view = (SurfaceView) findViewById(R.id.camera_view);
        bill_data = (TextView)  findViewById(R.id.bill_data);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            Log.d("fdf","fd");
            camera_view.getHolder().addCallback(new SurfaceHolder.Callback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMIT);
                        }
                        cameraSource.start(camera_view.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
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
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size()!= 0){
                        bill_data.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder str = new StringBuilder();
                                for(int i=0;i<items.size();i++) {
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
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        Toast.makeText(getApplicationContext(),R.string.common_error,Toast.LENGTH_SHORT).show();
                    }
                });

            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this,R.string.common_error,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if(requestCode==CAMERA_PERMIT){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMIT);
                }
                try {
                    cameraSource.start(camera_view.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
        if(requestCode == EXTERNAL_READ_PERMIT||requestCode == EXTERNAL_WRITE_PERMIT||requestCode==CAMERA_PERMIT){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1]==PackageManager.PERMISSION_GRANTED||grantResults[2]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),R.string.permitgranted,Toast.LENGTH_SHORT).show();
            }else {
                checkPermit();
            }
        }
    }



    protected  boolean checkPermit(){
        return ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
