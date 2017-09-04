package ccpe001.familywallet;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ccpe001.familywallet.admin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.wang.avi.AVLoadingIndicatorView;

import static java.security.AccessController.getContext;

public class CustomAlertDialogs {

    private AVLoadingIndicatorView avi;
    private TextView txtVw,txtTitle;
    private ImageView bgBVw,iconvVw;


    public void initLoadingPage(Context c) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(c);
        View alertDiaView = inflater.inflate(R.layout.activity_loading_page,null);
        nameBuilder.setView(alertDiaView);

        avi = (AVLoadingIndicatorView) alertDiaView.findViewById(R.id.avi);
        avi.smoothToShow();


        int val= (int) (130 * Resources.getSystem().getDisplayMetrics().density);//dp to px
        AlertDialog dlg = nameBuilder.show();
        dlg.getWindow().setLayout(val ,val);

    }

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
            txtTitle.setText(R.string.common_error);

            nameBuilder.setPositiveButton(R.string.customaletdialog_initCommonDialogPage_posbtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });
        }

        nameBuilder.setNegativeButton(R.string.customaletdialog_initCommonDialogPage_negbtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });



        nameBuilder.show();
    }
}
