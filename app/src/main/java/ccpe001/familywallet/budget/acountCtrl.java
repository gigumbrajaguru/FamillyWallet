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
    String lastUpdated,adddate,lastUpdateds,stat;
    public DatabaseReference databaseReference;
    public FirebaseAuth firebaseAuth;


    /** this method use to add account data to firebase **/
    public boolean addDataAcc(String user,String accountName,Double amount,String types,String bankID,String isPrivate,String curType,String familyId){
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String addDate = df.format(c.getTime());
        lastUpdated="Not Updated";
        DataStores insertData=new DataStores(user,accountName,amount,types,bankID,addDate,lastUpdated,isPrivate,curType,familyId);
        DatabaseReference childD=databaseReference.child("Account");
        childD.push().setValue(insertData);

        return true;
    }
    /** this method use to add budget data to firebase **/
    public boolean addbdget(String uname,String Fname,String bname,String strDate,String endDate,String amount,String tnotify,String catogary){
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        adddate = df.format(c.getTime());
        lastUpdated="Not Updated";
        stat="Good";
        bdatastore insertData=new bdatastore(uname,Fname,bname,strDate,endDate,amount,tnotify,catogary,adddate,lastUpdateds,stat);
        DatabaseReference childD=databaseReference.child("Budget");
        childD.push().setValue(insertData);

        return true;
    }

}
