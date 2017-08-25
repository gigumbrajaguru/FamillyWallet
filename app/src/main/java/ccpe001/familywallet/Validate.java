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

    public static String dateToValue(String date) {
        String month = date.substring(3, 6);
        String day = date.substring(0, 2);
        String year = date.substring(7);
        String monthNum = "";
        switch (month) {
            case "Jan":case "ජන":monthNum = "01";break;
            case "Feb":case "පෙබ":monthNum = "02";break;
            case "Mar":case "මාර්":monthNum = "03";break;
            case "Apr":case "අප්\u200Dරේල්":monthNum = "04";break;
            case "May":case "මැයි":monthNum = "05";break;
            case "Jun":case "ජුනි":monthNum = "06";break;
            case "Jul":case "ජූලි":monthNum = "07";break;
            case "Aug":case "අගෝ":monthNum = "08";break;
            case "Sep":case "සැප්":monthNum = "09";break;
            case "Oct":case "ඔක්":monthNum = "10";break;
            case "Nov":case "නොවැ":monthNum = "11";break;
            case "Dec":case "දෙසැ":monthNum = "12";break;

        }
        return year + monthNum + day;
    }

    public static String valueToDate(String date, Context con){
        Resources res = con.getResources();
        String monthNum = date.substring(4,6);
        String day = date.substring(6);
        String year = date.substring(0,4);
        String month="";
        switch (monthNum){
            case "01":month=res.getString(R.string.mJan);break;
            case "02":month=res.getString(R.string.mFeb);break;
            case "03":month=res.getString(R.string.mMar);break;
            case "04":month=res.getString(R.string.mApr);break;
            case "05":month=res.getString(R.string.mMay);break;
            case "06":month=res.getString(R.string.mJun);break;
            case "07":month=res.getString(R.string.mJul);break;
            case "08":month=res.getString(R.string.mAug);break;
            case "09":month=res.getString(R.string.mSep);break;
            case "10":month=res.getString(R.string.mOct);break;
            case "11":month=res.getString(R.string.mNov);break;
            case "12":month=res.getString(R.string.mDec);break;

        }
        return day+"-"+month+"-"+year;
    }

}

