package ccpe001.familywallet;

import android.annotation.TargetApi;
import android.content.Context;
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
            case "Jan":monthNum = "01";break;
            case "Feb":monthNum = "02";break;
            case "Mar":monthNum = "03";break;
            case "Apr":monthNum = "04";break;
            case "May":monthNum = "05";break;
            case "Jun":monthNum = "06";break;
            case "Jul":monthNum = "07";break;
            case "Aug":monthNum = "08";break;
            case "Sep":monthNum = "09";break;
            case "Oct":monthNum = "10";break;
            case "Nov":monthNum = "11";break;
            case "Dec":monthNum = "12";break;

        }
        return year + monthNum + day;
    }

    public static String valueToDate(String date){
        String monthNum = date.substring(4,6);
        String day = date.substring(6);
        String year = date.substring(0,4);
        String month="";
        switch (monthNum){
            case "01":month="Jan";break;
            case "02":month="Feb";break;
            case "03":month="Mar";break;
            case "04":month="Apr";break;
            case "05":month="May";break;
            case "06":month="Jun";break;
            case "07":month="Jul";break;
            case "08":month="Aug";break;
            case "09":month="Sep";break;
            case "10":month="Oct";break;
            case "11":month="Nov";break;
            case "12":month="Dec";break;

        }
        return day+"-"+month+"-"+year;
    }

}

