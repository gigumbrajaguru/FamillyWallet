package ccpe001.familywallet.budget;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Gigum on 2017-10-03.
 */

public class AutoTracking {
    String userID, familyID, InGroup,notify="",previouskey="" ;
    int budgetcount;
    private static DatabaseReference mDatabase;
    String[][] transactions=new String[10000][];
    String[] detailTransaction=new String[10];
    String[][] budgets=new String[10000][];
    String[] budgetsdetail=new String[10];
    public  void getTransactionDetail(Context context) {
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
                    int transcount = 0;
                    for (DataSnapshot tdSnapshot : dataSnapshot.getChildren()) {
                        String catName = tdSnapshot.child("categoryName").getValue().toString();
                        String transamount = tdSnapshot.child("amount").getValue().toString();
                        String tranDate = tdSnapshot.child("date").getValue().toString();
                        String subYr = tranDate.substring(0, 4);
                        String subMon = tranDate.substring(4, 6);
                        String subDate = tranDate.substring(6, 9);
                        detailTransaction[0]= catName;
                        detailTransaction[1]= transamount;
                        detailTransaction[2]= subYr;
                        detailTransaction[3]= subMon;
                        detailTransaction[4]= subDate;
                        transactions[transcount]= detailTransaction;
                        transcount++;
                    }
                    int numTrasaction = (int)dataSnapshot.getChildrenCount();
                    getBudgetDetail(familyID,numTrasaction);
                }
                else {
                    getBudgetDetail(familyID,0);
                }
            }

                @Override
                public void onCancelled (DatabaseError databaseError){

                }

        });
    }
    /**Get budget detail**/
    public void getBudgetDetail(final String familyID,final int numTrans) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Budget").orderByChild("familyId").equalTo(familyID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                        int budgetcounts = 0;
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
                            String strMon = startdateParts[0];
                            String strDate = startdateParts[1];
                            String[] enddateParts = endDate.split("/");
                            String endYr = enddateParts[2];
                            String endMon = enddateParts[0];
                            String endate = enddateParts[1];
                            if (!stat.equals("Closed")) {
                                budgetsdetail[0]= catagory;
                                budgetsdetail[1]= strYr;
                                budgetsdetail[2]= strMon;
                                budgetsdetail[3]= strDate;
                                budgetsdetail[4]= endYr;
                                budgetsdetail[5]= endMon;
                                budgetsdetail[6]= endate;
                                budgetsdetail[7]= budgetAmount;
                                budgetsdetail[8]= key;
                                budgets[budgetcount]=budgetsdetail;
                                budgetcounts++;

                            }
                            budgetcount=(int)dataSnapshot.getChildrenCount();
                        }
                        percentageCalc(familyID, numTrans, budgetcount, notify);
                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    /**Calculate percentages and totals transacction amount**/
    public void percentageCalc(String familyID,int numtrans,int numbudget,String notify){
            for(int i=0;i<numbudget;i++){
                double total=0;
                budgetsdetail=budgets[i];
                String catagory=budgetsdetail[0];
                String key=budgetsdetail[8];
                int strYr=Integer.parseInt(budgetsdetail[1]);
                int strMon=Integer.parseInt(budgetsdetail[2]);
                int strDate=Integer.parseInt(budgetsdetail[3]);
                int endYr=Integer.parseInt(budgetsdetail[4]);
                int endMon=Integer.parseInt(budgetsdetail[5]);
                int endate=Integer.parseInt(budgetsdetail[6]);
                int budgetAmount=Integer.parseInt(budgetsdetail[7]);
                for(int f=0;f<numtrans;f++) {
                    detailTransaction=transactions[f];
                    if (detailTransaction[0].equals(catagory) ) {
                            double transamount = Double.parseDouble(detailTransaction[1]);
                            int subYr = Integer.parseInt(detailTransaction[2]);
                            int subMon = Integer.parseInt(detailTransaction[3]);
                            int subDate = Integer.parseInt(detailTransaction[4]);
                            if ((strYr < subYr && endYr > subYr)) {
                                total = total + transamount;
                            } else if ((subYr == strYr) && (strMon < subMon) || (endYr == subYr && subMon < endMon)) {
                                total = total + transamount;
                            } else if (((strMon == subMon) && (strDate < subDate)) || ((subMon == endMon) && (subDate < endate))) {
                                total = total + transamount;
                            }
                    }
                }
                double percentage=(total/budgetAmount)*100;
                statusUpdate(percentage,key,notify);
            }
    }
    /**Update status according to percentage**/
    public  void statusUpdate(Double percentage,String key,String notify) {
        if(!previouskey.equals(key)) {
            if (percentage > 90 && percentage < 95) {
                if(notify.equals("On")) {

                }
                    FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Critical Level");
            } else if (percentage > 95) {
                if(notify.equals("On")) {
                }
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Over Flow");
            }
            else if (percentage < 50) {
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Good");
            }  else if (percentage > 50 && percentage<90) {
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Care");
            }
            String addpercentage=String.format("%.2f",percentage);
            FirebaseDatabase.getInstance().getReference("Budget").child(key).child("percentage").getRef().setValue(addpercentage);
            previouskey=key;
            addpercentage="0";

        }



    }

}


