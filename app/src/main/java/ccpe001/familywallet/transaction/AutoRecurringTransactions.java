package ccpe001.familywallet.transaction;

import android.accounts.Account;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.internal.ye;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ccpe001.familywallet.budget.ActionValidater;

/**
 * Created by Knight on 9/7/2017.
 */

public class AutoRecurringTransactions {

    public static final String STRING_ZERO = "0", STRING_DAY_28th ="28", STRING_DAY_31st ="31" ,
            STRING_DAY_30th ="30" , STRING_MONTH_FEBRUARY ="02";
    public static final Integer INTEGER_DAY_28th=28,INTEGER_DAY_29th=29,INTEGER_DAY_30th=30,
            INTEGER_DAY_31st=31, INTEGER_TEN=10;
    private static String USER_ID, FAMILY_ID, ADMIN_GROUP_STATUS, USER_NAME;
    private DatabaseReference mDatabase, m2Database;
    private static Integer TODAY_YEAR, TODAY_MONTH, TODAY_DAY, TODAY_DAY_OF_WEEK; //variables to hold today date and dat of week
    private static Integer TOMORROW_YEAR, TOMORROW_MONTH, TOMORROW_DAY; //variables to hold tomorrow date
    private TransactionDetails tdReturn;
    private ActionValidater av = new ActionValidater();

    public AutoRecurringTransactions(String uID , String fID, String uName, String inGrp){
        /*Getting Current Date*/
        final Calendar c = Calendar.getInstance();
        TODAY_YEAR = c.get(Calendar.YEAR);   //Current year
        TODAY_MONTH = (c.get(Calendar.MONTH))+1; //Current month
        TODAY_DAY = c.get(Calendar.DAY_OF_MONTH);    //Current day
        TODAY_DAY_OF_WEEK = c.get(Calendar.DAY_OF_WEEK);    //Current day of week ie- Sunday=1, Monday=2...

        /*Getting tomorrow Date*/
        c.add(Calendar.DATE, +1);
        TOMORROW_YEAR = c.get(Calendar.YEAR);   //tomorrow year
        TOMORROW_MONTH = (c.get(Calendar.MONTH))+1; //tomorrow month
        TOMORROW_DAY = c.get(Calendar.DAY_OF_MONTH);    //tomorrow day

        /* */
        FAMILY_ID = fID;
        USER_ID = uID;
        USER_NAME =uName;
        ADMIN_GROUP_STATUS = inGrp;


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
            Log.i("echo", FAMILY_ID);
            Query query = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(USER_ID);
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

    /* Method to add recurring transactions according to the specific period automatically*/
    public void addRecurring(TransactionDetails td){

        String dbDate = td.getDate();   //getting date and startTime of the recurring transaction
        String dbMonth = dbDate.substring(4,6); //getting month of the recurring transaction
        String dbDay = dbDate.substring(6,8);   //getting day of the recurring transaction
        String dbYear = dbDate.substring(0,4);  //getting year of the recurring transaction
        String dbTime= dbDate.substring(8);  //getting startTime of the recurring transaction
        String dbReccur = td.getRecurringPeriod();  //getting recurring period of the recurring transaction

        /* Creating a transaction date object */
        TransactionDate trDate = new TransactionDate(dbDay,dbMonth,dbYear,dbTime);


        /*calling method to add Daily transactions transactions to the database */
        if (dbReccur.equals("Daily")){
            dailyRecuring(trDate, td);
        }
        /*calling method to add Weekly transactions transactions to the database */
        else if(dbReccur.equals("Weekly")){
            weeklyRecurring(trDate,td);
        }
        /*calling method to add Monthly transactions transactions to the database */
        else if(dbReccur.equals("Monthly")){
            monthlyRecurring(trDate,td);
        }
        /*calling method to add Annually transactions transactions to the database */
        else if(dbReccur.equals("Annually")){
            annuallyRecurring(trDate,td);
        }
    }

    /**
     * Method the find the transaction recurring daily if so add it to the transaction list in database
     *
     * @param transactionDate - date of the recurring transaction template
     * @param td - transaction details object
     */
    private void dailyRecuring( TransactionDate transactionDate, TransactionDetails td) {
        String retMonth, retDay, retDate;

            /*Converting the month into a double digit value if its not*/
        retMonth = getMonthAsTwoDigits();

                /*Converting the day into a double digit value if its not*/
        retDay = getDayAsTwoDigits();

        retDate=String.valueOf(TODAY_YEAR)+retMonth+retDay+transactionDate.getTime(); //return date converted to format (year+month+day+startTime ie-201709241245)
        addTransaction(td, retDate);
    }


    /**
     * Method the find the transaction recurring weekly if so add it to the transaction list in database
     *
     * @param transactionDate - date of the recurring transaction template
     * @param td - transaction details object
     */
    private void weeklyRecurring(TransactionDate transactionDate, TransactionDetails td){
        String retDate, retMonth, retDay;
        Integer dbDayOfWeek = 0;
        try {
            Date date = new SimpleDateFormat("yyyyMMdd").parse(transactionDate.getYear()+transactionDate.getMonth()+transactionDate.getDay());
            final Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            dbDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        }catch (Exception e){

        }

        retMonth = getMonthAsTwoDigits();
        retDay = getDayAsTwoDigits();


        if (dbDayOfWeek==TODAY_DAY_OF_WEEK){

            retDate=String.valueOf(TODAY_YEAR)+retMonth+retDay+transactionDate.getTime(); //return date converted to format (year+month+day+startTime ie-201709241245)
            addTransaction(td, retDate);

        }
    }

    /**
     * Method the find the transaction recurring monthly if so add it to the transaction list in database
     *
     * @param transactionDate - date of the recurring transaction template
     * @param td - transaction details object
     */
    private void monthlyRecurring(TransactionDate transactionDate, TransactionDetails td){
        String retDay,retMonth;
        retDay = getValidDay(transactionDate.getMonth(), transactionDate.getDay());

        if (TODAY_DAY ==Integer.parseInt(retDay)){

            retMonth = getMonthAsTwoDigits();
            String retdate = String.valueOf(TODAY_YEAR)+retMonth+retDay+transactionDate.getTime();

                addTransaction(td, retdate);

            }
    }

    /**
     * Method the find the transaction recurring annually if so add it to the transaction list in database
     *
     * @param transactionDate - date of the recurring transaction template
     * @param td - transaction details object
     */
    private void annuallyRecurring(TransactionDate transactionDate,
                                    TransactionDetails td){
        String retDay,retMonth;
        retDay = getValidDay(transactionDate.getMonth(), transactionDate.getDay());
        if (TODAY_MONTH ==Integer.parseInt(transactionDate.getMonth()) && TODAY_DAY ==Integer.parseInt(retDay)){

            retMonth = getMonthAsTwoDigits();
            String retdate = String.valueOf(TODAY_YEAR)+retMonth+retDay+transactionDate.getTime();

            addTransaction(td, retdate);

        }
    }

    /**
     * get the valid date i.e if the recurring transaction is added to recur every 31st change the
     * date to 30 for month that doesnt have a 31 and 28 for february
     *
     * @param month - month of recurring the transaction
     * @param day - day of the recurring transaction
     * @return
     */
    @NonNull
    private String getValidDay(String month, String day) {
        String returnDay;
        if (month.equals(STRING_MONTH_FEBRUARY) && day.equals(STRING_DAY_28th)
                    && TODAY_DAY ==INTEGER_DAY_28th && TOMORROW_DAY!=INTEGER_DAY_29th){
            returnDay= STRING_MONTH_FEBRUARY;
        }
        else if (day.equals(STRING_DAY_31st) && day.equals(STRING_DAY_30th)
                    && TODAY_DAY ==INTEGER_DAY_30th && TOMORROW_DAY!=INTEGER_DAY_31st){
            returnDay=STRING_DAY_30th;
        }
        else{
            returnDay=day;
        }
        return returnDay;
    }

    /**
     *
     * @return the day as a two digit variable if day value is before 10th
     */
    private String getDayAsTwoDigits() {
        String retDay;/*Converting the day into a double digit value if its not*/
        if (TODAY_DAY < INTEGER_TEN){
            retDay=STRING_ZERO+ TODAY_DAY;
        }
        else {
            retDay=String.valueOf(TODAY_DAY);
        }
        return retDay;
    }

    /**
     *
     * @return the month as a two digit variable if month value is before 10th month
     */
    @NonNull
    private String getMonthAsTwoDigits() {
        String retMonth;
        if (TODAY_MONTH < INTEGER_TEN){
            retMonth=STRING_ZERO+ TODAY_MONTH;
        }
        else {
            retMonth=String.valueOf(TODAY_MONTH);
        }
        return retMonth;
    }


    /**
     * method to add the recurring transaction to database
     *
     * @param td - transaction details object
     * @param retDate - new date of the recurring transaction
     */
    private void addTransaction(TransactionDetails td, String retDate) {
        tdReturn = new TransactionDetails(USER_ID, USER_NAME,td.getAmount(), td.getTitle(),
                td.getCategoryName(), retDate, td.getCategoryID(), td.getTime(),
                td.getAccount(), td.getLocation(), td.getType(), td.getCurrency(), FAMILY_ID);
        if (FAMILY_ID.equals(USER_ID) && !ADMIN_GROUP_STATUS.equals("true")){
            m2Database.child("Transactions").child(USER_ID).push().setValue(tdReturn);
        }
        else {
            m2Database.child("Transactions").child("Groups").child(FAMILY_ID).push().setValue(tdReturn);
        }
        m2Database.child("Transactions").child(FAMILY_ID).push().setValue(tdReturn);
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


    /* Transaction date details class*/
    public class TransactionDate{

        String day, month,year,time;

        public TransactionDate(){
        }

        public TransactionDate(String d,String m, String y, String t){
            day=d;
            month=m;
            year=y;
            time=t;
        }

        public String getDay() {
            return day;
        }

        public String getMonth() {
            return month;
        }

        public String getYear() {
            return year;
        }

        public String getTime() {
            return time;
        }
    }

}


