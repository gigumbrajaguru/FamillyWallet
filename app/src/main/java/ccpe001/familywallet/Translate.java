package ccpe001.familywallet;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.content.res.Resources;

/**
 * Created by Knight on 8/25/2017.
 */

public class Translate {

    public String categoryToDB(String cat){
        String retCat="";
        switch (cat){
            case"Car":case"මෝටර් රථ":retCat="Car";break;
            case"Travel":case"ගමන්":retCat="Travel";break;
            case"Food & Drinks":case"ආහාර පාන":retCat="Food & Drinks";break;
            case"Transportation":case"ප්රවාහනයයි":retCat="Transportation";break;
            case"Gifts":case"තෑගි":retCat="Gifts";break;
            case"Bill":case"බිල්":retCat="Bill";break;
            case"Entertainment":case"විනෝදාස්වාදය":retCat="Entertainment";break;
            case"Home":case"ගෙදර":retCat="Home";break;
            case"Utilities":case"උපයෝගිතා":retCat="Utilities";break;
            case"Shopping":case"සාප්පු සවාරි":retCat="Shopping";break;
            case"Accommodation":case"නවාතැන්":retCat="Accommodation";break;
            case"Healthcare":case "සෞඛ්ය සත්කාර":retCat="Healthcare";break;
            case"Clothing":case"ඇඳුම්":retCat="Clothing";break;
            case"Groceries":case"සිල්ලර බඩු":retCat="Groceries";break;
            case"Drinks":case"බීම":retCat="Drinks";break;
            case"Pets":case"සුරතල් සතුන්":retCat="Pets";break;
            case"Education":case"අධ්යාපන":retCat="Education";break;
            case"Cinema":case"චිත්රපට":retCat="Cinema";break;
            case"Kids":case"දරුවන්":retCat="Kids";break;
            case"Loan":case"ණය":retCat="Loan";break;
            case"Business":case"ව්යාපාර":retCat="Business";break;
            case"Salary":case"වැටුප්":retCat="Salary";break;
            case"Extra Income":case"අමතර ආදායම":retCat="Extra Income";break;
            case"Other":case"වෙනත්":retCat="Other";break;
        }
        return retCat;
    }

    public String categoryView(String cat, Context con){
        Resources res = con.getResources();
        String[] iArr = res.getStringArray(R.array.IncomeCategory);
        String[] eArr = res.getStringArray(R.array.ExpenseCategory);
        String retCat="";
        switch (cat){
            case"Car":retCat=iArr[0];break;
            case"Travel":retCat=iArr[1];break;
            case"Food & Drinks":retCat=iArr[2];break;
            case"Transportation":retCat=iArr[3];break;
            case"Gifts":retCat=iArr[4];break;
            case"Bill":retCat=iArr[5];break;
            case"Entertainment":retCat=iArr[6];break;
            case"Home":retCat=iArr[7];break;
            case"Utilities":retCat=iArr[8];break;
            case"Shopping":retCat=iArr[9];break;
            case"Accommodation":retCat=iArr[10];break;
            case"Healthcare":retCat=iArr[11];break;
            case"Clothing":retCat=iArr[12];break;
            case"Groceries":retCat=iArr[13];break;
            case"Drinks":retCat=iArr[14];break;
            case"Pets":retCat=iArr[15];break;
            case"Education":retCat=iArr[16];break;
            case"Cinema":retCat=iArr[17];break;
            case"Kids":retCat=iArr[18];break;
            case"Loan":retCat=eArr[0];break;
            case"Business":retCat=eArr[1];break;
            case"Salary":retCat=eArr[3];break;
            case"Extra Income":retCat=eArr[4];break;
            case"Other":retCat=iArr[19];break;
        }
        return retCat;
    }

    public String timeToDB(String time){
        String[] parts = time.split(":");
        String ampm = parts[1].substring(3);
        String ampmAfter="";
        switch (ampm){
            case "AM":case "පෙ.ව.":ampmAfter="AM";break;
            case "PM":case "ප.ව.":ampmAfter="PM";break;
        }
        return parts[0]+":"+parts[1].substring(0,3)+ampmAfter;

    }

    public String timeView(String time, Context con){
        Resources res = con.getResources();
        String[] parts = time.split(":");
        String ampm = parts[1].substring(3);
        String ampmAfter="";
        switch (ampm){
            case "AM":ampmAfter=res.getString(R.string.tAM);break;
            case "PM":ampmAfter=res.getString(R.string.tPM);break;
        }
        return parts[0]+":"+parts[1].substring(0,3)+ampmAfter;

    }

    public static String dateToDB(String date) {
        String[] parts = date.split("-");
        String month = parts[1];
        String day = parts[0];
        String year = parts[2];
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
        String[] monthList = res.getStringArray(R.array.Months);
        String monthNum = date.substring(4,6);
        String day = date.substring(6);
        String year = date.substring(0,4);
        String month="";
        switch (monthNum){
            case "01":month=monthList[0];break;
            case "02":month=monthList[1];break;
            case "03":month=monthList[2];break;
            case "04":month=monthList[3];break;
            case "05":month=monthList[4];break;
            case "06":month=monthList[5];break;
            case "07":month=monthList[6];break;
            case "08":month=monthList[7];break;
            case "09":month=monthList[8];break;
            case "10":month=monthList[9];break;
            case "11":month=monthList[10];break;
            case "12":month=monthList[11];break;

        }
        return day+"-"+month+"-"+year;
    }

    public String currencyToDB(String cur){

        String retCur="";

        switch (cur){
            case"LKR.":case"රු.":retCur="LKR.";break;
            case"USD.":case"ඇ. ඩොලර්.":retCur="USD.";break;
            case"EUR.":case"යුරෝ.":retCur="EUR.";break;
            case"GBP.":case"පවුම්.":retCur="GBP.";break;
            case"INR.":case"ඉන්දීය රු.":retCur="INR.";break;
        }
        return retCur;
    }

    public String currencyView(String cur, Context con){
        Resources res = con.getResources();
        String[] curArr = res.getStringArray(R.array.spinnerCurrency);
        String retCur="";
        switch (cur){
            case"LKR.":retCur=curArr[0];break;
            case"USD.":retCur=curArr[1];break;
            case"EUR.":retCur=curArr[2];break;
            case"GBP.":retCur=curArr[3];break;
            case"INR.":retCur=curArr[4];break;
        }
        return retCur;
    }
}
