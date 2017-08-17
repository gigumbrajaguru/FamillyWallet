package ccpe001.familywallet.budget;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static ccpe001.familywallet.R.string.amount;


/**
 * Created by Gigum on 2017-08-13.
 */

public class actionValidater {
    private static DatabaseReference mDatabase;
    static boolean y, c;
    static double availableAmount,updatedIncomevalue,updatedAmount;

    public static boolean amountCheck(final String AccountName, final double amount) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Account").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if ((String) child.child("accountName").getValue() == AccountName) {
                        if (((Double) child.child("amount").getValue() - amount) >= 0) {
                            y = true;
                        } else {
                            y = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return y;
    }

    public static boolean isSaving(final String AccountName) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Account").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if ((String) child.child("accountName").getValue() == AccountName) {
                        if (((String) child.child("isSaving").getValue()) == "True") {
                            c = true;
                        } else {
                            c = false;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return c;
    }

    public boolean addIncome(final String accountName, final Double income) {
        c=false;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Account").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if ((String) child.child("accountName").getValue() == accountName) {
                        availableAmount=(Double)child.child("amount").getValue();
                        updatedIncomevalue=availableAmount+income;
                        dataSnapshot.getRef().child("amount").setValue(updatedIncomevalue);
                    }
                }
                c=true;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return c;
    }
    public static boolean getAmount(final String AccountName, final double getamount){
        if(amountCheck(AccountName,amount)){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Account").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if ((String) child.child("accountName").getValue() == AccountName) {
                            updatedAmount=(Double) child.child("amount").getValue()-getamount;
                            dataSnapshot.getRef().child("amount").setValue(updatedIncomevalue);
                        }
                    }
                    c=true;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else {
            c=false;
        }
        return c;
    }
}
