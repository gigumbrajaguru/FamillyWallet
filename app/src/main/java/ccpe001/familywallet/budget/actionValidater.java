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
        static boolean y;
        public static boolean amountCheck(final String AccountName, final double amount){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Account").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                       if((String) child.child("accountName").getValue()==AccountName){
                           if(((Double)child.child("amount").getValue()-amount)>=0){
                               y=true;
                           }
                           else{
                               y=false;
                           }

                       }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        return  y;
        }
        
}
