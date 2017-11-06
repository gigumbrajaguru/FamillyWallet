package ccpe001.familywallet.budget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
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
import ccpe001.familywallet.Translate;

import static java.security.AccessController.getContext;

public class AccountViews extends AppCompatActivity {
    private DatabaseReference mDatabases;
    Object[] arraysave = {}, arrayaccname = {}, arrayamount = {}, arrayk = {}, accTypes, curty;
    String[] arrayIssaving = {}, accNamearay = {}, arraamonts = {}, acckey = {}, acTypes, curtyp;
    String[] amonts, accName, issaving, acckeys, types, curtype;
    Integer[] imgId2 = {};
    private  String[] arraySpinner,arraySpinner1;
    String accnameD,saving;
    int check;
    AccountListAd addList;
    final List<String> lkey = new ArrayList<String>();
    final List<String> lAname = new ArrayList<String>();
    final List<String> lAccName = new ArrayList<String>();
    final List<String> lsaving = new ArrayList<String>();
    final List<String> acctype = new ArrayList<String>();
    final List<String> curt = new ArrayList<String>();
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acc_views);
        setTitle(getString(R.string.accountview));
        ListView accList = (ListView) findViewById(R.id.list1);
        AutoRefresh();
        accList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = ((TextView) view.findViewById(R.id.txtbudgetId)).getText().toString();
                setPromptBox(selected);
            }
        });
        FloatingActionButton f = (FloatingActionButton)findViewById(R.id.fab1);
        f.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newInt3 = new Intent(AccountViews.this, AddAccount.class);
                startActivity(newInt3);
            }
        });
    }
    public boolean setList() {
        mDatabases = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabases.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String accname = child.child("accountName").getValue().toString();
                        String save = child.child("isSaving").getValue().toString();
                        String accamount = child.child("amount").getValue().toString();
                        String actypes = child.child("types").getValue().toString();
                        String currencytypes = child.child("curType").getValue().toString();
                        String keys = child.getKey().toString();
                        curt.add(currencytypes);
                        acctype.add(actypes);
                        lkey.add(keys);
                        lAname.add(accamount);
                        lAccName.add(accname);
                        lsaving.add(save);
                    }
                    curty = new Object[curt.size()];
                    curtyp = new String[curt.size()];
                    accTypes = new Object[acctype.size()];
                    acTypes = new String[acctype.size()];
                    arrayk = new Object[lkey.size()];
                    acckey = new String[lkey.size()];
                    arrayamount = new Object[lAname.size()];
                    arraamonts = new String[lAname.size()];
                    arrayaccname = new Object[lAccName.size()];
                    accNamearay = new String[lAccName.size()];
                    arraysave = new Object[lsaving.size()];
                    arrayIssaving = new String[lsaving.size()];
                    imgId2 = new Integer[lAccName.size()];
                    arrayamount = lAname.toArray();
                    arrayaccname = lAccName.toArray();
                    arraysave = lsaving.toArray();
                    accTypes = acctype.toArray();
                    arrayk = lkey.toArray();
                    curty = curt.toArray();
                    Translate getcat = new Translate();
                    for (int y = 0; y < arrayk.length; y++) {
                        arraamonts[y] = (String) arrayamount[y];
                        accNamearay[y] = (String) arrayaccname[y];
                        arrayIssaving[y] = (String) arraysave[y];
                        acTypes[y] = (String) accTypes[y];
                        acckey[y] = (String) arrayk[y];
                        curtyp[y] = (String) curty[y];
                        imgId2[y] = getcat.getCategoryID("Bill");
                    }
                    lkey.clear();
                    lAname.clear();
                    lAccName.clear();
                    lsaving.clear();
                    acctype.clear();
                    curt.clear();
                    pushList(arraamonts, accNamearay, arrayIssaving, imgId2, acckey, acTypes, curtyp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return true;
    }
    public void pushList(String amont[], String accN[], String isaving[], Integer imgI[], String accokeys[], String[] typesaccount, String[] cut) {
        Integer[] imgId;
        ListView budList = (ListView) findViewById(R.id.list1);
        types = typesaccount.clone();
        amonts = amont.clone();
        curtype = cut.clone();
        accName = accN.clone();
        issaving = new String[isaving.length];
        for (int x = 0; x < isaving.length; x++) {
            if (isaving[x].equals("True")) {
                issaving[x] = "Saving Account";
            } else {
                issaving[x] = "";
            }
        }
        imgId = imgI.clone();
        acckeys = accokeys.clone();
        if (getContext() != null) {
            addList = new AccountListAd(AccountViews.this, accName, amonts, issaving, imgId, acckeys, types, curtype);
            budList.setAdapter(addList);
        }
    }
    private void AutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setList();
                AutoRefresh();
            }
        }, 500);
    }

    public void setPromptBox(String passed) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AccountViews.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View alertlayout = inflater.inflate(R.layout.accpromptbox, null);
        final TextView accname,accamount,acctype,bankid,curtyp,issaving;
        accname=(TextView)alertlayout.findViewById(R.id.accName);
        accamount=(TextView)alertlayout.findViewById(R.id.accAmounts);
       final Switch swe=(Switch)alertlayout.findViewById(R.id.swet);
        acctype=(TextView)alertlayout.findViewById(R.id.acctypess);
        bankid=(TextView)alertlayout.findViewById(R.id.bankIds);
        curtyp=(TextView)alertlayout.findViewById(R.id.curtyp);
        swe.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saving="True";
                }
                else {
                    saving="False";
                }
            }
        });
        if(swe.isChecked()){
            saving="True";
        }
        else {
            saving="False";
        }
        mDatabases = FirebaseDatabase.getInstance().getReference("Account");
        mDatabases.child(passed).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals("accountName")) {
                            accname.setText(child.getValue().toString());
                            accnameD=child.getValue().toString();
                        }
                        else if (child.getKey().equals("amount")) {
                            accamount.setText(child.getValue().toString());
                        }
                        else if (child.getKey().equals("types")) {
                            acctype.setText(child.getValue().toString());
                        }
                        else if (child.getKey().equals("bankID")) {
                            bankid.setText(child.getValue().toString());
                        }
                        else if (child.getKey().equals("curType")) {
                            curtyp.setText(child.getValue().toString());
                        }
                        else if (child.getKey().equals("isSaving")) {
                            if(child.getValue().toString().equals("True")) {
                                swe.setChecked(true);
                            }
                            else{
                                swe.setChecked(false);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        builder.setPositiveButton(getString(R.string.updates),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertupdate(accnameD,saving);
                    }
                });

        builder.setNeutralButton(getString(R.string.deletes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDel(accnameD);
                    }
                });

        builder.setNegativeButton(getString(R.string.cancels),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setView(alertlayout);
        builder.create().show();
    }
    public void alertDel(final String accnam) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountViews.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.deletemsg);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                walletDelete(accnam);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void alertupdate(final String accnam,final String switchs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountViews.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.deletemsg);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                walletUpdate(accnam,switchs);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void walletDelete(final String acconame) {

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabases = FirebaseDatabase.getInstance().getReference();
        mDatabases.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.child("accountName").getValue().toString().equals(acconame)) {
                        ActionValidater actionValidater=new ActionValidater();
                        if(actionValidater.rectransactionChecker(acconame)) {
                            child.getRef().removeValue();
                            AlertBox.alertBoxOut(AccountViews.this,getString(R.string.success),getString(R.string.deletedmsg));
                        }
                        else{
                            AlertBox.alertBoxOut(AccountViews.this,getString(R.string.error),getString(R.string.errormsg));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }
    public void walletUpdate(final String accNames,final String switchs){
        check=0;
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabases = FirebaseDatabase.getInstance().getReference();
        mDatabases.child("Account").orderByChild("user").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if(child.child("accountName").getValue().toString().equals(accNames)) {
                        String newValue = switchs;
                        if (check == 0) {
                            child.getRef().child("isSaving").setValue(newValue);
                            AlertBox.alertBoxOut(AccountViews.this,getString(R.string.success),getString(R.string.updatedmsg));
                            check = 1;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}









