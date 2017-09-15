package ccpe001.familywallet.budget;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public static boolean checks;
    static double availableAmount,newValue;
    static String key;
    static int check=0,checkam;

    public actionValidater(){}

    public static boolean amountCheck(final String AccountName, final double amount) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        checkam=0;
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dumpData dp= new dumpData();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(isSaving(AccountName)) {
                        if (child.child("accountName").getValue().toString().equals(AccountName)) {
                            if ((Double.parseDouble(child.child("amount").getValue().toString()) - amount) >= 0) {
                                dp.setCheck(true);
                                checks = dp.getCheck();
                                Log.i("ss", "ss2");
                                if (checkam == 0) {
                                    getAmount(AccountName, amount);
                                    checkam = 1;
                                }
                            } else {
                                dp.setCheck(false);
                                checks = dp.getCheck();
                            }

                        }
                    }
                    else{
                        dp.setCheck(false);
                        checks = dp.getCheck();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return checks;
    }
    public static boolean isSaving(final String AccountName) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    dumpData dp= new dumpData();
                    if((child.child("accountName").getValue().toString()).equals(AccountName)) {
                        if ((child.child("isSaving").getValue().toString()).equals("True")) {
                            dp.setCheck(false);
                            checks=dp.getCheck();
                        } else {

                            dp.setCheck(true);
                            checks=dp.getCheck();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return checks;
    }
    public static boolean addIncome(final String accountName, final Double income) {
        check=0;
        checks=false;
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if(child.child("accountName").getValue().toString().equals(accountName)) {
                        availableAmount = Double.parseDouble(child.child("amount").getValue().toString());
                        newValue = availableAmount + income;
                        if (check == 0) {
                            child.getRef().child("amount").setValue(newValue);
                            dumpData dp= new dumpData();
                            dp.setCheck(true);
                            dp.getCheck();
                            check = 1;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return checks;
    }
    public static  boolean getAmount(final String AccountName, final double getamount){

            check=0;
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        if (child.child("accountName").getValue().toString().equals(AccountName)) {
                            {
                                availableAmount = Double.parseDouble(child.child("amount").getValue().toString());
                                newValue = availableAmount - getamount;
                                if (check == 0) {
                                    child.getRef().child("amount").setValue(newValue);
                                    dumpData dp= new dumpData();
                                    dp.setCheck(true);
                                    checks=dp.getCheck();
                                    check = 1;
                                }
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        return checks;
        }

    public static boolean  accountChecker(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dumpData dp= new dumpData();
                if(dataSnapshot.hasChildren()){
                    dp.setCheck(false);
                    checks=dp.getCheck();
                }
                else{
                    dp.setCheck(true);
                    checks=dp.getCheck();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return checks;
        }
    public static boolean accountName(final String accountN){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int x=0;
                checks=true;
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    if(child.child("accountName").getValue().equals(accountN)){
                        dumpData dp=new dumpData();
                        dp.setCheckName(false);
                        checks=dp.getCheckName();
                        x=1;
                    }
                    else{
                        if(x==0) {
                            dumpData dp=new dumpData();
                            dp.setCheckName(true);
                            checks=dp.getCheckName();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return checks;
    }
    public static boolean rectransactionChecker(final String Wallet){
        int x=0;
        checks=true;
        mDatabase = FirebaseDatabase.getInstance().getReference("RecurringTransactions");
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child(currentUser.getUid()).orderByChild("account").equalTo(Wallet).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checks=true;
                dumpData dp= new dumpData();
               if(dataSnapshot.hasChildren()){
                   dp.setCheck(false);
                   checks=dp.getCheck();
               }
               else{
                   dp.setCheck(true);
                   checks=dp.getCheck();
               }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return checks;
    }

}
