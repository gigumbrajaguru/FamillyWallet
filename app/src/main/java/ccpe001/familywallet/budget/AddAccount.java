package ccpe001.familywallet.budget;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ccpe001.familywallet.R;

public class AddAccount extends AppCompatActivity {
    String m_txt="",validbank,isPrivate="False",Notify="False",currtype;
    String familyId="not assigned";
    boolean check=false,msgBoxOut=false;
    private static DatabaseReference mDatabases;
    Button btnSubmit;
    EditText accName,accAmountss;
    private String[] arraySpinner,arraySpinner1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_account);
        // Inflate the layout for this fragment
        this.arraySpinner = new String[] {
                "Wallet", "Bank account"
        };
        this.arraySpinner1 = new String[] {
                "LKR", "USD"
        };
        /*database*/
        final ArrayList<String> providerlist1= new ArrayList<String>();
        Spinner s1 = (Spinner)findViewById(R.id.curType);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arraySpinner1);
        s1.setAdapter(adapter1);
        s1.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                 currtype = parent.getItemAtPosition(pos).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                 currtype ="LKR";
            }
        });
        final ArrayList<String> providerlist= new ArrayList<String>();
        Spinner s = (Spinner)findViewById(R.id.accType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_dropdown_item_1line, arraySpinner);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new OnItemSelectedListener() {
            private AlertDialog.Builder box=null;
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                if(selected=="Bank account"){
                    box = new AlertDialog.Builder(AddAccount.this);
                    box.setTitle("Attention");
                    box.setMessage("Enter bank account ID :");
                    final EditText input = new EditText(AddAccount.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT );
                    box.setView(input);
                    box.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_txt = input.getText().toString();
                            if(m_txt.matches(".*[a-z0-9].*")){
                                check=true;
                                validbank=m_txt;
                                AlertBox.alertBoxOut(AddAccount.this,getString(R.string.success),getString(R.string.bankaccount));
                            }
                            else{
                                check=false;
                                AlertBox.alertBoxOut(AddAccount.this,getString(R.string.error),getString(R.string.emptymsg));
                            }
                        }
                    });
                    box.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    box.show();
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Switch sw1 = (Switch)findViewById(R.id.swcPrivate);

        sw1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isPrivate="True";
                }
                else {
                    isPrivate="False";
                }

            }
        });
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabases = FirebaseDatabase.getInstance().getReference();
        mDatabases.child("UserInfo").orderByChild("userId").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    familyId=child.child("familyId").getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        accName=(EditText)findViewById(R.id.editText5);
        accAmountss=(EditText)findViewById(R.id.editText7);
        btnSubmit=(Button)findViewById(R.id.btnAdd);
        btnSubmit.setOnClickListener(new OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             final AccountCtrl Ctrl = new AccountCtrl();
                                             final String accountName = accName.getText().toString();
                                             final Double amount = Double.parseDouble(accAmountss.getText().toString());
                                             ActionValidater actionValidater=new ActionValidater();
                                             if(amount!=null && accountName!=null) {
                                                 if (actionValidater.accountName(accountName)) {
                                                     if (check) {
                                                         msgBoxOut = (Ctrl.addDataAcc(currentUser.getUid(), accountName, amount, "Bank Account", validbank, isPrivate, currtype, familyId));
                                                     } else {
                                                         msgBoxOut = (Ctrl.addDataAcc(currentUser.getUid(), accountName, amount, "Wallet", "Wallet", isPrivate, currtype, familyId));

                                                     }
                                                     if (msgBoxOut) {
                                                         AlertBox.alertBoxOut(AddAccount.this, getString(R.string.success), getString(R.string.storedaccount));
                                                         accName.setText("");
                                                         accAmountss.setText("");
                                                     }
                                                 } else {
                                                     AlertBox.alertBoxOut(AddAccount.this, getString(R.string.error), getString(R.string.storeaccountdetail));
                                                 }
                                             }else{
                                                 AlertBox.alertBoxOut(AddAccount.this, getString(R.string.error), getString(R.string.emptymsg));
                                             }

                                         }
                                     });
            }
        }
