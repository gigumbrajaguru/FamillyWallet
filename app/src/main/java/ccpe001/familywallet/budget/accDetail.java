package ccpe001.familywallet.budget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ccpe001.familywallet.R;

public class accDetail extends AppCompatActivity {
    private String[] arraySpinner;
    Button btUpdate, btDelete;
    int h=0;
    String keys,accountNames,typess,bankIDs,addDates,lastUpdateds,isPrivates,Notifys,curTypess;
    TextView accId,curType,accStat,accDate;
    EditText amounts;
    private DatabaseReference mDatabase,mDatabases;
    public String getUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userMail", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_detail);
        this.arraySpinner = new String[]{""};
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Spinner s = (Spinner) findViewById(R.id.spinner);
        mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Account").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();
                Spinner s = (Spinner) findViewById(R.id.spinner);
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                        String xy = (String) child.child("accountName").getValue();
                        areas.add(xy);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(accDetail.this,android.R.layout.simple_dropdown_item_1line, areas){
                    public View getDropDownView(int position, View convertView,ViewGroup parent) {

                        View v = super.getDropDownView(position, convertView,parent);

                        ((TextView) v).setGravity(Gravity.CENTER);

                        return v;

                    }
                };

                s.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btUpdate = (Button) findViewById(R.id.btnUpdate);
        btDelete = (Button) findViewById(R.id.transfer);
        accId=(TextView)findViewById(R.id.accId) ;
        curType=(TextView)findViewById(R.id.curType) ;
        accStat=(TextView)findViewById(R.id.accStat) ;
        accDate=(TextView)findViewById(R.id.accDate) ;
        amounts=(EditText)findViewById(R.id.accAmount);
        mDatabases = FirebaseDatabase.getInstance().getReference();
       s.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                final String selected = parent.getItemAtPosition(pos).toString();
                mDatabases.child("Account").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (selected == (String)child.child("accountName").getValue()) {
                                keys = child.getKey();
                                accountNames = (String) child.child("accountName").getValue();
                                typess = (String) child.child("types").getValue();
                                bankIDs = (String) child.child("bankID").getValue();
                                addDates = (String) child.child("addDate").getValue();
                                lastUpdateds = (String) child.child("lastUpdated").getValue();
                                isPrivates = (String) child.child("isPrivate").getValue();
                                Notifys = (String) child.child("Notify").getValue();
                                curTypess = (String) child.child("curType").getValue();
                            }

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                accId.setText(bankIDs);
                curType.setText(curTypess);
                accStat.setText(isPrivates);
                accDate.setText(addDates);
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}



