package ccpe001.familywallet;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Knight on 8/25/2017.
 */

public class Translate {

    /** get the current date according to the format used*/
    public String getCurrentDate(){

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        String currentDate = dateFormat.format(cal.getTime());
        return currentDate;
    }

    /* Method to get category name in english to save in database  */
    public String categoryToDB(String cat) {
        String retCat = "";
        switch (cat) {
            case "Car":
            case "මෝටර් රථ":
                retCat = "Car";
                break;
            case "Travel":
            case "ගමන්":
                retCat = "Travel";
                break;
            case "Food & Drinks":
            case "ආහාර පාන":
                retCat = "Food & Drinks";
                break;
            case "Transportation":
            case "ප්රවාහනයයි":
                retCat = "Transportation";
                break;
            case "Gifts":
            case "තෑගි":
                retCat = "Gifts";
                break;
            case "Bill":
            case "බිල්":
                retCat = "Bill";
                break;
            case "Entertainment":
            case "විනෝදාස්වාදය":
                retCat = "Entertainment";
                break;
            case "Home":
            case "ගෙදර":
                retCat = "Home";
                break;
            case "Utilities":
            case "උපයෝගිතා":
                retCat = "Utilities";
                break;
            case "Shopping":
            case "සාප්පු සවාරි":
                retCat = "Shopping";
                break;
            case "Accommodation":
            case "නවාතැන්":
                retCat = "Accommodation";
                break;
            case "Healthcare":
            case "සෞඛ්ය සත්කාර":
                retCat = "Healthcare";
                break;
            case "Clothing":
            case "ඇඳුම්":
                retCat = "Clothing";
                break;
            case "Groceries":
            case "සිල්ලර බඩු":
                retCat = "Groceries";
                break;
            case "Drinks":
            case "බීම":
                retCat = "Drinks";
                break;
            case "Pets":
            case "සුරතල් සතුන්":
                retCat = "Pets";
                break;
            case "Education":
            case "අධ්යාපන":
                retCat = "Education";
                break;
            case "Cinema":
            case "චිත්රපට":
                retCat = "Cinema";
                break;
            case "Kids":
            case "දරුවන්":
                retCat = "Kids";
                break;
            case "Loan":
            case "ණය":
                retCat = "Loan";
                break;
            case "Business":
            case "ව්යාපාර":
                retCat = "Business";
                break;
            case "Salary":
            case "වැටුප්":
                retCat = "Salary";
                break;
            case "Extra Income":
            case "අමතර ආදායම":
                retCat = "Extra Income";
                break;
            case "Other":
            case "වෙනත්":
                retCat = "Other";
                break;
        }
        return retCat;
    }

    /* Method to get category name in selected language */
    public String categoryView(String cat, Context con) {
        Resources res = con.getResources();
        String[] iArr = res.getStringArray(R.array.IncomeCategory);
        String[] eArr = res.getStringArray(R.array.ExpenseCategory);
        String retCat = "";
        switch (cat) {
            case "Car":
                retCat = iArr[0];
                break;
            case "Travel":
                retCat = iArr[1];
                break;
            case "Food & Drinks":
                retCat = iArr[2];
                break;
            case "Transportation":
                retCat = iArr[3];
                break;
            case "Gifts":
                retCat = iArr[4];
                break;
            case "Bill":
                retCat = iArr[5];
                break;
            case "Entertainment":
                retCat = iArr[6];
                break;
            case "Home":
                retCat = iArr[7];
                break;
            case "Utilities":
                retCat = iArr[8];
                break;
            case "Shopping":
                retCat = iArr[9];
                break;
            case "Accommodation":
                retCat = iArr[10];
                break;
            case "Healthcare":
                retCat = iArr[11];
                break;
            case "Clothing":
                retCat = iArr[12];
                break;
            case "Groceries":
                retCat = iArr[13];
                break;
            case "Drinks":
                retCat = iArr[14];
                break;
            case "Pets":
                retCat = iArr[15];
                break;
            case "Education":
                retCat = iArr[16];
                break;
            case "Cinema":
                retCat = iArr[17];
                break;
            case "Kids":
                retCat = iArr[18];
                break;
            case "Loan":
                retCat = eArr[0];
                break;
            case "Business":
                retCat = eArr[1];
                break;
            case "Salary":
                retCat = eArr[3];
                break;
            case "Extra Income":
                retCat = eArr[4];
                break;
            case "Other":
                retCat = iArr[19];
                break;
        }
        return retCat;
    }

    /* Method to get time to english to save in database  */
    public String timeToDB(String time) {
        String[] parts = time.split(":");
        String ampm = parts[1].substring(3);
        String ampmAfter = "";
        switch (ampm) {
            case "AM":
            case "පෙ.ව.":
                ampmAfter = "AM";
                break;
            case "PM":
            case "ප.ව.":
                ampmAfter = "PM";
                break;
        }
        return parts[0] + ":" + parts[1].substring(0, 3) + ampmAfter;

    }

    /* Method to get time in selected language */
    public String timeView(String time, Context con) {
        Resources res = con.getResources();
        String[] parts = time.split(":");
        String ampm = parts[1].substring(3);
        String ampmAfter = "";
        switch (ampm) {
            case "AM":
                ampmAfter = res.getString(R.string.tAM);
                break;
            case "PM":
                ampmAfter = res.getString(R.string.tPM);
                break;
        }
        return parts[0] + ":" + parts[1].substring(0, 3) + ampmAfter;

    }

    /* Method to convert date and time in to all digit format ie- "2017-Jan-11 and 11.24pm" --> "201701112324"  */
    public static String dateToDB(String date, String time) {
        String[] parts = date.split("-");
        String month = parts[1];
        String day = parts[0];
        String year = parts[2];
        String monthNum = "";

        String[] timeParts = time.split(":");
        String ampm = timeParts[1].substring(3);
        String ampmAfter = "";
        String rethours = "";
        int hour = 0;
        switch (ampm) {
            case "AM":
            case "පෙ.ව.":
                ampmAfter = "AM";
                break;
            case "PM":
            case "ප.ව.":
                ampmAfter = "PM";
                break;
        }
        int hourCheck = Integer.parseInt(timeParts[0]);
        if (ampmAfter.equals("PM")) {
            hour = Integer.parseInt(timeParts[0]);
            if (hour != 12) {
                rethours = Integer.toString(hour + 12);
            } else {
                rethours = "12";
            }
        } else if (ampmAfter.equals("AM")) {
            if (timeParts[0].equals("12")) {
                rethours = "00";
            }
            else if(hourCheck<10){
                rethours="0"+String.valueOf(hourCheck);
            }
            else {
                rethours = timeParts[0];
            }

        }
        if (Integer.parseInt(day) < 10) {
            day = "0" + Integer.parseInt(day);
        }

        switch (month) {
            case "Jan":
            case "ජන":
                monthNum = "01";
                break;
            case "Feb":
            case "පෙබ":
                monthNum = "02";
                break;
            case "Mar":
            case "මාර්":
                monthNum = "03";
                break;
            case "Apr":
            case "අප්\u200Dරේල්":
                monthNum = "04";
                break;
            case "May":
            case "මැයි":
                monthNum = "05";
                break;
            case "Jun":
            case "ජුනි":
                monthNum = "06";
                break;
            case "Jul":
            case "ජූලි":
                monthNum = "07";
                break;
            case "Aug":
            case "අගෝ":
                monthNum = "08";
                break;
            case "Sep":
            case "සැප්":
                monthNum = "09";
                break;
            case "Oct":
            case "ඔක්":
                monthNum = "10";
                break;
            case "Nov":
            case "නොවැ":
                monthNum = "11";
                break;
            case "Dec":
            case "දෙසැ":
                monthNum = "12";
                break;

        }
        return year + monthNum + day + rethours + timeParts[1].substring(0, 2);
    }

    /* Method to get date from all digit format(date and time) to readable format in language ie- "20170111" --> "2017-Jan-11" */
    public static String dateView(String date, Context con) {
        Resources res = con.getResources();
        String[] monthList = res.getStringArray(R.array.Months);
        String monthNum = date.substring(4, 6);
        String day = date.substring(6, 8);//0
        String year = date.substring(0, 4);
        String month = "";
        switch (monthNum) {
            case "01":
                month = monthList[0];
                break;
            case "02":
                month = monthList[1];
                break;
            case "03":
                month = monthList[2];
                break;
            case "04":
                month = monthList[3];
                break;
            case "05":
                month = monthList[4];
                break;
            case "06":
                month = monthList[5];
                break;
            case "07":
                month = monthList[6];
                break;
            case "08":
                month = monthList[7];
                break;
            case "09":
                month = monthList[8];
                break;
            case "10":
                month = monthList[9];
                break;
            case "11":
                month = monthList[10];
                break;
            case "12":
                month = monthList[11];
                break;

        }
        return day + "-" + month + "-" + year;
    }

    /* Method to convert date in to all digit format ie- "2017-Jan-11" --> "20170111"*/
    public static String dateToValue(String date) {
        Log.i("helloWW1",date);
        String[] parts = date.split("-");
        String month = parts[1];
        String strDay = parts[0];
        String year = parts[2];
        String monthNum = "";
        switch (month) {
            case "Jan":
            case "ජන":
                monthNum = "01";
                break;
            case "Feb":
            case "පෙබ":
                monthNum = "02";
                break;
            case "Mar":
            case "මාර්":
                monthNum = "03";
                break;
            case "Apr":
            case "අප්\u200Dරේල්":
                monthNum = "04";
                break;
            case "May":
            case "මැයි":
                monthNum = "05";
                break;
            case "Jun":
            case "ජුනි":
                monthNum = "06";
                break;
            case "Jul":
            case "ජූලි":
                monthNum = "07";
                break;
            case "Aug":
            case "අගෝ":
                monthNum = "08";
                break;
            case "Sep":
            case "සැප්":
                monthNum = "09";
                break;
            case "Oct":
            case "ඔක්":
                monthNum = "10";
                break;
            case "Nov":
            case "නොවැ":
                monthNum = "11";
                break;
            case "Dec":
            case "දෙසැ":
                monthNum = "12";
                break;

        }
        String retDay;
        Integer intDay = Integer.parseInt(strDay);
        if (intDay < 10){
            retDay="0"+intDay;
        }
        else {
            retDay=String.valueOf(intDay);
        }
        return year + monthNum + retDay;
    }

    /* Method to get date day nad month to double digits day =6th > 06 */
    public static String dateWithDoubleDigit(int year, int inputMonth, int day, Context con) {
        String month = null;
        Resources res = con.getResources();
        String[] monthList = res.getStringArray(R.array.Months);
        String retDay;/*Converting the day into a double digit value if its not*/

        if (day < 10){
            retDay="0"+day;
        }
        else {
            retDay=String.valueOf(day);
        }
        String convMonth;/*Converting the month into a double digit value if its not*/
        if (inputMonth < 10){
            convMonth="0"+ inputMonth;
        }
        else {
            convMonth=String.valueOf(inputMonth);
        }
        switch (convMonth) {
            case "01":
                month = monthList[0];
                break;
            case "02":
                month = monthList[1];
                break;
            case "03":
                month = monthList[2];
                break;
            case "04":
                month = monthList[3];
                break;
            case "05":
                month = monthList[4];
                break;
            case "06":
                month = monthList[5];
                break;
            case "07":
                month = monthList[6];
                break;
            case "08":
                month = monthList[7];
                break;
            case "09":
                month = monthList[8];
                break;
            case "10":
                month = monthList[9];
                break;
            case "11":
                month = monthList[10];
                break;
            case "12":
                month = monthList[11];
                break;

        }
        return day + "-" + month + "-" + year;
    }

    /* Method to get date from all digit format(date) to readable format in language ie- "20170111" --> "2017-Jan-11" */
    public static String valueToDate(String date, Context con) {
        Resources res = con.getResources();
        String[] monthList = res.getStringArray(R.array.Months);
        String monthNum = date.substring(4, 6);
        String day = date.substring(6);
        String year = date.substring(0, 4);
        String month = "";
        switch (monthNum) {
            case "01":
                month = monthList[0];
                break;
            case "02":
                month = monthList[1];
                break;
            case "03":
                month = monthList[2];
                break;
            case "04":
                month = monthList[3];
                break;
            case "05":
                month = monthList[4];
                break;
            case "06":
                month = monthList[5];
                break;
            case "07":
                month = monthList[6];
                break;
            case "08":
                month = monthList[7];
                break;
            case "09":
                month = monthList[8];
                break;
            case "10":
                month = monthList[9];
                break;
            case "11":
                month = monthList[10];
                break;
            case "12":
                month = monthList[11];
                break;

        }
        return day + "-" + month + "-" + year;
    }

    /* Method to get the currency name in english in database */
    public String currencyToDB(String cur) {

        String retCur = "";

        switch (cur) {
            case "LKR.":
            case "රු.":
                retCur = "LKR.";
                break;
            case "USD.":
            case "ඇ. ඩොලර්.":
                retCur = "USD.";
                break;
            case "EUR.":
            case "යුරෝ.":
                retCur = "EUR.";
                break;
            case "GBP.":
            case "පවුම්.":
                retCur = "GBP.";
                break;
            case "INR.":
            case "ඉන්දීය රු.":
                retCur = "INR.";
                break;
        }
        return retCur;
    }

    /* Method to get the currency name according to the selected language*/
    public String currencyView(String cur, Context con) {
        Resources res = con.getResources();
        String[] curArr = res.getStringArray(R.array.spinnerCurrency);
        String retCur = "";
        switch (cur) {
            case "LKR.":
                retCur = curArr[0];
                break;
            case "USD.":
                retCur = curArr[1];
                break;
            case "EUR.":
                retCur = curArr[2];
                break;
            case "GBP.":
                retCur = curArr[3];
                break;
            case "INR.":
                retCur = curArr[4];
                break;
        }
        return retCur;
    }

    /* Method to get the recurring period name in english in database */
    public String recurringToDB(String recur) {
        String retRecur = "";
        switch (recur) {
            case "Daily":
            case "දිනපතා":
                retRecur = "Daily";
                break;
            case "Weekly":
            case "සතිපතා":
                retRecur = "Weekly";
                break;
            case "Monthly":
            case "මාසිකව":
                retRecur = "Monthly";
                break;
            case "Annually":
            case "වාර්ෂිකව":
                retRecur = "Annually";
                break;
        }
        return retRecur;
    }

    /* Method to get the recurring period name according to the selected language*/
    public String recurringView(String recur, Context con) {
        Resources res = con.getResources();
        String[] recurList = res.getStringArray(R.array.spinnerRecurring);
        String retRecur = "";
        switch (recur) {
            case "Daily":
                retRecur = recurList[0];
                break;
            case "Weekly":
                retRecur = recurList[1];
                break;
            case "Monthly":
                retRecur = recurList[2];
                break;
            case "Annually":
                retRecur = recurList[3];
                break;
        }
        return retRecur;
    }

    /* Method to get the image id of the category name*/
    public int getCategoryID(String cat) {
        int catID = 0;
        switch (cat) {
            case "Car":
                catID = R.drawable.cat1;
                break;
            case "Travel":
                catID = R.drawable.cat2;
                break;
            case "Food & Drinks":
                catID = R.drawable.cat3;
                break;
            case "Transportation":
                catID = R.drawable.cat4;
                break;
            case "Gifts":
                catID = R.drawable.cat5;
                break;
            case "Bill":
                catID = R.drawable.cat6;
                break;
            case "Entertainment":
                catID = R.drawable.cat7;
                break;
            case "Home":
                catID = R.drawable.cat8;
                break;
            case "Utilities":
                catID = R.drawable.cat9;
                break;
            case "Shopping":
                catID = R.drawable.cat10;
                break;
            case "Accommodation":
                catID = R.drawable.cat11;
                break;
            case "Healthcare":
                catID = R.drawable.cat12;
                break;
            case "Clothing":
                catID = R.drawable.cat13;
                break;
            case "Groceries":
                catID = R.drawable.cat14;
                break;
            case "Drinks":
                catID = R.drawable.cat15;
                break;
            case "Pets":
                catID = R.drawable.cat16;
                break;
            case "Education":
                catID = R.drawable.cat17;
                break;
            case "Cinema":
                catID = R.drawable.cat18;
                break;
            case "Kids":
                catID = R.drawable.cat19;
                break;
            case "Loan":
                catID = R.drawable.cat100;
                break;
            case "Business":
                catID = R.drawable.cat101;
                break;
            case "Salary":
                catID = R.drawable.cat103;
                break;
            case "Extra Income":
                catID = R.drawable.cat104;
                break;
            case "Other":
                catID = R.drawable.cat_other;
                break;
        }
        return catID;
    }

    /* Method to get date from all digit format(date) to readable format in language ie- "20170111" --> "2017-Jan-11" */
    public String dateFormatter(String date, Context context, int opt) {
        String ret = null;
        String[] monthList = context.getResources().getStringArray(R.array.Months);
        String monthNum = date.substring(4, 6);
        String day = date.substring(6);
        String year = date.substring(0, 4);
        if (opt == 2) {
            String month = "";
            switch (monthNum) {
                case "01":
                    month = monthList[0];
                    break;
                case "02":
                    month = monthList[1];
                    break;
                case "03":
                    month = monthList[2];
                    break;
                case "04":
                    month = monthList[3];
                    break;
                case "05":
                    month = monthList[4];
                    break;
                case "06":
                    month = monthList[5];
                    break;
                case "07":
                    month = monthList[6];
                    break;
                case "08":
                    month = monthList[7];
                    break;
                case "09":
                    month = monthList[8];
                    break;
                case "10":
                    month = monthList[9];
                    break;
                case "11":
                    month = monthList[10];
                    break;
                case "12":
                    month = monthList[11];
                    break;
            }
            ret = month + " " + day + "," + year;
        } else if (opt == 0) {
            ret = year + "/" + day + "/" + monthNum;
        } else if (opt == 1) {
            ret = day + "/" + monthNum + "/" + year;
        }
        return ret;
    }

    public String currencyFormatter(Context context, int opt) {
        String ret = null;
        String[] currList = context.getResources().getStringArray(R.array.spinnerCurrency);
        if(opt == 0) {
            ret = currList[0];
        }else if (opt == 1) {
            ret = currList[1];
        }else if (opt == 2) {
            ret = currList[2];
        }else if (opt == 3) {
            ret = currList[3];
        }else if (opt == 4) {
            ret = currList[4];
        }
        return ret;
    }
}
