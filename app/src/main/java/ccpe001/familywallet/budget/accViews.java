package ccpe001.familywallet.budget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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

public class accViews extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String accountName="", typess = "", bankIDs = "", addDates = "", lastUpdateds = "", issaving = "", curTypess = "", accamountavaialble="",accName="";
    TextView curTypes, accDates, accStats, accIds,accNames;
    EditText accAmounts;
    Object[] array={"Select One"};
    String[] array1=null;
    Button btnDel1,btnUpdate1;
    boolean c,x;
    int check,checkspinner=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_views);
        if(actionValidater.accountChecker()){

        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final List<String> areas = new ArrayList<String>();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                areas.add("Select One");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String xy = (String) child.child("accountName").getValue();
                    areas.add(xy);

                }
                array = new Object[areas.size()];
                array1 = new String[areas.size()];
                array = areas.toArray();
                for (int y = 0; y < array.length; y++) {
                    array1[y] = (String) array[y];
                }
                addSpinner(array1);
                checkspinner=1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        accNames=(TextView) findViewById(R.id.accNameText);
        curTypes = (TextView) findViewById(R.id.curType);
        accDates = (TextView) findViewById(R.id.accDate);
        accStats = (TextView) findViewById(R.id.accStat);
        accIds = (TextView) findViewById(R.id.accId);
        accAmounts = (EditText) findViewById(R.id.accAmount);
        Spinner s = (Spinner) findViewById(R.id.accountselect);
        s.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String acconame = parent.getItemAtPosition(position).toString();
                getwalletDetails(acconame);
                curTypes.setText(curTypess);
                accDates.setText(addDates);
                accStats.setText(issaving);
                accIds.setText(bankIDs);
                accNames.setText("Account Name : "+accountName);
                accAmounts.setText(accamountavaialble);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnDel1=(Button)findViewById(R.id.btnDel);
        btnUpdate1=(Button)findViewById(R.id.btnUpdate);
        btnDel1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                walletDelete(accName);
            }
        });
        btnUpdate1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                walletUpdate(accName,accAmounts.getText().toString());

            }
        });
    }
    public void addSpinner(String[] arr) {
        if(checkspinner==0) {
            Spinner s = (Spinner) findViewById(R.id.accountselect);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, arr) {
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = super.getDropDownView(position, convertView, parent);
                    ((TextView) v).setGravity(Gravity.CENTER);
                    return v;
                }
            };
            s.setAdapter(adapter);
        }
    }
    public void getwalletDetails(final String acconame) {
        accName=acconame;
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.child("accountName").getValue().toString().equals(acconame)) {
                        accountName=child.child("accountName").getValue().toString();
                        typess = child.child("types").getValue().toString();
                        bankIDs = child.child("bankID").getValue().toString();
                        addDates = child.child("addDate").getValue().toString();
                        lastUpdateds = child.child("lastUpdated").getValue().toString();
                        issaving = child.child("isSaving").getValue().toString();
                        curTypess = child.child("curType").getValue().toString();
                        accamountavaialble = child.child("amount").getValue().toString();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public boolean walletDelete(final String acconame) {
          x=false;
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if(child.child("accountName").getValue().toString().equals(acconame)) {
                            child.getRef().removeValue();
                            x=true;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        return x;
    }
    public boolean walletUpdate(final String accNames,final String amount){
        check=0;
        c=false;
        final double income=Double.parseDouble(amount);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if(child.child("accountName").getValue().toString().equals(accNames)) {
                        double newValue = income;
                        if (check == 0) {
                            child.getRef().child("amount").setValue(newValue);
                            check = 1;
                        }
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

}



