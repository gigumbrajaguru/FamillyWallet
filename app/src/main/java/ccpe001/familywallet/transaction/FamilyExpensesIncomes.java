package ccpe001.familywallet.transaction;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Knight on 10/20/2017.
 */

public class FamilyExpensesIncomes {

    private Double totalIncome;
    private Double totalExpense;
    public static HashMap<String , Double[]> id_total;

    private DatabaseReference mDatabase;
    /** Default constructor*/
    public FamilyExpensesIncomes(){

    }

    public FamilyExpensesIncomes(final String uID, final String fID){
        id_total = new HashMap<>() ;
        Query query ;
        mDatabase = FirebaseDatabase.getInstance().getReference();
            query = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(fID).orderByChild("date");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalIncome=0.0;
                totalExpense=0.0;
                for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                    TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                    if (td.getUserID().equals(uID)){
                        if (td.getType().equals("Income")){
                            totalIncome=totalIncome+Double.parseDouble(td.getAmount());
                        }
                        else if (td.getType().equals("Expense")){
                            totalExpense=totalExpense+Double.parseDouble(td.getAmount());
                        }

                    }
                }
                Double [] income_expense={totalIncome,totalExpense};
                id_total.put(uID,income_expense);
                Double[] aa = id_total.get(uID);
                Log.i("TestTotalTransactions",""+aa[1]);
                mDatabase.child("Groups").child(fID).child(uID).child("TotalIncome").setValue(totalIncome);
                mDatabase.child("Groups").child(fID).child(uID).child("TotalExpense").setValue(totalExpense);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
