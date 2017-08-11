package ccpe001.familywallet.transaction;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import ccpe001.familywallet.R;
import ccpe001.familywallet.Validate;

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
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    public TransactionMain() {
        // Required empty public constructor
    }

        List<TransactionDetails> tdList;
        List<String> keys;
        List<String> checkedPosition;
        TransactionListAdapter adapter;
        String userID;
        String familyID;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.transaction_main, container, false);
        list = (ListView) view.findViewById(R.id.transactionList);
        tdList = new ArrayList<>();
        keys = new ArrayList<>();
        checkedPosition = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        FirebaseDatabase.getInstance().getReference("UserInfo").child(userID).child("familyId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                familyID=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Query query = FirebaseDatabase.getInstance().getReference("Transactions").orderByChild("date");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tdList.clear();
                //keys.clear();
                for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                    TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                    if (familyID.equals(td.getFamilyID())){
                        tdList.add(td);
                        keys.add(tdSnapshot.getKey());
                    }



                }
                Collections.reverse(tdList);
                Collections.reverse(keys);
                adapter = new TransactionListAdapter(getActivity(),tdList);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("Transactions");
        mDatabase.keepSynced(true);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
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

                if(isOpen){
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
                }
                else {
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
                            intent.putExtra("transactionType","Income");
                            intent.putExtra("Update","False");
                            startActivity(intent);
                        }
                    });
                    fab_expense.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent("ccpe001.familywallet.AddTransaction");
                            intent.putExtra("transactionType","Expense");
                            intent.putExtra("Update","False");
                            startActivity(intent);
                        }
                    });
                }
            }
        });






        return view;
    }

    private void deleteTransaction(String key){
        DatabaseReference transaction = FirebaseDatabase.getInstance().getReference("Transactions").child(key);
        transaction.removeValue();
    }
    private void editTransaction(final String key){
        final Validate v = new Validate();
        DatabaseReference transaction = FirebaseDatabase.getInstance().getReference("Transactions").child(key);
        transaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransactionDetails td = dataSnapshot.getValue(TransactionDetails.class);
                Intent intent = new Intent("ccpe001.familywallet.AddTransaction");
                intent.putExtra("Update","True");
                intent.putExtra("key",key);
                intent.putExtra("title",td.getTitle());
                intent.putExtra("amount",td.getAmount());
                intent.putExtra("date",v.valueToDate(td.getDate()));
                intent.putExtra("time",td.getTime());
                intent.putExtra("categoryName",td.getCategoryName());
                intent.putExtra("categoryID",td.getCategoryID());
                intent.putExtra("location",td.getLocation());
                intent.putExtra("currencyIndex",td.getCurrency());
                intent.putExtra("accountIndex",td.getAccount());
                intent.putExtra("transactionType",td.getType());
                intent.putExtra("userID",td.getUserID());
                intent.putExtra("familyID",td.getFamilyID());
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
