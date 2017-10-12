package ccpe001.familywallet;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Editable;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
        InternetAddress internetAddress = null;
        try {
            internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    //Only letters
    public static boolean ContainOnlyLetters(String name) {
        return name.matches("[a-zA-Z]+");
    }

    /**
     *
     * @param inputDate
     * @return
     */
    public static boolean validFutureDate(String inputDate){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date();
        String CurrentDate = dateFormat.format(date);

        int difference = (inputDate.compareTo(CurrentDate));

        if (difference<0) {
            return false;
        }
        else
            return true;
    }   //Check whether if the date is in the future

    /**
     *
     * @param inputDate
     * @return
     */
    public static boolean validPastDate(String inputDate){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date= new Date();
        String CurrentDate = dateFormat.format(date);

        int difference = (CurrentDate.compareTo(inputDate));

        if (difference>=0) {
            return true;
        }
        else
            return false;
    }

}

