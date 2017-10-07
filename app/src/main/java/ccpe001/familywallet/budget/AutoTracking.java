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
    String userID, familyID, InGroup;
    boolean updateValidation;
    int budgetcount;
    private static DatabaseReference mDatabase;
    public static DataTrackTrasmit[][] TransactionArray;
    public static DataTrackTrasmit[][] budgetArray;
    public  void getTransactionDetail(Context context) {
        updateValidation=true;
        TransactionArray = new DataTrackTrasmit[100][];
        for (int i = 0; i < TransactionArray.length; i++) {
            TransactionArray[i] = new DataTrackTrasmit[100];
            for (int j = 0; j < TransactionArray[i].length; j++)
                TransactionArray[i][j] = new DataTrackTrasmit();
        }

        budgetArray = new DataTrackTrasmit[100][];
        for (int i = 0; i < budgetArray.length; i++) {
            budgetArray[i] = new DataTrackTrasmit[100];
            for (int j = 0; j < budgetArray[i].length; j++)
                budgetArray[i][j] = new DataTrackTrasmit();
        }

        Query querys;
        SharedPreferences sharedPref = context.getSharedPreferences("fwPrefs", 0);
        userID = sharedPref.getString("uniUserID", "");
        familyID = sharedPref.getString("uniFamilyID", "");
        InGroup = sharedPref.getString("InGroup", "");
        if (familyID.equals(userID) && !InGroup.equals("true")) {
            querys =  FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date");
        } else {
            querys = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");

        }
        querys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    int i = 0;
                    for (DataSnapshot tdSnapshot : dataSnapshot.getChildren()) {
                        String catName = tdSnapshot.child("categoryName").getValue().toString();
                        String transamount = tdSnapshot.child("amount").getValue().toString();
                        String tranDate = tdSnapshot.child("date").getValue().toString();
                        String subYr = tranDate.substring(0, 4);
                        String subMon = tranDate.substring(4, 6);
                        String subDate = tranDate.substring(6, 9);
                        TransactionArray[i][0].catagory = catName;
                        TransactionArray[i][1].transamount = transamount;
                        TransactionArray[i][2].subYr = subYr;
                        TransactionArray[i][3].subMon = subMon;
                        TransactionArray[i][4].subDate = subDate;
                        i++;
                    }
                    int numTrasaction = i;
                    getBudgetDetail(familyID, numTrasaction);
                }
                else {
                    getBudgetDetail(familyID, 0);
                }
            }

                @Override
                public void onCancelled (DatabaseError databaseError){

                }

        });
    }
    public void getBudgetDetail(final String familyID, final int numTrans) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Budget").orderByChild("familyId").equalTo(familyID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    budgetcount=0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String stat = child.child("status").getValue().toString();
                        String budgetAmount=child.child("Amount").getValue().toString();
                        String catagory = child.child("catagory").getValue().toString();
                        String strtDate = child.child("startDate").getValue().toString();
                        String endDate = child.child("endDays").getValue().toString();
                        String key=child.getKey();
                        String[]startdateParts = strtDate.split("/");
                        String strYr=startdateParts[2];
                        String strMon=startdateParts[0];
                        String strDate=startdateParts[1];
                        String[]enddateParts = endDate.split("/");
                        String endYr=enddateParts[2];
                        String endMon=enddateParts[0];
                        String endate=enddateParts[1];
                        if(!stat.equals("Closed")){
                            budgetArray[budgetcount][0].catagory=catagory;
                            budgetArray[budgetcount][1].strYr=strYr;
                            budgetArray[budgetcount][2].strMon=strMon;
                            budgetArray[budgetcount][3].strDate=strDate;
                            budgetArray[budgetcount][4].endYr=endYr;
                            budgetArray[budgetcount][5].endMon=endMon;
                            budgetArray[budgetcount][6].endate=endate;
                            budgetArray[budgetcount][7].budgetAmount=budgetAmount;
                            budgetArray[budgetcount][8].keys=key;
                        }
                        budgetcount++;
                    }
                    percentageCalc(familyID,numTrans,budgetcount);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    public void percentageCalc(String familyID,int numtrans,int numbudget){
        double total=0;
            for(int i=0;i<numbudget;i++){
                String catagory=budgetArray[i][0].catagory;
                String key=budgetArray[i][8].keys;
                int strYr=Integer.parseInt(budgetArray[i][1].strYr);
                int strMon=Integer.parseInt(budgetArray[i][2].strMon);
                int strDate=Integer.parseInt(budgetArray[i][3].strDate);
                int endYr=Integer.parseInt(budgetArray[i][4].endYr);
                int endMon=Integer.parseInt(budgetArray[i][5].endMon);
                int endate=Integer.parseInt(budgetArray[i][6].endate);
                int budgetAmount=Integer.parseInt(budgetArray[i][7].budgetAmount);
                for(int f=0;f<numtrans;f++){
                    if(TransactionArray[f][0].catagory.equals(catagory)){
                        double transamount=Double.parseDouble(TransactionArray[i][1].transamount);
                        int subYr=Integer.parseInt(TransactionArray[i][2].subYr);
                        int subMon=Integer.parseInt(TransactionArray[i][3].subMon);
                        int subDate=Integer.parseInt(TransactionArray[i][4].subDate);
                        if((strYr<subYr && endYr>subYr)) {
                            total=total+transamount;
                        }
                         else if((subYr==strYr)&& (strMon<subMon) ||(endYr==subYr && subMon<endMon) )
                        {
                            total=total+transamount;
                        }
                        else if(((strMon==subMon)&&(strDate<subDate))||((subMon==endMon)&&(subDate<endate))){
                            total=total+transamount;
                        }
                    }
                }
                double percentage=(total/budgetAmount)*100;
                statusUpdate(percentage,key);
            }
    }
    public  void statusUpdate(Double percentage,String key) {
        if(updateValidation) {
            if (percentage > 90 && percentage < 95) {
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Critical Level");
            } else if (percentage > 95) {
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Over Flow");
            }
            else if (percentage < 50) {
                FirebaseDatabase.getInstance().getReference("Budget").child(key).child("status").getRef().setValue("Good");
            }
            FirebaseDatabase.getInstance().getReference("Budget").child(key).child("percentage").getRef().setValue(percentage);
            updateValidation=false;
        }


    }

}


