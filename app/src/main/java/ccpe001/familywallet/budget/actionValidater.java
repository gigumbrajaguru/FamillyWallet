package ccpe001.familywallet.budget;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Gigum on 2017-08-13.
 */

public class actionValidater {
    private static DatabaseReference mDatabase;
    static boolean y=true, c=true;
    static double availableAmount,newValue;
    static String key;


    public static boolean amountCheck(final String AccountName, final double amount) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Account").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.child("accountName").getValue().toString().equals(AccountName)) {
                        if ((Double.parseDouble(child.child("amount").getValue().toString())-amount) >= 0) {
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
                    if((child.child("accountName").getValue().toString()).equals(AccountName))
                        if ((child.child("isSaving").getValue().toString()).equals("True")){
                            c = true;
                        } else {
                            c = false;
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
        DatabaseReference mDatabases;
        mDatabase.child("Account").orderByChild("accountName").equalTo(accountName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    availableAmount=Double.parseDouble(child.child("amount").getValue().toString());
                    newValue=availableAmount+income;

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
        if(amountCheck(AccountName,getamount)){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mDatabases = mDatabase.child("Account");
            mDatabase.child("Account").orderByChild("accountName").equalTo(AccountName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        availableAmount=Double.parseDouble(child.child("amount").getValue().toString());
                        newValue=availableAmount-getamount;
                        key=child.getRef().getKey();
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
