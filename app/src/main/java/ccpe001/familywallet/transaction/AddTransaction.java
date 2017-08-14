package ccpe001.familywallet.transaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ccpe001.familywallet.ExportData;
import ccpe001.familywallet.R;
import ccpe001.familywallet.Validate;

public class AddTransaction extends AppCompatActivity {

    /*Initializing */
    private TextView txtLocation,txtCategory;
    private Button btnRecurring;
    private Spinner spinCurrency, spinAccount;
    int PLACE_PICKER_REQUEST=1;
    private EditText txtAmount, txtDate, txtTime, txtTitle;
    private CheckBox checkRecurring;
    /*Initializing String and Integer variables to store data from input values*/
    String categoryName,  title, date, amount, currency, time, location, account, type, update, key, userID, familyID, eUserID, eFamilyID;
    Integer currencyIndex, accountIndex, categoryID;
    /*Initializing variable firebase reference */
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    Integer count=1;
    Resources resources;
    final Context context = this;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtAmount =(EditText)findViewById(R.id.txtAmount);
        txtDate = (EditText) findViewById(R.id.txtDate);
        txtTime = (EditText) findViewById(R.id.txtTime);
        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        btnRecurring = (Button) findViewById(R.id.btnRecurring);
        spinCurrency = (Spinner) findViewById(R.id.spinCurrency);
        spinAccount = (Spinner) findViewById(R.id.spinAccount);
        ImageView imgValue = (ImageView) findViewById(R.id.imgValue);
        ImageView imgAccount = (ImageView) findViewById(R.id.imgAccount);
        ImageView imgCategory = (ImageView) findViewById(R.id.imgCategory);
        ImageView imgNote = (ImageView) findViewById(R.id.imgNote);
        ImageView imgCalender = (ImageView) findViewById(R.id.imgCalender);
        ImageView imgLocation = (ImageView) findViewById(R.id.imgLocation);
        ImageButton imgSave = (ImageButton) findViewById(R.id.btnSave);




        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlacePickerActivity();
            }
        });
        checkRecurring =(CheckBox) findViewById(R.id.chRecurring);
        resources=getResources();
        final String[] recurringPeriod = resources.getStringArray(R.array.spinnerRecurring);
        btnRecurring.setText(recurringPeriod[0]);

        checkRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnRecurring.setVisibility(View.VISIBLE);

                }
                else
                    btnRecurring.setVisibility(View.INVISIBLE);
            }
        });

        btnRecurring.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (count==4)
                    count=0;
                switch (count){
                    case 0 :btnRecurring.setText(recurringPeriod[0]);break;
                    case 1 :btnRecurring.setText(recurringPeriod[1]);break;
                    case 2 :btnRecurring.setText(recurringPeriod[2]);break;
                    case 3 :btnRecurring.setText(recurringPeriod[3]);break;

                }
                count++;


            }
        });

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

        if (savedInstanceState == null) {
            Bundle extras = this.getIntent().getExtras();
            if(extras == null) {

            } else {
                categoryName = extras.getString("categoryName");
                categoryID = extras.getInt("categoryID");
                title = extras.getString("title");
                date = extras.getString("date");
                time = extras.getString("time");
                amount = extras.getString("amount");
                location = extras.getString("location");
                currencyIndex = extras.getInt("currencyIndex");
                accountIndex = extras.getInt("accountIndex");
                type = extras.getString("transactionType");
                update = extras.getString("Update");
                key = extras.getString("key");
                eUserID = extras.getString("userID");
                eFamilyID = extras.getString("familyID");



                txtTitle.setText(title);
                txtAmount.setText(amount);
                txtDate.setText(date);
                txtTime.setText(time);
                txtLocation.setText(location);
                spinAccount.setSelection(accountIndex);
                spinCurrency.setSelection(currencyIndex);

            }

        }

        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = txtAmount.getText().toString();
                date = txtDate.getText().toString();
                time = txtTime.getText().toString();
                title = txtTitle.getText().toString();
                currencyIndex = spinCurrency.getSelectedItemPosition();
                accountIndex = spinCurrency.getSelectedItemPosition();
                location = txtLocation.getText().toString();
                Intent intent = new Intent(AddTransaction.this,TransactionCategory.class);
                intent.putExtra("title",title);
                intent.putExtra("amount",amount);
                intent.putExtra("date",date);
                intent.putExtra("time",time);
                intent.putExtra("location",location);
                intent.putExtra("currencyIndex",currencyIndex);
                intent.putExtra("accountIndex",accountIndex);
                intent.putExtra("transactionType",type);
                intent.putExtra("Update",update);
                intent.putExtra("key",key);
                intent.putExtra("userID",eUserID);
                intent.putExtra("familyID",eFamilyID);
                startActivity(intent);
            }
        });


        if (categoryName!=null){
            txtCategory.setText(categoryName);
        }


        txtAmount.setFilters(new InputFilter[] {new CurrencyFormatInputFilter()});

        if (date==null){
            long Cdate = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dateString = sdf.format(Cdate);
            txtDate.setText(dateString);
        }
        if (time==null){
            long Cdate = System.currentTimeMillis();
            SimpleDateFormat stf = new SimpleDateFormat("h:mm a");
            String timeString = stf.format(Cdate);
            txtTime.setText(timeString);
        }
        if (type!=null){
            getSupportActionBar().setTitle(type);
            if (type.equals("Income")){

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.income)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.incomeDark));
                    imgValue.setColorFilter(getResources().getColor(R.color.income));
                    imgAccount.setColorFilter(getResources().getColor(R.color.income));
                    imgCategory.setColorFilter(getResources().getColor(R.color.income));
                    imgNote.setColorFilter(getResources().getColor(R.color.income));
                    imgCalender.setColorFilter(getResources().getColor(R.color.income));
                    imgLocation.setColorFilter(getResources().getColor(R.color.income));
                    imgSave.setImageResource(R.mipmap.check_income);
                }
            }
            else if(type.equals("Expense")){
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.expense)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.expenseDark));
                    imgValue.setColorFilter(getResources().getColor(R.color.expense));
                    imgAccount.setColorFilter(getResources().getColor(R.color.expense));
                    imgCategory.setColorFilter(getResources().getColor(R.color.expense));
                    imgNote.setColorFilter(getResources().getColor(R.color.expense));
                    imgCalender.setColorFilter(getResources().getColor(R.color.expense));
                    imgLocation.setColorFilter(getResources().getColor(R.color.expense));
                    imgSave.setImageResource(R.mipmap.check_expense);
                }
            }
        }

        TextView txtDate = (TextView) findViewById(R.id.txtDate);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateDialog dialog = new DateDialog(v);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft,"DatePicker");

            }
        });

        TextView txtTime = (TextView) findViewById(R.id.txtTime);
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeDialog dialog = new TimeDialog(v);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft,"TimePicker");

            }
        });
        final String[] cur = resources.getStringArray(R.array.spinnerCurrency);

        final TextView txttest = (TextView) findViewById(R.id.txttest);
        txttest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.currency_list);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1,cur );
                ListView lv = (ListView) dialog.findViewById(R.id.listCurrency);
                lv.setAdapter(arrayAdapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        txttest.setText(cur[position]);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }



    private void startPlacePickerActivity() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displaySelectedPlaceFromPlacePicker(Intent data) {
        Place placeSelected = PlacePicker.getPlace(data, this);

        String name = placeSelected.getName().toString();
        String address = placeSelected.getAddress().toString();

        //TextView enterCurrentLocation = (TextView) findViewById(R.id.txtLocation);
        txtLocation.setText(name + ", " + address);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        txtCategory = (TextView)findViewById(R.id.txtCategory);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        }

    }

    public class CurrencyFormatInputFilter implements InputFilter {

        Pattern mPattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?");

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {

            String result =
                    dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

            Matcher matcher = mPattern.matcher(result);

            if (!matcher.matches()) return dest.subSequence(dstart, dend);

            return null;
        }
    }





    public void saveTransaction(View view) {

        final Validate v = new Validate();
        amount = txtAmount.getText().toString();
        date = txtDate.getText().toString();
        date = v.dateToValue(date);
        time = txtTime.getText().toString();
        title = txtTitle.getText().toString();
        currency = spinCurrency.getSelectedItem().toString();
        location = txtLocation.getText().toString();
        account = spinAccount.getSelectedItem().toString();
        String recurringPeriod = btnRecurring.getText().toString();

        if (categoryName==null){
            categoryName = "Other";
            categoryID = (R.drawable.cat_other);
        }

        if (checkRecurring.isChecked()) {
            if (amount.isEmpty()) {
                Toast.makeText(this, " Set the Amount first", Toast.LENGTH_SHORT).show();
            }
            else {
                TransactionDetails td;
                try {
                    if (update.equals("False")){
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        td = new TransactionDetails(userID,amount, title, categoryName, date, categoryID, time, account, location, type, currency,familyID, recurringPeriod);
                        mDatabase.child("RecurringTransactions").push().setValue(td);
                        Toast.makeText(this, "Transaction Added", Toast.LENGTH_LONG).show();
                    }
//                    else if (update.equals("True")){
//                        mDatabase = FirebaseDatabase.getInstance().getReference("Transactions");
//                        td = new TransactionDetails(eUserID,amount, title, categoryName, date, categoryID, time, account, location, type, currency,eFamilyID);
//                        Map<String, Object> postValues = td.toMap();
//                        mDatabase.child(key).updateChildren(postValues);
//                        Toast.makeText(this, "Successfully Updated", Toast.LENGTH_LONG).show();
//                    }
                }catch (Exception e){

                }

                Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
                startActivity(intent);
            }
        }

        else {
        if (amount.isEmpty()) {
            Toast.makeText(this, " Set the Amount first", Toast.LENGTH_SHORT).show();
        }
        else {
            //DatabaseOps dbOp = new DatabaseOps(cnt);
            //dbOp.addData(amount, title, categoryName, date, Integer.parseInt(categoryID), time, account, location, type, currency, "uID");
            //Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();

            //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            TransactionDetails td;
            try {
                if (update.equals("False")){
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    td = new TransactionDetails(userID,amount, title, categoryName, date, categoryID, time, account, location, type, currency,familyID);
                    mDatabase.child("Transactions").push().setValue(td);
                    Toast.makeText(this, "Transaction Added", Toast.LENGTH_LONG).show();
                }
                else if (update.equals("True")){
                    mDatabase = FirebaseDatabase.getInstance().getReference("Transactions");
                    td = new TransactionDetails(eUserID,amount, title, categoryName, date, categoryID, time, account, location, type, currency,eFamilyID);
                    Map<String, Object> postValues = td.toMap();
                    mDatabase.child(key).updateChildren(postValues);
                    Toast.makeText(this, "Successfully Updated", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){

            }



            Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
            startActivity(intent);
        }

        }


    }



}
