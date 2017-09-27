package ccpe001.familywallet.transaction;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ccpe001.familywallet.PeriodicBackupCaller;
import ccpe001.familywallet.R;
import ccpe001.familywallet.Splash;
import ccpe001.familywallet.Translate;
import ccpe001.familywallet.Validate;
import ccpe001.familywallet.budget.addAccount;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionMain extends Fragment {

    ListView list;
    FloatingActionButton fab_income, fab_expense,fab_main;
    Animation fabOpen, fabClose, fabClockwise, fabAntiClockwise;
    TextView txtIncome,txtExpense;
    boolean isOpen = false;
    private DatabaseReference mDatabase;
    Validate v = new Validate();
    Translate trns = new Translate();
    List<TransactionDetails> tdList;
    List<String> keys, checkedPosition, accountsList, groupKeys;
    TransactionListAdapter adapter;
    String userID, familyID, InGroup;
    Resources res;
    public TransactionMain() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.transaction_main, container, false);
        fabAddMenu(view);
        list = (ListView) view.findViewById(R.id.transactionList);
        tdList = new ArrayList<>();
        groupKeys = new ArrayList<>();
        keys = new ArrayList<>();
        checkedPosition = new ArrayList<>();
        res = getResources();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){

        }

        SharedPreferences sharedPref = getContext().getSharedPreferences("fwPrefs",0);
        String uid = sharedPref.getString("uniUserID", "");
        String fid = sharedPref.getString("uniFamilyID", "");
        InGroup = sharedPref.getString("InGroup", "");
        userID = uid;
        familyID = fid;




        /* gcmNetworkManager api method scheduling to run the recurring transactions daily*/
        GcmNetworkManager gcmRecuTrans = GcmNetworkManager.getInstance(getActivity());
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(recurringService.class)
                .setPeriod(86400)//recurring daily
                .setTag("recurringTransaction")
                .setPersisted(true)
                .build();
        gcmRecuTrans.schedule(task);

        mDatabase = FirebaseDatabase.getInstance().getReference("Transactions");
        mDatabase.keepSynced(true);

        accountsList = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance().getReference("Account").orderByChild("user").equalTo(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accountsList.clear();
                for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                    accountsList.add(tdSnapshot.child("accountName").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        try{
            Query query2 = FirebaseDatabase.getInstance().getReference("Groups").orderByChild(familyID);
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groupKeys.clear();
                    for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                        groupKeys.add(tdSnapshot.getKey());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){

        }

           try{

               Query query3;
               if (familyID.equals(userID) && !InGroup.equals("true")){
                   query3 = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date");
               }
               else {
                   query3 = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");

               }
                query3.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tdList.clear();
                        for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                            TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                            if (td.getUserID().equals(userID)){
                                tdList.add(tdSnapshot.getValue(TransactionDetails.class));
                                keys.add(tdSnapshot.getKey());
                            }
                        }

                        try{
                            Collections.reverse(tdList);
                            Collections.reverse(keys);
                            adapter = new TransactionListAdapter(getActivity(),tdList);
                            list.setAdapter(adapter);
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

           }catch (Exception e){

           }





        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewTransaction(keys.get(position));
            }
        });
        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            MenuItem deleteIcon, editIcon;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedItems = list.getCheckedItemCount();

                mode.setTitle(String.valueOf(checkedItems)+ " Selected");
                if (checked==true) {
                    checkedPosition.add(String.valueOf(position));
                }
                else if (checked==false) {
                    checkedPosition.remove(String.valueOf(position));

                }
                if (checkedPosition.size()>1)
                    editIcon.setVisible(false);
                else
                    editIcon.setVisible(true);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.transaction_main, menu);

                deleteIcon = menu.findItem(R.id.delete_id);
                editIcon = menu.findItem(R.id.edit_id);
                checkedPosition.clear();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_id: new AlertDialog.Builder(getActivity())
                            .setTitle("Delete")
                            .setMessage("Do you really want to Delete this?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    for (String checkedKey : checkedPosition){
                                        deleteTransaction(keys.get(Integer.parseInt(checkedKey)));
                                    }

                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                        mode.finish();
                        return true;
                    case R.id.edit_id:
                                    for (String checkedKey : checkedPosition){
                                        editTransaction(keys.get(Integer.parseInt(checkedKey)));
                                    }


                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }


        });
        return view;
    }

    private void viewTransaction(final String key) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.transaction_view);
        final DatabaseReference transaction;
        if (familyID.equals(userID) && !InGroup.equals("true")){
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).child(key);
        }
        else {
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key);
        }
        TextView vhTitle = (TextView) dialog.findViewById(R.id.vhTitle);
        TextView vhAmount = (TextView) dialog.findViewById(R.id.vhAmount);
        TextView vhCategory = (TextView) dialog.findViewById(R.id.vhCategory);
        TextView vhAccount = (TextView) dialog.findViewById(R.id.vhAccount);
        TextView vhDate = (TextView) dialog.findViewById(R.id.vhDate);
        TextView vhTime = (TextView) dialog.findViewById(R.id.vhTime);
        TextView vhLocation = (TextView) dialog.findViewById(R.id.vhLocation);
        vhTitle.setText(res.getString(R.string.vhTitle));
        vhAmount.setText(res.getString(R.string.vhAmount));
        vhCategory.setText(res.getString(R.string.vhCategory));
        vhAccount.setText(res.getString(R.string.vhAccount));
        vhDate.setText(res.getString(R.string.vhDate));
        vhTime.setText(res.getString(R.string.vhTime));
        vhLocation.setText(res.getString(R.string.vhLocation));

        transaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransactionDetails td = dataSnapshot.getValue(TransactionDetails.class);
                TextView vTitle = (TextView) dialog.findViewById(R.id.vTitle);
                TextView vAmount = (TextView) dialog.findViewById(R.id.vTxtAmount);
                TextView vCategory = (TextView) dialog.findViewById(R.id.vTxtCategory);
                TextView vAccount = (TextView) dialog.findViewById(R.id.vTxtAccount);
                TextView vDate = (TextView) dialog.findViewById(R.id.vTxtDate);
                TextView vTime = (TextView) dialog.findViewById(R.id.vTxtTime);
                TextView vLocation = (TextView) dialog.findViewById(R.id.vTxtLocation);
                Button vEdit = (Button) dialog.findViewById(R.id.btnEdit);
                Button vCancel = (Button) dialog.findViewById(R.id.btnCancel);
                if (td.getTitle().isEmpty())
                    vTitle.setText("Title Not Available");
                else
                    vTitle.setText(td.getTitle());
                vAmount.setText(trns.currencyView(td.getCurrency(),getActivity())+td.getAmount());
                vCategory.setText(trns.categoryView(td.getCategoryName(),getActivity()));
                vAccount.setText(td.getAccount());
                vDate.setText(trns.dateView(td.getDate(),getContext()));
                vTime.setText(trns.timeView(td.getTime(),getActivity()));
                SpannableString content = new SpannableString(td.getLocation());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                vLocation.setText(content);
                if (vLocation.getText().equals(null)){
                    vLocation.setClickable(false);
                }
                else {
                    vLocation.setClickable(true);
                }

                final String location = td.getLocation();
                vLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String map = "http://maps.google.co.in/maps?q=" + location;
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(map));
                        startActivity(intent);
                    }
                });
                vEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editTransaction(key);
                    }
                });

                vCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void deleteTransaction(String key){
        final DatabaseReference transaction;
        if (familyID.equals(userID) && !InGroup.equals("true")){
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).child(key);
        }
        else {
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key);
        }
        transaction.removeValue();
    }

    private void editTransaction(final String key){
        final DatabaseReference transaction;
        if (familyID.equals(userID) && !InGroup.equals("true")){
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).child(key);
        }
        else {
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key);
        }
        transaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransactionDetails td = dataSnapshot.getValue(TransactionDetails.class);
                Intent intent = new Intent("ccpe001.familywallet.AddTransaction");
                intent.putExtra("Update","True");
                intent.putExtra("key",key);
                intent.putExtra("title",td.getTitle());
                intent.putExtra("amount",td.getAmount());
                intent.putExtra("date",trns.dateView(td.getDate(),getContext()));
                intent.putExtra("time",trns.timeView(td.getTime(),getContext()));
                intent.putExtra("categoryName",trns.categoryView(td.getCategoryName(),getContext()));
                intent.putExtra("categoryID",td.getCategoryID());
                intent.putExtra("location",td.getLocation());
                intent.putExtra("currency",trns.currencyView(td.getCurrency(),getContext()));
                intent.putExtra("account",td.getAccount());
                intent.putExtra("transactionType",td.getType());
                intent.putExtra("userID",td.getUserID());
                intent.putExtra("familyID",td.getFamilyID());
                intent.putExtra("previousAmount",td.getAmount());
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fabAddMenu(View view){
        txtExpense = (TextView) view.findViewById(R.id.txtExpense);
        txtIncome = (TextView) view.findViewById(R.id.txtIncome);
        fab_main = (FloatingActionButton) view.findViewById(R.id.fabMain);
        fab_expense = (FloatingActionButton) view.findViewById(R.id.fabExpense);
        fab_income = (FloatingActionButton) view.findViewById(R.id.fabIncome);
        fabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fabClockwise = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_clockwise);
        fabAntiClockwise = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_anticlockwise);
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (accountsList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage(R.string.noAccMsg)
                            .setTitle(R.string.noAccTitle);
                    builder.setPositiveButton(R.string.noAccBtnAdd, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                                    .beginTransaction();
                            addAccount addwallet = new addAccount();
                            fragmentTransaction.replace(R.id.fragmentContainer1, addwallet);
                            fragmentTransaction.commit();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    if (isOpen) {
                        fab_income.startAnimation(fabClose);
                        fab_expense.startAnimation(fabClose);
                        txtExpense.setAnimation(fabClose);
                        txtIncome.setAnimation(fabClose);
                        txtExpense.setClickable(false);
                        txtIncome.setClickable(false);
                        fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1976d2")));
                        fab_main.setScaleType(ImageView.ScaleType.CENTER);
                        fab_main.setImageResource(R.mipmap.add_transaction);
                        fab_income.setClickable(false);
                        fab_expense.setClickable(false);
                        isOpen = false;
                    } else {
                        fab_income.startAnimation(fabOpen);
                        fab_expense.startAnimation(fabOpen);
                        txtExpense.setAnimation(fabOpen);
                        txtIncome.setAnimation(fabOpen);
                        txtExpense.setClickable(true);
                        txtIncome.setClickable(true);
                        fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffcc0000")));
                        fab_main.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        fab_main.setImageResource(R.mipmap.cancel);
                        fab_income.setClickable(true);
                        fab_expense.setClickable(true);
                        isOpen = true;
                    }
                    if (isOpen) {
                        fab_income.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent("ccpe001.familywallet.AddTransaction");
                                intent.putExtra("transactionType", "Income");
                                intent.putExtra("Update", "False");
                                startActivity(intent);
                            }
                        });
                        fab_expense.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent("ccpe001.familywallet.AddTransaction");
                                intent.putExtra("transactionType", "Expense");
                                intent.putExtra("Update", "False");
                                startActivity(intent);

                            }
                        });
                    }
                }
            }
        });
    }


}
