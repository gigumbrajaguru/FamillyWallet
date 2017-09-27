package ccpe001.familywallet.transaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Translate;
import ccpe001.familywallet.Validate;

/**
 *
 */

public class FamilyTransactions extends Fragment {

    static Translate trns = new Translate();
    static HashMap<String,String> KeyName;
    String userID, familyID, InGroup;


    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private Context context;

    public FamilyTransactions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.transaction_family_list, container, false);

        /* getting family, user ID and group status from shred Preferences */
        SharedPreferences sharedPref = getContext().getSharedPreferences("fwPrefs",0);
        userID = sharedPref.getString("uniUserID", "");
        familyID = sharedPref.getString("uniFamilyID", "");
        InGroup = sharedPref.getString("InGroup", "");

        context=getContext();      //getting current context
        KeyName = new HashMap<>() ;     //Setting hasmap to set user ID and name as key value pair

        /* getting recycler view and setting it with layout manager to populate it in reverse*/
        recyclerView=(RecyclerView)view.findViewById(R.id.testRecyc);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


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


        Query query ;

        if (familyID.equals(userID) && !InGroup.equals("true")){
            query = FirebaseDatabase.getInstance().getReference("Transactions").child(userID).orderByChild("date");
        }
        else {
            query = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date");

        }

        /* Setting the firebase recycler adapter to populate the all family transaction list */
        FirebaseRecyclerAdapter<TransactionDetails,DetailsViewHolder> adapter = new FirebaseRecyclerAdapter<TransactionDetails,DetailsViewHolder>(
                TransactionDetails.class,
                R.layout.transaction_family_row,
                DetailsViewHolder.class,
                query
        ){

            @Override
            protected void populateViewHolder(DetailsViewHolder viewHolder, TransactionDetails model, int position) {

                final String key = getRef(position).getKey();      //getting the key of the selecting item
                viewHolder.setTitle(model.getTitle(),model.getType(),context);
                viewHolder.setDate(model.getDate(),model.getType(), context);
                viewHolder.setCategory(model.getCategoryName(),model.getType(), context);
                viewHolder.setAmount(model.getAmount(), model.getCurrency(), model.getType(), context);
                viewHolder.setName(model.getUserID());
                viewHolder.setImage(model.getCategoryID());

                /* listen to selection */
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewTransaction(key);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

            return view;
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
            public void setName(String id) {
                String name=KeyName.get(id);
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
            }
            else {
                transaction = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).child(key);
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
    }

}
