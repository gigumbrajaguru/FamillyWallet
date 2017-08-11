package ccpe001.familywallet.budget;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Gigum on 2017-07-09.
 */

public class acountCtrl {
    String lastUpdated;
    public DatabaseReference databaseReference;
    public FirebaseAuth firebaseAuth;

    public void addDataAcc(String user,String accountName,Double amount,String types,String bankID,String isPrivate,String Notify,String curType,String family){

        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String addDate = df.format(c.getTime());
        lastUpdated="Not Updated";
        DataStores insertData=new DataStores(user,accountName,amount,types,bankID,addDate,lastUpdated,isPrivate,Notify,curType,family);
        DatabaseReference childD=databaseReference.child("Account");
        childD.push().setValue(insertData);

    }

}
