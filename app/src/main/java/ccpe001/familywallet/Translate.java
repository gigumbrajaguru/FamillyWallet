package ccpe001.familywallet;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Knight on 8/25/2017.
 */

public class Translate {

    public String categoryToEngilsh(String cat){
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

}
