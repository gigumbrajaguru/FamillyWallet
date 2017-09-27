package ccpe001.familywallet.transaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.AccessControlContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ccpe001.familywallet.Splash;
import ccpe001.familywallet.budget.actionValidater;

import static java.security.AccessController.getContext;

/**
 * Created by Knight on 9/7/2017.
 */

public class AutoRecurringTransactions {

    String userID, familyID, InGroup;
    List<TransactionDetails> tdList;
    List<String> keys;
    private DatabaseReference mDatabase, m2Database;
    private int todayYear, todayMonth, todayDay, todayDayOfWeek; //variables to hold today date and dat of week
    private int nxtYear, nxtMonth, nxtDay; //variables to hold tomorrow date
    TransactionDetails tdReturn;
    final actionValidater av = new actionValidater();

    public AutoRecurringTransactions(String uID , String fID, String inGrp){
        /*Getting Current Date*/
        final Calendar c = Calendar.getInstance();
        todayYear = c.get(Calendar.YEAR);   //Current year
        todayMonth = (c.get(Calendar.MONTH))+1; //Current month
        todayDay = c.get(Calendar.DAY_OF_MONTH);    //Current day
        todayDayOfWeek = c.get(Calendar.DAY_OF_WEEK);    //Current day of week ie- Sunday=1, Monday=2...

        /*Getting tomorrow Date*/
        c.add(Calendar.DATE, +1);
        nxtYear = c.get(Calendar.YEAR);   //tomorrow year
        nxtMonth = (c.get(Calendar.MONTH))+1; //tomorrow month
        nxtDay = c.get(Calendar.DAY_OF_MONTH);    //tomorrow day

        /**/
        familyID=fID;
        userID=uID;
        InGroup = inGrp;


        /*Keeping the recurring transaction list available offline */
        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = FirebaseDatabase.getInstance().getReference("RecurringTransactions");
            m2Database = FirebaseDatabase.getInstance().getReference("Transactions");
            mDatabase.keepSynced(true);
            m2Database.keepSynced(true);
        }catch (Exception e){

        }
        m2Database = FirebaseDatabase.getInstance().getReference();

        /*Getting the recurring transaction list for particular user*/
        try {
            Log.i("echo",familyID);
            Query query = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(userID);
            query.keepSynced(true);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot tdSnapshot : dataSnapshot.getChildren()) {
                        TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                        addRecurring(td);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Log.i("Recurring trns : ",""+e);
        }
    }

    /*Method to add recurring transactions according to the specific period automatically*/
    public void addRecurring(TransactionDetails td){

        String dbDate = td.getDate();   //getting date and time of the recurring transaction
        String dbMonth = dbDate.substring(4,6); //getting month of the recurring transaction
        String dbDay = dbDate.substring(6,8);   //getting day of the recurring transaction
        String dbYear = dbDate.substring(0,4);  //getting year of the recurring transaction
        String dbTime= dbDate.substring(8);  //getting time of the recurring transaction
        String dbReccur = td.getRecurringPeriod();  //getting recurring period of the recurring transaction


        /*calling method to add Daily transactions transactions to the database */
        if (dbReccur.equals("Daily")){
            dailyRecuring(dbTime, td);
        }
        /*calling method to add Weekly transactions transactions to the database */
        else if(dbReccur.equals("Weekly")){
            weeklyRecurring(dbYear,dbMonth,dbDay,dbTime,td);
        }
        /*calling method to add Monthly transactions transactions to the database */
        else if(dbReccur.equals("Monthly")){
            monthlyRecurring(dbYear,dbMonth,dbDay,dbTime,td);
        }
        /*calling method to add Annually transactions transactions to the database */
        else if(dbReccur.equals("Annually")){
            annuallyRecurring(dbYear,dbMonth,dbDay,dbTime,td);
        }
    }

    /* Method the find the transaction recurring daily if so add it to the transaction list in database */
    private void dailyRecuring( String dbTime, TransactionDetails td) {
        String retMonth, retDay, retDate;

            /*Converting the month into a double digit value if its not*/
            if (todayMonth<10){
                retMonth="0"+todayMonth;
            }
            else {
                retMonth=Integer.toString(todayMonth);
            }

                /*Converting the day into a double digit value if its not*/
            if (todayDay<10){
                retDay="0"+todayDay;
            }
            else {
                retDay=Integer.toString(todayDay);
            }

        retDate=Integer.toString(todayYear)+retMonth+retDay+dbTime; //return date converted to format (year+month+day+time ie-201709241245)
        addTransaction(td, retDate);
    }

    /* Method the find the transaction recurring weekly if so add it to the transaction list in database */
    private void weeklyRecurring(String dbYear, String dbMonth, String dbDay, String dbTime, TransactionDetails td){
        String retDate, retMonth, retDay;
        Integer dbDayOfWeek = 0;
        try {
            Date date = new SimpleDateFormat("yyyyMMdd").parse(dbYear+dbMonth+dbDay);
            final Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            dbDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        }catch (Exception e){

        }

            /*Converting the month into a double digit value if its not*/
            if (todayMonth<10){
                retMonth="0"+todayMonth;
            }
            else {
                retMonth=Integer.toString(todayMonth);
            }

            /*Converting the day into a double digit value if its not*/
            if (todayDay<10){
                retDay="0"+todayDay;
            }
            else {
                retDay=Integer.toString(todayDay);
            }

        if (dbDayOfWeek==todayDayOfWeek){

            retDate=Integer.toString(todayYear)+retMonth+retDay+dbTime; //return date converted to format (year+month+day+time ie-201709241245)
            addTransaction(td, retDate);

        }
    }

    /* Method the find the transaction recurring monthly if so add it to the transaction list in database */
    private void monthlyRecurring(String dbYear, String dbMonth, String dbDay, String dbTime, TransactionDetails td){
        String retDay,retMonth;
            if (dbMonth.equals("02") && dbDay.equals("28") && todayDay==28 && nxtDay!=29){
                retDay="28";
            }
            else if (dbDay.equals("31") && dbDay.equals("30") && todayDay==30 && nxtDay!=31){
                retDay="30";
            }
            else{
                retDay=dbDay;
            }

            if (todayDay==Integer.parseInt(retDay)){

                if (todayMonth<10){
                    retMonth="0"+todayMonth;
                }
                else {
                    retMonth=Integer.toString(todayMonth);
                }
                String retdate = Integer.toString(todayYear)+retMonth+retDay+dbTime;

                addTransaction(td, retdate);

            }
    }

    /* Method the find the transaction recurring annually if so add it to the transaction list in database */
    private void annuallyRecurring(String dbYear, String dbMonth, String dbDay, String dbTime, TransactionDetails td){
        String retDay,retMonth;
        if (dbMonth.equals("02") && dbDay.equals("28") && todayDay==28 && nxtDay!=29){
            retDay="28";
        }
        else if (dbDay.equals("31") && dbDay.equals("30") && todayDay==30 && nxtDay!=31){
            retDay="30";
        }
        else{
            retDay=dbDay;
        }
        if (todayMonth==Integer.parseInt(dbMonth) && todayDay==Integer.parseInt(retDay)){

            if (todayMonth<10){
                retMonth="0"+todayMonth;
            }
            else {
                retMonth=Integer.toString(todayMonth);
            }
            String retdate = Integer.toString(todayYear)+retMonth+retDay+dbTime;

            addTransaction(td, retdate);

        }
    }

    /* method to add the recurring transaction to database*/
    private void addTransaction(TransactionDetails td, String retDate) {
        tdReturn = new TransactionDetails(userID,td.getAmount(), td.getTitle(),
                td.getCategoryName(), retDate, td.getCategoryID(), td.getTime(),
                td.getAccount(), td.getLocation(), td.getType(), td.getCurrency(),familyID);
        if (familyID.equals(userID) && !InGroup.equals("true")){
            m2Database.child("Transactions").child(userID).push().setValue(tdReturn);
        }
        else {
            m2Database.child("Transactions").child("Groups").child(familyID).push().setValue(tdReturn);
        }
        m2Database.child("Transactions").child(familyID).push().setValue(tdReturn);
        Double amountDouble =Double.parseDouble(td.getAmount());
        /* Reduct amount from the relevant account  */
        if (td.getType().equals("Expense")){
            av.amountCheck(td.getAccount(), amountDouble);

        }
        /* Add amount to the relevant account  */
        else if(td.getType().equals("Income")){
            av.addIncome(td.getAccount(), amountDouble);
        }
    }

}
