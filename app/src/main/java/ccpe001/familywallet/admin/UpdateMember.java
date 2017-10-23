package ccpe001.familywallet.admin;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import ccpe001.familywallet.CustomAlertDialogs;
import ccpe001.familywallet.R;
import ccpe001.familywallet.Validate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateMember extends Fragment {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private FloatingActionButton imageButton;
    private Button signUpButton,changePw;
    private EditText fnameTxt,lnameTxt;
    private final static int GALLERY_PERMIT = 0;
    private final static int CAMERA_PERMIT = 11;

    private static final int RQ_CAPTURE = 4;
    private static final int RQ_GALLERY_REQUEST = 5;
    private RoundedBitmapDrawable round;
    private Uri sendProPicURI;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private CustomAlertDialogs alert;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_member, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        imageButton = (FloatingActionButton) v.findViewById(R.id.imageButton);
        changePw = (Button) v.findViewById(R.id.changePw);
        signUpButton = (Button) v.findViewById(R.id.signUPGetInfo);
        fnameTxt = (EditText) v.findViewById(R.id.editText);
        lnameTxt = (EditText) v.findViewById(R.id.editText4);
        if(mAuth.getCurrentUser().getProviders().toString().equals("[password]")) {
            changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(getActivity());
                View alertDiaView = inflater.inflate(R.layout.change_password,null);
                nameBuilder.setView(alertDiaView)
                        .setPositiveButton(R.string.change, null)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).create();



                final EditText passwordTxt1 = (EditText) alertDiaView.findViewById(R.id.passwordTxt1);
                final EditText passwordTxt2 =(EditText) alertDiaView.findViewById(R.id.passwordTxt2);


                nameBuilder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if(Validate.anyValidPass(passwordTxt2.getText().toString())){
                            if(Validate.anyValidPass(passwordTxt1.getText().toString())){
                                if(passwordTxt1.getText().toString().equals(passwordTxt2.getText().toString())){
                                    Log.d("sd",passwordTxt2.getText().toString());
                                        mAuth.getCurrentUser().updatePassword(passwordTxt2.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                alert = new CustomAlertDialogs();
                                                if (task.isSuccessful()) {
                                                    alert.initCommonDialogPage(getActivity(), getString(R.string.pw_changed), false).show();
                                                } else {
                                                    alert.initCommonDialogPage(getActivity(), getString(R.string.common_error), true).show();
                                                }
                                            }
                                        });
                                }else{
                                    passwordTxt2.setError(getString(R.string.pw_not_match));
                                }
                            }else{
                                passwordTxt1.setError(getString(R.string.signup_onclick_passerr));
                            }
                        }else{
                            passwordTxt2.setError(getString(R.string.signup_onclick_passerr));
                        }

                    }
                });


                nameBuilder.show();
            }
            });
        }else{
            alert = new CustomAlertDialogs();
            alert.initCommonDialogPage(getActivity(), getString(R.string.pw_change_error), true).show();
            changePw.setEnabled(false);
        }
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validate.ContainOnlyLetters(fnameTxt.getText().toString())) {
                    if(Validate.ContainOnlyLetters(lnameTxt.getText().toString())) {
                        CustomAlertDialogs alert = new CustomAlertDialogs();
                        alert.initLoadingPage(getActivity());
                        saveData();
                        uploadImg();
                        storeUpdatedUserInfoStatus(getActivity(),true);//set this true once user update his details
                        startActivity(new Intent("ccpe001.familywallet.DASHBOARD"));
                    }else{
                        lnameTxt.setError(getString(R.string.getinfo_lastname_errmsg));
                    }
                }else {
                    fnameTxt.setError(getString(R.string.getinfo_firstname_errmsg));
                }
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] optArr ={getString(R.string.getinfo_cameraopt1),getString(R.string.getinfo_cameraopt2)};
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.getinfo_cameraopt_settitle);
                builder.setItems(optArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int opt) {
                        if(opt==0){
                            final String[] CAMERAPERMARR = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            if (!CustomAlertDialogs.hasPermissions(getActivity(), CAMERAPERMARR)) {
                                alert = new CustomAlertDialogs();
                                alert.initPermissionPage(getActivity(),getString(R.string.permit_only_camera)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        ActivityCompat.requestPermissions(getActivity(),CAMERAPERMARR,CAMERA_PERMIT);
                                    }
                                }).show();

                            }else {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, RQ_CAPTURE);
                            }
                        }else if(opt==1){
                            final String[] GALLERYPERMARR = {Manifest.permission.READ_EXTERNAL_STORAGE};
                            if(!CustomAlertDialogs.hasPermissions(getActivity(), GALLERYPERMARR)){
                                alert = new CustomAlertDialogs();
                                alert.initPermissionPage(getActivity(),getString(R.string.permit_only_read)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        ActivityCompat.requestPermissions(getActivity(),GALLERYPERMARR,GALLERY_PERMIT);
                                    }
                                }).show();

                            }else {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, RQ_GALLERY_REQUEST);
                            }


                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        if(!new GetInfo().hasCamera(getActivity())){
            imageButton.setEnabled(false);
        }
    }


    private void saveData() {
        UserData userData = new UserData(fnameTxt.getText().toString().trim(),
                lnameTxt.getText().toString().trim(),
                mAuth.getCurrentUser().getUid(),"Storage");
        databaseReference.child("UserInfo").child(mAuth.getCurrentUser().getUid()).setValue(userData);
    }

    private void uploadImg(){

        if(sendProPicURI != null) {
            StorageReference reference = storageReference.child("UserPics/"+mAuth.getCurrentUser().getUid()+".jpg");
            reference.putFile(sendProPicURI);
        }//no image selected part
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == GALLERY_PERMIT){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RQ_GALLERY_REQUEST);
            }else {
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(getActivity(),getString(R.string.error_permitting),true).show();;
            }
        }else if(requestCode == CAMERA_PERMIT) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, RQ_CAPTURE);
            }else {
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(getActivity(),getString(R.string.error_permitting),true).show();;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==RQ_GALLERY_REQUEST&&resultCode==RESULT_OK){
            sendProPicURI = data.getData();

            try {
                round = RoundedBitmapDrawableFactory.create(getResources(), MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),sendProPicURI));
            } catch (IOException e) {
                e.printStackTrace();
            }
            round.setCircular(true);
            imageButton.setImageDrawable(round);
        }else if(requestCode==RQ_CAPTURE&&resultCode==RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            sendProPicURI = new GetInfo().getImageUri(getActivity(),photo);

            round = RoundedBitmapDrawableFactory.create(getResources(), photo);
            round.setCircular(true);
            imageButton.setImageDrawable(round);
        }
    }

    public void storeUpdatedUserInfoStatus(Context c, boolean b){
        prefs = c.getSharedPreferences("App Settings",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putBoolean("isUpdated",b);
        editor.commit();
    }

    public static boolean isUpdatedUserInfo(Context c){
        SharedPreferences prefs = c.getSharedPreferences("App Settings",Context.MODE_PRIVATE);
        return prefs.getBoolean("isUpdated",false);
    }

}
