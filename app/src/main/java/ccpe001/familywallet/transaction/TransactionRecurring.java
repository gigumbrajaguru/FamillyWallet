package ccpe001.familywallet.transaction;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Translate;
import ccpe001.familywallet.Validate;


public class TransactionRecurring extends Fragment {

    private ListView list;
    private TextView emptyText;
    private List<TransactionDetails> tdList;
    private List<String> keys;
    private List<String> checkedPosition;
    private TransactionRecurListAdapter adapter;
    private String userID, familyID;
    private Validate v = new Validate();
    private Translate trns = new Translate();
    private String tId;
    private DatabaseReference mDatabase;

    public TransactionRecurring() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.transaction_recurring_list, container, false);
        list = (ListView) view.findViewById(R.id.transactionListR);
        emptyText = (TextView) view.findViewById(R.id.emptyList);
        setHasOptionsMenu(false);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){

        }

        list.setEmptyView(emptyText);

        tdList = new ArrayList<>();
        keys = new ArrayList<>();
        checkedPosition = new ArrayList<>();

        SharedPreferences sharedPref = getContext().getSharedPreferences("fwPrefs",0);
        String uid = sharedPref.getString("uniUserID", "");
        String fid = sharedPref.getString("uniFamilyID", "");
        userID = uid;
        familyID = fid;

        mDatabase = FirebaseDatabase.getInstance().getReference("RecurringTransactions");
        mDatabase.keepSynced(true);
        try {

            Query query = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(userID).orderByChild("date");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tdList.clear();
                    for (DataSnapshot tdSnapshot : dataSnapshot.getChildren()) {
                        TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                        tdList.add(td);
                        keys.add(tdSnapshot.getKey());
                    }
                    try {
                        Collections.reverse(tdList);
                        Collections.reverse(keys);
                        adapter = new TransactionRecurListAdapter(getActivity(), tdList);
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
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_filter).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
    private void deleteTransaction(String key){
        DatabaseReference transaction = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(familyID).child(key);
        transaction.removeValue();
    }
    private void editTransaction(final String key){

        DatabaseReference transaction = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(familyID).child(key);
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
                intent.putExtra("templateChecked",true);
                intent.putExtra("recurrPeriod",trns.recurringView(td.getRecurringPeriod(),getContext()));
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /* View transaction in a expanded view on a dialog box */
    private void viewTransaction(final String key) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.transaction_recurring_view);
        final DatabaseReference transaction;
            transaction = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(userID).child(key);
            tId = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(userID).child(key).getKey();

        transaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransactionDetails td = dataSnapshot.getValue(TransactionDetails.class);
                TextView vTitle = (TextView) dialog.findViewById(R.id.vRecurTitle);
                TextView vAmount = (TextView) dialog.findViewById(R.id.vRecurTxtAmount);
                TextView vCategory = (TextView) dialog.findViewById(R.id.vRecurTxtCategory);
                TextView vAccount = (TextView) dialog.findViewById(R.id.vRecurTxtAccount);
                TextView vDate = (TextView) dialog.findViewById(R.id.vRecurTxtDate);
                TextView vTime = (TextView) dialog.findViewById(R.id.vRecurTxtTime);
                TextView vRecur = (TextView) dialog.findViewById(R.id.vRecurTxtName);
                TextView vLocation = (TextView) dialog.findViewById(R.id.vRecurTxtLocation);
                Button vCancel = (Button) dialog.findViewById(R.id.btnRecurCancel);
                if (td.getTitle().isEmpty())
                    vTitle.setText(R.string.titleEmpty);
                else
                    vTitle.setText(td.getTitle());
                vAmount.setText(trns.currencyView(td.getCurrency(),getActivity())+td.getAmount());
                vCategory.setText(trns.categoryView(td.getCategoryName(),getActivity()));
                vAccount.setText(td.getAccount());
                vDate.setText(trns.dateView(td.getDate(),getContext()));
                vTime.setText(trns.timeView(td.getTime(),getActivity()));
                vRecur.setText(trns.recurringView(td.getRecurringPeriod(),getContext()));
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
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(map));
                        startActivity(intent);
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

        /*retriving bill image*/
        final ImageView vScan = (ImageView) dialog.findViewById(R.id.imageview_scan);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("ScannedBills/" + userID+"/"+tId+ ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext())
                        .load(uri)
                        .into(vScan);
            }
        });
    }


}
