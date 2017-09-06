package ccpe001.familywallet.budget;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ccpe001.familywallet.R;

public class addAccount extends Fragment  {
    String m_txt="",validbank,isPrivate="False",Notify="False",currtype;
    String familyId="not assigned";
    boolean check=false,msgBoxOut=false;
    private static DatabaseReference mDatabases;
    Button btnSubmit,btnUpdate;
    EditText accName,accAmountss;
    private String[] arraySpinner,arraySpinner1;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.arraySpinner = new String[] {
                "Wallet", "Bank account"
        };
        this.arraySpinner1 = new String[] {
                "USD", "LKR"
        };
        /*database*/
        View v = inflater.inflate(R.layout.add_account,container, false);
        final ArrayList<String> providerlist1= new ArrayList<String>();
        Spinner s1 = (Spinner) v.findViewById(R.id.curType);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, arraySpinner1);
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
        Spinner s = (Spinner) v.findViewById(R.id.accType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
        android.R.layout.simple_dropdown_item_1line, arraySpinner);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new OnItemSelectedListener() {
            private AlertDialog.Builder box=null;
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                Toast.makeText(getActivity(), selected, Toast.LENGTH_LONG).show();
                if(selected=="Bank account"){
                    box = new AlertDialog.Builder(getContext());
                    box.setTitle("Attention");
                    box.setMessage("Enter bank account ID :");
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT );
                    box.setView(input);
                    box.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_txt = input.getText().toString();
                            if(m_txt.matches(".*[a-z0-9].*")){
                                check=true;
                                validbank=m_txt;
                                alertBox.alertBoxOut(getContext(),"Succeed","BAnk account added");
                            }
                            else{
                                check=false;
                                alertBox.alertBoxOut(getContext(),"Failed","Wrong input");
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
        Switch sw = (Switch)v.findViewById(R.id.swtNotify);
        Switch sw1 = (Switch)v.findViewById(R.id.swcPrivate);
        sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Notify="True";
                }
                else {
                    Notify="False";
                }

            }
        });
        if(sw.isChecked()){
            Notify="True";
        }
        else {
            Notify="False";
        }
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
        if(sw.isChecked()){
            isPrivate="True";
        }
        else {
            isPrivate="False";
        }
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
        accName=(EditText)v.findViewById(R.id.editText5);
        accAmountss=(EditText)v.findViewById(R.id.editText7);
        btnSubmit=(Button)v.findViewById(R.id.btnAdd);
        btnSubmit.setOnClickListener(new OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             acountCtrl Ctrl = new acountCtrl();
                                             final String accountName = accName.getText().toString();
                                             Double amount = Double.parseDouble(accAmountss.getText().toString());
                                             boolean xyzx=actionValidater.accountName(accountName);
                                             if (xyzx) {
                                                 if (check) {
                                                     msgBoxOut = (Ctrl.addDataAcc(currentUser.getUid(), accountName, amount, "Bank Account", validbank, isPrivate, Notify, currtype, familyId));
                                                 } else {
                                                     msgBoxOut = (Ctrl.addDataAcc(currentUser.getUid(), accountName, amount, "Wallet", "Wallet", isPrivate, Notify, currtype, familyId));

                                                 }
                                                 if (msgBoxOut) {
                                                     alertBox.alertBoxOut(getContext(), "Data Stored", "Succeed");
                                                     accName.setText("");
                                                     accAmountss.setText("");
                                                 }
                                             } else {
                                                 alertBox.alertBoxOut(getContext(), "Account Name ", "Change your account name");
                                             }
                                         }
                                     });
                btnUpdate = (Button) v.findViewById(R.id.btnUpdate);
                btnUpdate.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), accViews.class);
                        startActivity(intent);
                    }
                });


                return v;
            }
        }
