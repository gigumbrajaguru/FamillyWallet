package ccpe001.familywallet.transaction;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Translate;
import ccpe001.familywallet.budget.AddAccount;

import com.squareup.picasso.Picasso;


public class FamilyTransactions extends Fragment {

    static Translate trns = new Translate();
    static HashMap<String,String> KeyName;
    String userID, familyID, InGroup;
    FloatingActionButton fab_income, fab_expense,fab_main;
    Animation fabOpen, fabClose, fabClockwise, fabAntiClockwise;
    TextView txtIncome,txtExpense;
    CoordinatorLayout fabLayout;
    boolean isOpen = false;
    RelativeLayout backgroundLayout;

    List<String> accountsList;


    private DatabaseReference mDatabase, gDatabase;
    private RecyclerView recyclerView, familyRecyclerView;
    private Context context;
    List<GroupDetails> grpList;
    GroupListAdapter grpAdapter;
    ListView memberList, transactionList;
    SlidingUpPanelLayout slide;
    ImageView imgSlide;
    Button btnAlltransaction;
    FamilyTransactionsAdapter adapter;
    List<TransactionDetails> tdList;
    List<String> keys;

    private String tId;


    public FamilyTransactions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.transaction_family_list, container, false);
        fabAddMenu(view);
        /**
         * @grpList - Array to hold group list objects
         * @tdList - Array to hold transaction list objects
         * @keys - Array to hold keys of group members
         *
         */
        grpList = new ArrayList<>();
        tdList = new ArrayList<>();
        keys = new ArrayList<>();
        context=getContext();      //getting current context
        KeyName = new HashMap<>() ;     //Setting hasmap to set user ID and name as key value pair

        memberList = (ListView) view.findViewById(R.id.grpList);
        transactionList = (ListView) view.findViewById(R.id.familyTransactions);
        backgroundLayout = (RelativeLayout) view.findViewById(R.id.backgroundLayout);

        imgSlide = (ImageView) view.findViewById(R.id.imgSlide);
        slide = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        btnAlltransaction = (Button) view.findViewById(R.id.btnAllTransactions);

        /** Listener for Slider panel state changes*/
        slide.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED)){
                    imgSlide.setImageResource(R.mipmap.slide_up);
                    fabLayout.setVisibility(View.VISIBLE);
                }
                else if (newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)){
                    imgSlide.setImageResource(R.mipmap.slide_down);
                    fabLayout.setVisibility(View.GONE);
                }
            }
        });
        
        /** getting family, user ID and group status from shred Preferences */
        SharedPreferences sharedPref = getContext().getSharedPreferences("fwPrefs",0);
        userID = sharedPref.getString("uniUserID", "");
        familyID = sharedPref.getString("uniFamilyID", "");
        InGroup = sharedPref.getString("InGroup", "");

        Log.i("TestFamilyTransactions",userID+" "+familyID+" "+InGroup);


        /* Getting user details from relevant group */
        FirebaseDatabase.getInstance().getReference("Groups").child(familyID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                KeyName.clear();
                for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                    KeyName.put(tdSnapshot.getKey(),tdSnapshot.child("firstName").getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Query query = getQuery(familyID, userID, InGroup);
        populateTransactions(query);


            /** Getting and populating list with group member info */
            gDatabase = FirebaseDatabase.getInstance().getReference("Groups").child(familyID);
            gDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    grpList.clear();
                    for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                        Log.i("testFT","+"+tdSnapshot.child("userID").getValue().toString());

                        GroupDetails gr = tdSnapshot.getValue(GroupDetails.class);
                        FamilyExpensesIncomes expensesIncomes = new FamilyExpensesIncomes(gr.getUserID(), familyID);

                        grpList.add(gr);
                    }
                    grpAdapter = new GroupListAdapter(getActivity(),grpList,familyID);
                    memberList.setAdapter(grpAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /** Setting up account list to check there are any accounts before adding a transaction*/
        accountsList = new ArrayList<>();
        Query query2 = FirebaseDatabase.getInstance().getReference("Account").orderByChild("user").equalTo(userID);
        query2.addValueEventListener(new ValueEventListener() {
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

        /** List all members transactions when members name is clicked*/
        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupDetails gr = grpList.get(position);
                Query query2 = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");
                populateUserTransactions(query2, gr.getUserID());
                slide.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        /** When trasaction is clicked to view more details*/
        transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewTransaction(keys.get(position));
            }
        });

        /** List all transactions on the family when button clicked*/
        btnAlltransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = getQuery(familyID, userID, InGroup);
                populateTransactions(query);
                slide.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        return view;
        }

    /**
     * @param familyID - family id of current the user
     * @param userID - user id of the current user
     * @param inGroup - status of the admin
     * @return - return the query relevent to the status(In a group or not) of the user
     */
    private static Query getQuery(String familyID, String userID, String inGroup) {
        Query query ;

        if (familyID.equals(userID) && !inGroup.equals("true")){
            query = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date");
        }
        else {
            query = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");
        }
        return query;
    }

    /***
     *  populate list with all family transactions
     * @param query - input query relevant to the user
     */
    private void populateTransactions(final Query query) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tdList.clear();
                keys.clear();
                for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                    TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                        tdList.add(tdSnapshot.getValue(TransactionDetails.class));
                        keys.add(tdSnapshot.getKey());

                }
                Collections.reverse(tdList);
                Collections.reverse(keys);
                adapter = new FamilyTransactionsAdapter(getActivity(),tdList);
                transactionList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     *  populate list relevant to user id of the member
     * @param query - input query relevant to the user
     * @param sortUID - user id the member which is used to filter
     */
    private void populateUserTransactions(final Query query, final String sortUID) {


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tdList.clear();
                keys.clear();
                for(DataSnapshot tdSnapshot : dataSnapshot.getChildren()){
                    TransactionDetails td = tdSnapshot.getValue(TransactionDetails.class);
                    if (td.getUserID().equals(sortUID)){
                        tdList.add(tdSnapshot.getValue(TransactionDetails.class));
                        keys.add(tdSnapshot.getKey());
                    }
                }
                Collections.reverse(tdList);
                Collections.reverse(keys);
                adapter = new FamilyTransactionsAdapter(getActivity(),tdList);
                transactionList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* method to populate the a single row from transaction details  */
    public static class DetailsViewHolder extends RecyclerView.ViewHolder{
            View mView;
            TextView txtTitle, txtCategory, txtDate, txtAmount, txtName;
            ImageView imageView;
            public DetailsViewHolder(View v) {
                super(v);
                mView = v;
                txtTitle = (TextView) v.findViewById(R.id.txtFamTitle);
                txtCategory = (TextView) v.findViewById(R.id.txtFamCategory);
                txtDate = (TextView) v.findViewById(R.id.txtFamTime);
                txtAmount = (TextView) v.findViewById(R.id.txtFamAmount);
                txtName = (TextView) v.findViewById(R.id.txtFamName);
                imageView = (ImageView) v.findViewById(R.id.FamImg);
            }

            public void setTitle(String title, String type,Context context) {

                if (type.equals("Income")){
                    txtTitle.setTextColor(ContextCompat.getColor(context,R.color.income));
                }
                else if (type.equals("Expense")){
                    txtTitle.setTextColor(ContextCompat.getColor(context,R.color.expense));
                }
                txtTitle.setText(title);
            }

            public void setDate(String date, String type,Context context) {
                if (type.equals("Income")){
                    txtDate.setTextColor(ContextCompat.getColor(context,R.color.income));
                }
                else if (type.equals("Expense")){
                    txtDate.setTextColor(ContextCompat.getColor(context,R.color.expense));
                }

                txtDate.setText(trns.dateView(date,context));
            }

            public void setCategory(String category, String type,Context context) {
                if (type.equals("Income")){
                    txtCategory.setTextColor(ContextCompat.getColor(context,R.color.income));
                }
                else if (type.equals("Expense")){
                    txtCategory.setTextColor(ContextCompat.getColor(context,R.color.expense));
                }
                txtCategory.setText(trns.categoryView(category,context));
            }

            public void setAmount(String amount, String currency, String type, Context context) {
                if (type.equals("Income")){
                    txtAmount.setTextColor(ContextCompat.getColor(context,R.color.income));
                    txtAmount.setText("+"+trns.currencyView(currency,context)+amount);
                }
                else if (type.equals("Expense")){
                    txtAmount.setTextColor(ContextCompat.getColor(context,R.color.expense));
                    txtAmount.setText("-"+trns.currencyView(currency,context)+amount);
                }

            }
            public void setName(String name) {
                txtName.setText(name);
            }
            public void setImage(Integer image) {
                imageView.setImageResource(image);
            }


        }

        /* View transaction in a expanded view on a dialog box */
    private void viewTransaction(final String key) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.transaction_family_view);
            final DatabaseReference transaction;
            if (familyID.equals(userID) && !InGroup.equals("true")){
                transaction = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).child(key);
                tId = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).child(key).getKey();
            }
            else {
                transaction = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key);
                tId = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key).getKey();
            }
        TextView vhFamTitle = (TextView) dialog.findViewById(R.id.vhFamTitle);
        TextView vhFamAmount = (TextView) dialog.findViewById(R.id.vhFamAmount);
        TextView vhFamCategory = (TextView) dialog.findViewById(R.id.vhFamCategory);
        TextView vhFamAccount = (TextView) dialog.findViewById(R.id.vhFamAccount);
        TextView vhFamDate = (TextView) dialog.findViewById(R.id.vhFamDate);
        TextView vhFamTime = (TextView) dialog.findViewById(R.id.vhFamTime);
        TextView vhFamName = (TextView) dialog.findViewById(R.id.vhFamName);
        TextView vhFamLocation = (TextView) dialog.findViewById(R.id.vhFamLocation);
        vhFamTitle.setText(R.string.vhTitle);
        vhFamAmount.setText(R.string.vhAmount);
        vhFamCategory.setText(R.string.vhCategory);
        vhFamAccount.setText(R.string.vhAccount);
        vhFamDate.setText(R.string.vhDate);
        vhFamTime.setText(R.string.vhTime);
        vhFamName.setText(R.string.vhName);
        vhFamLocation.setText(R.string.vhLocation);

        transaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransactionDetails td = dataSnapshot.getValue(TransactionDetails.class);
                TextView vTitle = (TextView) dialog.findViewById(R.id.vFamTitle);
                TextView vAmount = (TextView) dialog.findViewById(R.id.vFamTxtAmount);
                TextView vCategory = (TextView) dialog.findViewById(R.id.vFamTxtCategory);
                TextView vAccount = (TextView) dialog.findViewById(R.id.vFamTxtAccount);
                TextView vDate = (TextView) dialog.findViewById(R.id.vFamTxtDate);
                TextView vTime = (TextView) dialog.findViewById(R.id.vFamTxtTime);
                TextView vName = (TextView) dialog.findViewById(R.id.vFamTxtName);
                TextView vLocation = (TextView) dialog.findViewById(R.id.vFamTxtLocation);
                Button vCancel = (Button) dialog.findViewById(R.id.btnFamCancel);
                if (td.getTitle().isEmpty())
                    vTitle.setText("Title Not Available");
                else
                    vTitle.setText(td.getTitle());
                vAmount.setText(trns.currencyView(td.getCurrency(),getActivity())+td.getAmount());
                vCategory.setText(trns.categoryView(td.getCategoryName(),getActivity()));
                vAccount.setText(td.getAccount());
                vDate.setText(trns.dateView(td.getDate(),getContext()));
                vTime.setText(trns.timeView(td.getTime(),getActivity()));
                String name=KeyName.get(td.getUserID());
                vName.setText(name);
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


    /* view holder for group details for member info */
    public static class GrpDetailsViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        public GrpDetailsViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.txtGrName);
        }

        public void setName(String name) {
            txtName.setText(name);
        }

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
                        backgroundLayout.setVisibility(View.VISIBLE);
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
                        backgroundLayout.setVisibility(View.GONE);
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
