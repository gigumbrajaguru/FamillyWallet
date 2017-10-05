package ccpe001.familywallet.budget;


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

public class ActionValidater {
    private static DatabaseReference mDatabase;
    public static boolean checks;
    public static double availableAmount,newValue;
    static String key;
    public static int check=0,checkam;

    public ActionValidater(){}

    public static boolean amountCheck(final String AccountName, final double amount) {/*this method use to validate transaction if can process transaction this will return true*/
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        checkam=0;
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Datatrasmit dp= new Datatrasmit();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(isSaving(AccountName)) {
                        if (child.child("accountName").getValue().toString().equals(AccountName)) {
                            if ((Double.parseDouble(child.child("amount").getValue().toString()) - amount) >= 0) {
                                dp.setCheck(true);
                                checks = dp.getCheck();
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
    public static boolean isSaving(final String AccountName) {/* If given account is saving account this method return false*/
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Datatrasmit dp= new Datatrasmit();
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
    public static boolean addIncome(final String accountName, final Double income) {/*This method use to add income to given account*/
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
                            Datatrasmit dp= new Datatrasmit();
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
    public static  boolean getAmount(final String AccountName, final double getamount){/*this method reduce given amount from given account*/

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
                                    Datatrasmit dp= new Datatrasmit();
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
                Datatrasmit dp= new Datatrasmit();
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
    public static boolean accountName(final String accountN){ /* If there is no equal account name for user this method will return true*/
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int x=0;
                checks=true;
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    if(child.child("accountName").getValue().equals(accountN)){
                        Datatrasmit dp=new Datatrasmit();
                        dp.setCheckName(false);
                        checks=dp.getCheckName();
                        x=1;
                    }
                    else{
                        if(x==0) {
                            Datatrasmit dp=new Datatrasmit();
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



    public static boolean rectransactionChecker(final String Wallet){/*If there is allocated account for users any recurring transaction this method will return false*/
        int x=0;
        checks=true;
        mDatabase = FirebaseDatabase.getInstance().getReference("RecurringTransactions");
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child(currentUser.getUid()).orderByChild("account").equalTo(Wallet).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checks=true;
                Datatrasmit dp= new Datatrasmit();
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
