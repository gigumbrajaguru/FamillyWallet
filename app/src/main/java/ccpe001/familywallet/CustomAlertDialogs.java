package ccpe001.familywallet;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import ccpe001.familywallet.admin.SignIn;
import com.wang.avi.AVLoadingIndicatorView;

public class CustomAlertDialogs {

    private AVLoadingIndicatorView avi;


    public void initLoadingPage(Context c) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(c,R.style.D1NoTitleDim);
        View alertDiaView = inflater.inflate(R.layout.activity_loading_page,null);
        nameBuilder.setView(alertDiaView);

        avi = (AVLoadingIndicatorView) alertDiaView.findViewById(R.id.avi);
        avi.smoothToShow();

        nameBuilder.show();
    }

    public void initCommonErrorPage(Context c){

    }
}
