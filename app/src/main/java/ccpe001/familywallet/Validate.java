package ccpe001.familywallet;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Editable;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harithaperera on 5/22/17.
 */
public class Validate {

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean fileValidate(Editable text) {
        if (!text.toString().isEmpty()) {
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean anyValidPass(String pw) {
        if (!pw.isEmpty()) {
            if (pw.length() > 6) {
                return true;
            }
        }
        return false;
    }

    public static boolean anyValidMail(String email) { //email
        String str = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Only letters
    public static boolean ContainOnlyLetters(String name) {
        return name.matches("[a-zA-Z]+");
    }



}

