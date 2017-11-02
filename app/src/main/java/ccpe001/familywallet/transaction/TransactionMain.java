package ccpe001.familywallet.transaction;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Translate;
import ccpe001.familywallet.Validate;
import ccpe001.familywallet.budget.AddAccount;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionMain extends Fragment {

    ListView list;
    FloatingActionButton fab_income, fab_expense,fab_main;
    Animation fabOpen, fabClose, fabClockwise, fabAntiClockwise;
    CoordinatorLayout fabLayout;
    ProgressBar trnsMainProgressBar;
    TextView txtIncome,txtExpense;
    boolean isOpen = false;
    private DatabaseReference mDatabase, gDatabase;
    Validate v = new Validate();
    Translate trns = new Translate();
    List<TransactionDetails> tdList;
    List<String> keys, checkedPosition, accountsList, groupKeys;
    TransactionListAdapter adapter;
    String userID = "uid", familyID="fid", InGroup="false";
    Resources res;

    private String tId;

    public TransactionMain() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.transaction_main, container, false);
        trnsMainProgressBar = (ProgressBar) view.findViewById(R.id.trnsMainProgressBar);
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
        String uid = sharedPref.getString("uniUserID", "uid");
        String fid = sharedPref.getString("uniFamilyID", "fid");
        InGroup = sharedPref.getString("InGroup", "false");
        userID = uid;
        familyID = fid;



        /* gcmNetworkManager api method scheduling to run the recurring transactions daily*/
        GcmNetworkManager gcmRecuTrans = GcmNetworkManager.getInstance(getActivity());
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(RecurringService.class)
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
               filterByQuery(query3);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search) {
            return true;
        }
        else if (id == R.id.filter_date){
            filterTransaction("date");
        }
        else if (id == R.id.filter_account){
            filterTransaction("account");
        }
        else if (id == R.id.filter_amount){
            filterTransaction("amount");
        }
        else if (id == R.id.filter_category){
            filterTransaction("category");
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewTransaction(final String key) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.transaction_view);
        final DatabaseReference transaction;
        if (familyID.equals(userID) && !InGroup.equals("true")){
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).child(key);
            tId = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).child(key).getKey();
        }
        else {
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key);
            tId = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key).getKey();
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

    private void deleteTransaction(String key){
        final DatabaseReference transaction;
        if (familyID.equals(userID) && !InGroup.equals("true")){
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).child(key);
        }
        else {
            transaction = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key);
        }
        transaction.removeValue();

        /*used when deleting scanned image*/
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("ScannedBills/" + userID+"/"+key+ ".jpg").delete();
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
        fabLayout = (CoordinatorLayout) view.findViewById(R.id.fabLayout);
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
                            Intent newInt3 = new Intent(getContext(), AddAccount.class);
                            startActivity(newInt3);
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
                        fabLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        list.setVisibility(View.VISIBLE);
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
                        fabLayout.setBackgroundColor(Color.parseColor("#BF000000"));
                        list.setVisibility(View.GONE);
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

    private void filterTransaction(String filterType) {

        final Dialog dialog = new Dialog(getContext());

        final Integer[] imgid = {
                R.drawable.cat1,R.drawable.cat2,R.drawable.cat3,R.drawable.cat4,
                R.drawable.cat5,R.drawable.cat6,R.drawable.cat7,R.drawable.cat8,R.drawable.cat9,
                R.drawable.cat10,R.drawable.cat11,R.drawable.cat12,R.drawable.cat13,R.drawable.cat14,
                R.drawable.cat15,R.drawable.cat16,R.drawable.cat17,R.drawable.cat18,R.drawable.cat19,
                R.drawable.cat_other,
        };
        /*populating itemname array with expense category list */
        final String[] itemname = res.getStringArray(R.array.IncomeCategory);
        if (filterType.equals("date")){
            dialog.setContentView(R.layout.filter_dialog_date);
            filterDate(dialog,getContext());
        }else if (filterType.equals("account")){
            dialog.setContentView(R.layout.filter_dialog_account);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,accountsList);
            ListView lv = (ListView) dialog.findViewById(R.id.filterAccountList);
            lv.setAdapter(arrayAdapter);
            filterAccounts(dialog);
        }else if (filterType.equals("amount")){
            dialog.setContentView(R.layout.filter_dialog_amount);
            filterAmount(dialog);
        }else if (filterType.equals("category")){
            dialog.setContentView(R.layout.filter_dialog_category);
            GridView gridd = (GridView) dialog.findViewById(R.id.filterCatergoryGrid);
            CategoryAdapter adapter = new CategoryAdapter(getActivity(), itemname, imgid);  //Sending list to category adapter
            gridd.setAdapter(adapter);
            filterCategory(dialog,itemname);
        }




        dialog.show();
    }

    public void filterDate(Dialog view, final Context con) {

        DatePicker dp = (DatePicker) view.findViewById(R.id.filterDatepicker);
        final EditText startDate = (EditText) view.findViewById(R.id.etxtStartDate);
        final EditText endDate = (EditText) view.findViewById(R.id.etxtEndDate);
        final Button btnfilter = (Button) view.findViewById(R.id.btnFilterDate);

        Calendar calendar = Calendar.getInstance();
        dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                String date = trns.dateWithDoubleDigit(year,month+1,day,con);
                Log.d("LOG",""+year+"  "+month+"  "+day);

                if(startDate.isFocused()) {
                    startDate.setText(date);
                }else {
                    endDate.setText(date);
                }
            }
        });




        btnfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startDate.getText().toString().isEmpty() || endDate.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), R.string.emptyDate, Toast.LENGTH_SHORT).show();
                }else {
                    String filterStartdate = trns.dateToValue(startDate.getText().toString());
                    String filterEnddate = trns.dateToValue(endDate.getText().toString());

                    Query query;
                    if (familyID.equals(userID) && !InGroup.equals("true")){
                        query = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date").startAt(filterStartdate).endAt(filterEnddate);
                    }
                    else {
                        query = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date").startAt(filterStartdate).endAt(filterEnddate);
                    }
                    filterByQuery(query);
                }
            }
        });

    }

    public void filterByQuery(Query query){
        try{

            query.addValueEventListener(new ValueEventListener() {
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
    }

    public void filterAmount(Dialog view){

        final EditText etxtAmountFrom = (EditText) view.findViewById(R.id.amountFrom);
        final EditText etxtAmountTo = (EditText) view.findViewById(R.id.amountTo);
        Button btnFilterAmount = (Button) view.findViewById(R.id.btnFilterAmount);


        btnFilterAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etxtAmountFrom.getText().toString().isEmpty() || etxtAmountTo.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), R.string.emptyDate, Toast.LENGTH_SHORT).show();
                }else {
                    Double amountFrom = Double.parseDouble(etxtAmountFrom.getText().toString());
                    Double amountTo = Double.parseDouble(etxtAmountTo.getText().toString());
                    Query query;
                    Log.i("heloo",amountFrom+"--"+amountTo);
                    if (familyID.equals(userID) && !InGroup.equals("true")){
                        query = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date");
                    }
                    else {
                        query = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");
                    }
                    filterByAmountQuery(query,amountFrom,amountTo);
                }
            }
        });

    }

    public void filterByAmountQuery(Query query, final Double amountFrom, final Double amountTo){
        try{

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tdList.clear();
                    for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                        TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                        if (td.getUserID().equals(userID)){
                            Double amount = Double.parseDouble(td.getAmount());
                            if ((amountFrom<=amount) &&(amount<=amountTo)){
                                tdList.add(tdSnapshot.getValue(TransactionDetails.class));
                                keys.add(tdSnapshot.getKey());
                            }
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
    }

    public void filterAccounts(final Dialog view){
        ListView filterAccountList = (ListView) view.findViewById(R.id.filterAccountList);
        filterAccountList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        filterAccountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Query query;
                if (familyID.equals(userID) && !InGroup.equals("true")){
                    query = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date");
                }
                else {
                    query = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");
                    Toast.makeText(getActivity(), "-"+accountsList.get(i), Toast.LENGTH_SHORT).show();
                }
                filterByAccounyQuery(query,accountsList.get(i));
            }
        });


    }

    public void filterByAccounyQuery(Query query, final String accountName){
        try{

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tdList.clear();
                    for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                        TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                        if (td.getUserID().equals(userID)){
                            if (td.getAccount().equals(accountName)){
                                tdList.add(tdSnapshot.getValue(TransactionDetails.class));
                                keys.add(tdSnapshot.getKey());
                            }

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
    }

    public void filterCategory(final Dialog view, final String[] itemname){
        GridView filterCategoryList = (GridView) view.findViewById(R.id.filterCatergoryGrid);
        filterCategoryList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        filterCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Query query;
                if (familyID.equals(userID) && !InGroup.equals("true")){
                    query = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date");
                }
                else {
                    query = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");
                }
                filterByCategoryQuery(query,itemname[i].toString());
            }
        });


    }

    public void filterByCategoryQuery(Query query, final String categoryName){
        try{
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tdList.clear();
                    for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                        TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                        if (td.getUserID().equals(userID)){


                            if (td.getCategoryName().equals(categoryName)){
                                tdList.add(tdSnapshot.getValue(TransactionDetails.class));
                                keys.add(tdSnapshot.getKey());
                            }

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
    }
}
