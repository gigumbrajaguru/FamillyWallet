package ccpe001.familywallet.budget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ccpe001.familywallet.admin.Notification;

import static ccpe001.familywallet.budget.ActionValidater.key;

/**
 * Created by Gigum on 2017-10-03.
 */

public class AutoTracking {
    String userID, familyID, InGroup,notify="",previouskey="";
    int budgetcount=0,nextitem=0;
    private static DatabaseReference mDatabase;
    private String[] budgetsdetail=new String[10000];
    private String[] detailTransaction=new String[10000];
    public  void getTransactionDetail(final Context context) {
        previouskey="";
        Query querys;
        /**Get Shared preference data**/
        SharedPreferences sharedPref = context.getSharedPreferences("fwPrefs", 0);
        userID = sharedPref.getString("uniUserID", "");
        familyID = sharedPref.getString("uniFamilyID", "");
        InGroup = sharedPref.getString("InGroup", "");
        if (familyID.equals(userID) && !InGroup.equals("true")) {
            querys =  FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date");
        } else {
            querys = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");

        }
        /**Get transaction detail after data change event**/
        querys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot tdSnapshot : dataSnapshot.getChildren()) {
                        String catName = tdSnapshot.child("categoryName").getValue().toString();
                        String transamount = tdSnapshot.child("amount").getValue().toString();
                        String tranDate = tdSnapshot.child("date").getValue().toString();
                        String subYr = tranDate.substring(0, 4);
                        String subMon = tranDate.substring(4, 6);
                        String subDate = tranDate.substring(6, 8);
                        detailTransaction[0+nextitem]= catName;
                        detailTransaction[1+nextitem]= transamount;
                        detailTransaction[2+nextitem]= subYr;
                        detailTransaction[3+nextitem]= subMon;
                        detailTransaction[4+nextitem]= subDate;
                        nextitem=nextitem+5;
                    }
                    int numTrasaction = nextitem-4;
                    getBudgetDetail(context,familyID,numTrasaction);
                }
                else {
                    getBudgetDetail(context,familyID,0);
                }
            }

                @Override
                public void onCancelled (DatabaseError databaseError){

                }

        });
    }
    /**Get budget detail**/
    public void getBudgetDetail(final Context context,final String familyID,final int numTrans) {
        nextitem=0;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Budget").orderByChild("familyId").equalTo(familyID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            notify = child.child("notification").getValue().toString();
                            String stat = child.child("status").getValue().toString();
                            String budgetAmount = child.child("Amount").getValue().toString();
                            String catagory = child.child("catagory").getValue().toString();
                            String strtDate = child.child("startDate").getValue().toString();
                            String endDate = child.child("endDays").getValue().toString();
                            String key = child.getKey();
                            String[] startdateParts = strtDate.split("/");
                            String strYr = startdateParts[2];
                            String strMon = startdateParts[1];
                            String strDate = startdateParts[0];
                            String[] enddateParts = endDate.split("/");
                            String endYr = enddateParts[2];
                            String endMon = enddateParts[1];
                            String endate = enddateParts[0];
                            if (!stat.equals("Closed")) {
                                budgetsdetail[0+nextitem]= catagory;
                                budgetsdetail[1+nextitem]= strYr;
                                budgetsdetail[2+nextitem]= strMon;
                                budgetsdetail[3+nextitem]= strDate;
                                budgetsdetail[4+nextitem]= endYr;
                                budgetsdetail[5+nextitem]= endMon;
                                budgetsdetail[6+nextitem]= endate;
                                budgetsdetail[7+nextitem]= budgetAmount;
                                budgetsdetail[8+nextitem]= key;
                                nextitem=nextitem+9;

                            }
                            budgetcount=nextitem-8;
                        }
                        percentageCalc(context,familyID, numTrans, budgetcount, notify);
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    /**Calculate percentages and totals transacction amount**/
    public void percentageCalc(Context context,String familyID,int numtrans,int numbudget,String notify){
            for(int i=0;i<numbudget;i=i+9){
                double total=0;
                String catagory=budgetsdetail[0+i];
                String key=budgetsdetail[8+i];
                int strYr=Integer.parseInt(budgetsdetail[1+i]);
                int strMon=Integer.parseInt(budgetsdetail[2+i]);
                int strDate=Integer.parseInt(budgetsdetail[3+i]);
                int endYr=Integer.parseInt(budgetsdetail[4+i]);
                int endMon=Integer.parseInt(budgetsdetail[5+i]);
                int endate=Integer.parseInt(budgetsdetail[6+i]);
                int budgetAmount=Integer.parseInt(budgetsdetail[7+i]);
                total=0;
                for(int f=0;f<numtrans;f=f+5) {
                    if (detailTransaction[0 + f] != null) {
                        if (detailTransaction[0 + f].equals(catagory)) {
                            double transamount = Double.parseDouble(detailTransaction[1 + f]);
                            int subYr = Integer.parseInt(detailTransaction[2 + f]);
                            int subMon = Integer.parseInt(detailTransaction[3 + f]);
                            int subDate = Integer.parseInt(detailTransaction[4 + f]);
                            if (strYr < subYr && endYr > subYr) {
                                total = total + transamount;
                            } else if (subYr == strYr || endYr == subYr) {
                                if (strMon < subMon && subMon < endMon) {
                                    total = total + transamount;
                                }
                                if (strMon == subMon || subMon == endMon) {
                                    if (strDate < subDate && subDate < endate) {
                                        total = total + transamount;
                                    }

                                }
                            }
                        }
                    }
                }
                double percentage=(total/budgetAmount)*100;
                statusUpdate(context,percentage,key,notify);
            }
    }
    /**Update status according to percentage**/
    public  void statusUpdate(Context context,Double percentage,String key,String notify) {
        Notification not=new Notification();
        if (!previouskey.equals(key)) {
            if (percentage > 90 && percentage < 95) {
                if (notify.equals("On")) {
                  /*  not.addNotification(context,"Critical level","Budget went over 95%");*/
                }
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Critical Level");
            } else if (percentage > 95) {
                if (notify.equals("On")) {
                  /*  not.addNotification(context,"Critical level","Budget went over 90%");*/
                }
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Over Flow");
            } else if (percentage < 50) {
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Good");
            } else if (percentage > 50 && percentage < 90) {
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Care");
            }
            String addpercentage = String.format("%.2f", percentage);
            FirebaseDatabase.getInstance().getReference("Budget").child(key).child("percentage").getRef().setValue(addpercentage);
            previouskey=key;
        }
    }
}


