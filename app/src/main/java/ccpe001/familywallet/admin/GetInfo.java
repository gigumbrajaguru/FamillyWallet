package ccpe001.familywallet.admin;import android.Manifest;import android.app.ProgressDialog;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.SharedPreferences;import android.content.pm.PackageManager;import android.content.res.Resources;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.graphics.drawable.BitmapDrawable;import android.graphics.drawable.Drawable;import android.media.Image;import android.net.Uri;import android.os.Build;import android.os.Bundle;import android.provider.MediaStore;import android.support.annotation.NonNull;import android.support.annotation.RequiresApi;import android.support.design.widget.FloatingActionButton;import android.support.v4.app.ActivityCompat;import android.support.v4.content.ContextCompat;import android.support.v4.graphics.drawable.RoundedBitmapDrawable;import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;import android.support.v7.app.AlertDialog;import android.support.v7.app.AppCompatActivity;import android.util.AttributeSet;import android.util.Base64;import android.util.Log;import android.view.View;import android.widget.*;import ccpe001.familywallet.CustomAlertDialogs;import ccpe001.familywallet.Dashboard;import ccpe001.familywallet.R;import ccpe001.familywallet.Validate;import com.github.orangegangsters.lollipin.lib.PinActivity;import com.google.android.gms.tasks.OnFailureListener;import com.google.android.gms.tasks.OnSuccessListener;import com.google.firebase.auth.FirebaseAuth;import com.google.firebase.auth.FirebaseUser;import com.google.firebase.database.DatabaseReference;import com.google.firebase.database.FirebaseDatabase;import com.google.firebase.storage.FirebaseStorage;import com.google.firebase.storage.OnProgressListener;import com.google.firebase.storage.StorageReference;import com.google.firebase.storage.UploadTask;import org.apache.poi.hpsf.Constants;import java.io.ByteArrayOutputStream;import java.io.File;import java.io.IOException;import java.util.Arrays;import static android.app.Activity.RESULT_OK;public class GetInfo extends PinActivity implements View.OnClickListener{  private FloatingActionButton imageButton;  private Button signUpButton;  private EditText fnameTxt,lnameTxt;    private final static int GALLERY_PERMIT = 0;    private final static int CAMERA_PERMIT = 11;  private static final int RQ_CAPTURE = 4;  private static final int RQ_GALLERY_REQUEST = 5;  private RoundedBitmapDrawable round;  private Uri sendProPicURI;  private DatabaseReference databaseReference;  private StorageReference storageReference;  private FirebaseAuth mAuth;  private CustomAlertDialogs alert;  @Override  protected void onCreate(Bundle savedInstanceState) {    super.onCreate(savedInstanceState);    setContentView(R.layout.get_info);    init();  }  private void init() {    setTitle(R.string.getinfo_title);    mAuth = FirebaseAuth.getInstance();      if(mAuth.getCurrentUser() == null){          finish();          Intent intent = new Intent(this,SignIn.class);          startActivity(intent);      }      databaseReference = FirebaseDatabase.getInstance().getReference();      storageReference = FirebaseStorage.getInstance().getReference();    imageButton = (FloatingActionButton) findViewById(R.id.imageButton);    signUpButton = (Button) findViewById(R.id.signUPGetInfo);    fnameTxt = (EditText) findViewById(R.id.editText);    lnameTxt = (EditText) findViewById(R.id.editText4);    signUpButton.setOnClickListener(this);    imageButton.setOnClickListener(this);    if(!hasCamera()){      imageButton.setEnabled(false);    }  }    @Override    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {        super.onRequestPermissionsResult(requestCode, permissions, grantResults);        if(requestCode == GALLERY_PERMIT){            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);                startActivityForResult(galleryIntent, RQ_GALLERY_REQUEST);            }else {                alert = new CustomAlertDialogs();                alert.initCommonDialogPage(GetInfo.this,getString(R.string.error_permitting),true).show();;            }        }else if(requestCode == CAMERA_PERMIT) {            if(grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED){                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);                startActivityForResult(cameraIntent, RQ_CAPTURE);            }else {                alert = new CustomAlertDialogs();                alert.initCommonDialogPage(GetInfo.this,getString(R.string.error_permitting),true).show();;            }        }    }  @Override  public void onClick(View view) {    if(view.getId()==R.id.imageButton){      String [] optArr ={getString(R.string.getinfo_cameraopt1),getString(R.string.getinfo_cameraopt2)};      final AlertDialog.Builder builder = new AlertDialog.Builder(this);      builder.setTitle(R.string.getinfo_cameraopt_settitle);      builder.setItems(optArr, new DialogInterface.OnClickListener() {        @Override        public void onClick(DialogInterface dialogInterface, int opt) {          if(opt==0){              final String[] CAMERAPERMARR = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};              if (!CustomAlertDialogs.hasPermissions(GetInfo.this, CAMERAPERMARR)) {                alert = new CustomAlertDialogs();                alert.initPermissionPage(GetInfo.this,getString(R.string.permit_only_camera)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {                    @Override                    public void onClick(DialogInterface dialog, int id) {                        dialog.dismiss();                        ActivityCompat.requestPermissions(GetInfo.this,CAMERAPERMARR,CAMERA_PERMIT);                    }                }).show();            }else {                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);                startActivityForResult(cameraIntent, RQ_CAPTURE);            }          }else if(opt==1){              final String[] GALLERYPERMARR = {Manifest.permission.READ_EXTERNAL_STORAGE};              if(!CustomAlertDialogs.hasPermissions(GetInfo.this, GALLERYPERMARR)){                  alert = new CustomAlertDialogs();                  alert.initPermissionPage(GetInfo.this,getString(R.string.permit_only_read)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {                      @Override                      public void onClick(DialogInterface dialog, int id) {                          dialog.dismiss();                          ActivityCompat.requestPermissions(GetInfo.this,GALLERYPERMARR,GALLERY_PERMIT);                      }                  }).show();              }else {                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);                startActivityForResult(galleryIntent, RQ_GALLERY_REQUEST);              }          }        }      });      AlertDialog alertDialog = builder.create();      alertDialog.show();    }else if(view.getId()==R.id.signUPGetInfo){      if(Validate.ContainOnlyLetters(fnameTxt.getText().toString())) {        if(Validate.ContainOnlyLetters(lnameTxt.getText().toString())) {            CustomAlertDialogs alert = new CustomAlertDialogs();            alert.initLoadingPage(this);            Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");            intent.putExtra("firstname",fnameTxt.getText().toString());            intent.putExtra("lastname",lnameTxt.getText().toString());            try {                intent.putExtra("profilepic", sendProPicURI.toString());            }catch (Exception e){            }            saveData();            uploadImg();            startActivity(intent);        }else{          lnameTxt.setError(getString(R.string.getinfo_lastname_errmsg));        }      }else {        fnameTxt.setError(getString(R.string.getinfo_firstname_errmsg));      }    }  }    private void saveData() {        UserData userData = new UserData(fnameTxt.getText().toString().trim(),                                            lnameTxt.getText().toString().trim(),                                        mAuth.getCurrentUser().getUid(),"Storage");        databaseReference.child("UserInfo").child(mAuth.getCurrentUser().getUid()).setValue(userData);    }    private void uploadImg(){        if(sendProPicURI != null) {            StorageReference reference = storageReference.child("UserPics/"+mAuth.getCurrentUser().getUid()+".jpg");            reference.putFile(sendProPicURI);        }//no image selected part    }    private boolean hasCamera() {    return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);  }  @Override  protected void onActivityResult(int requestCode, int resultCode, Intent data) {    if(requestCode==RQ_GALLERY_REQUEST&&resultCode==RESULT_OK){      sendProPicURI = data.getData();      try {        round = RoundedBitmapDrawableFactory.create(getResources(), MediaStore.Images.Media.getBitmap(this.getContentResolver(),sendProPicURI));      } catch (IOException e) {        e.printStackTrace();      }      round.setCircular(true);      imageButton.setImageDrawable(round);    }else if(requestCode==RQ_CAPTURE&&resultCode==RESULT_OK){        Bitmap photo = (Bitmap) data.getExtras().get("data");        sendProPicURI = getImageUri(getApplicationContext(),photo);        round = RoundedBitmapDrawableFactory.create(getResources(), photo);        round.setCircular(true);        imageButton.setImageDrawable(round);    }  }    private Uri getImageUri(Context inContext, Bitmap inImage) {        ByteArrayOutputStream bytes = new ByteArrayOutputStream();        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);        return Uri.parse(path);    }}