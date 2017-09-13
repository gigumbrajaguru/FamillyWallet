package ccpe001.familywallet;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import ccpe001.familywallet.admin.GetInfo;
import ccpe001.familywallet.admin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static java.security.AccessController.getContext;


public class CustomAlertDialogs {

    private AVLoadingIndicatorView avi;
    private TextView txtVw,txtTitle;
    private ImageView bgBVw,iconvVw,lblVw;
    private AlertDialog alertDialog;

    /*USE this FOR PASSING CONTEXT*/

    /*pass the context*/
    public void initLoadingPage(Context c) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(c);
        View alertDiaView = inflater.inflate(R.layout.activity_loading_page,null);
        nameBuilder.setView(alertDiaView);

        avi = (AVLoadingIndicatorView) alertDiaView.findViewById(R.id.avi);
        avi.smoothToShow();


        int val= (int) (130 * Resources.getSystem().getDisplayMetrics().density);//dp to px
        alertDialog= nameBuilder.show();
        alertDialog.getWindow().setLayout(val ,val);


    }

    public void hideLoadingPage(){
        alertDialog.dismiss();
        avi.smoothToHide();
    }

    /*pass the context, msg description, and whther if it is a error or not*/
    public void initCommonDialogPage(Context c,String str,Boolean isError){
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(c);
        View alertDiaView = inflater.inflate(R.layout.common_dialog_box,null);
        nameBuilder.setView(alertDiaView);

        txtVw = (TextView) alertDiaView.findViewById(R.id.textView27);
        bgBVw = (ImageView) alertDiaView.findViewById(R.id.bgBVw);
        iconvVw = (ImageView) alertDiaView.findViewById(R.id.iconvVw);
        txtTitle =(TextView) alertDiaView.findViewById(R.id.txtTitle);



        if(isError) {
            txtTitle.setText(R.string.error_dialogbox_title);
            txtVw.setText(str);
            iconvVw.setImageDrawable(c.getResources().getDrawable(R.drawable.icons8_error));
            nameBuilder.setPositiveButton(R.string.customaletdialog_initCommonDialogPage_posbtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        }

        if(!isError){
            txtTitle.setText(R.string.common_sucessful);
            txtVw.setText(str);
            iconvVw.setImageDrawable(c.getResources().getDrawable(R.drawable.icons8_ok));
        }

        nameBuilder.setNegativeButton(R.string.customaletdialog_initCommonDialogPage_negbtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });



        nameBuilder.show();
    }

    /*pass the contect when asking for permissions,permission desc for str*/
    public AlertDialog.Builder initPermissionPage(Context c,String str) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(c);
        View alertDiaView = inflater.inflate(R.layout.get_permission_layout,null);
        nameBuilder.setView(alertDiaView);

        txtVw = (TextView) alertDiaView.findViewById(R.id.textView27);
        lblVw = (ImageView) alertDiaView.findViewById(R.id.bgBVw);
        txtVw.setText(str);
        lblVw.setImageResource(R.drawable.permit_dialog);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lblVw.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lblVw.setLayoutParams(lp);

        nameBuilder.setNegativeButton(R.string.customaletdialog_initPermissionPage_negbtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return nameBuilder;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
