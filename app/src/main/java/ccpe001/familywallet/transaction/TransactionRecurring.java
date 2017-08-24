package ccpe001.familywallet.transaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

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


public class TransactionRecurring extends Fragment {

    ListView list;
    TextView emptyText;

    List<TransactionDetails> tdList;
    List<String> keys;
    List<String> checkedPosition;
    TransactionRecurListAdapter adapter;
    String userID;
    String familyID;


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    public TransactionRecurring() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.transaction_recurring, container, false);
        list = (ListView) view.findViewById(R.id.transactionListR);
        emptyText = (TextView) view.findViewById(android.R.id.empty);
        list.setEmptyView(emptyText);

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

        Query query = FirebaseDatabase.getInstance().getReference("RecurringTransactions").orderByChild("date");
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
                adapter = new TransactionRecurListAdapter(getActivity(),tdList);
                list.setAdapter(adapter);
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

        return view;
    }

    private void deleteTransaction(String key){
        DatabaseReference transaction = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(key);
        transaction.removeValue();
    }
    private void editTransaction(final String key){
        final Validate v = new Validate();
        DatabaseReference transaction = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(key);
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
                intent.putExtra("templateChecked",true);
                intent.putExtra("recurrPeriod",td.getRecurringPeriod());
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
