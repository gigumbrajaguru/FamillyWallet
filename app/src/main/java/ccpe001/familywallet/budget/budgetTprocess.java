package ccpe001.familywallet.budget;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Gigum on 2017-09-11.
 */

public class budgetTprocess {
    private DatabaseReference mDatabases;
    dumpData dp = new dumpData();
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public void autoUpdateBudget(){
        getbudgetData();

    }

    public void getbudgetData() {
        mDatabases = FirebaseDatabase.getInstance().getReference();
        mDatabases.child("Budget").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int roundB=0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals("BudgetName")) {
                        dp.setBudgetName(child.getValue().toString());
                    }
                    if (child.getKey().equals("Amount")) {
                        dp.setamount(child.getValue().toString());
                    }
                    if (child.getKey().equals("addD")) {
                        dp.setaddD(child.getValue().toString());
                    }
                    if (child.getKey().equals("catagory")) {
                        dp.setcatagory(child.getValue().toString());
                    }
                    if (child.getKey().equals("endDays")) {
                        dp.setendDays(child.getValue().toString());
                    }
                    if (child.getKey().equals("familyId")) {
                        dp.setfamilyId(child.getValue().toString());
                    }
                    if (child.getKey().equals("notification")) {
                        dp.setnotification(child.getValue().toString());
                    }
                    if (child.getKey().equals("percentage")) {
                        dp.setpercentage(child.getValue().toString());
                    }
                    if (child.getKey().equals("startDate")) {
                        dp.setstatus(child.getValue().toString());
                    }
                    if (child.getKey().equals("status")) {
                        dp.setstartDate(child.getValue().toString());
                    }
                    if(roundB==10){
                        getTransactionDetails();
                    }
                    roundB++;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getTransactionDetails(){
        mDatabases = FirebaseDatabase.getInstance().getReference("Transactions");
        mDatabases.child(currentUser.getUid()).orderByChild("categoryName").equalTo(dp.getcatagory()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
