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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ccpe001.familywallet.R;
import ccpe001.familywallet.Translate;
import ccpe001.familywallet.Validate;
import ccpe001.familywallet.budget.actionValidater;

public class AddTransaction extends AppCompatActivity {


    /*Initializing layout items*/
    private TextView txtLocation;
    private EditText txtAmount, txtDate, txtTime, txtTitle,txtCurrency, txtCategory, txtRecurring, txtAccount;
    private CheckBox checkRecurring;
    private ImageView imgValue, imgAccount, imgCategory, imgNote, imgCalender, imgLocation, imgSave;
    /*Initializing variables to hold Extra values passed with intent or values from input fields */
    String categoryName,  title, date, amount, currency, time, location, account, type, update, key,
            userID, familyID, eUserID, eFamilyID, previousAmount, recurrPeriod;
    Integer   categoryID;
    Boolean templateChecked;
    List<String> accountsList;
    /*Initializing firebase variables */
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    Integer count=1;
    Resources resources;
    int PLACE_PICKER_REQUEST=1;
    final Context context = this;

    final actionValidater av = new actionValidater();
    final Validate v = new Validate();
    final Translate trns = new Translate();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*Setting references to layout items*/
        txtAmount =(EditText)findViewById(R.id.txtAmount);
        txtDate = (EditText) findViewById(R.id.txtDate);
        txtTime = (EditText) findViewById(R.id.txtTime);
        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtCategory = (EditText) findViewById(R.id.txtCategory);
        txtRecurring = (EditText) findViewById(R.id.txtRecurring);
        txtCurrency = (EditText) findViewById(R.id.txtCurrency);
        txtAccount = (EditText) findViewById(R.id.txtAccount);
        imgValue = (ImageView) findViewById(R.id.imgValue);
        imgAccount = (ImageView) findViewById(R.id.imgAccount);
        imgCategory = (ImageView) findViewById(R.id.imgCategory);
        imgNote = (ImageView) findViewById(R.id.imgNote);
        imgCalender = (ImageView) findViewById(R.id.imgCalender);
        imgLocation = (ImageView) findViewById(R.id.imgLocation);
        imgSave = (ImageButton) findViewById(R.id.btnSave);
        checkRecurring =(CheckBox) findViewById(R.id.chRecurring);
        resources=getResources();


        /**/
        txtAmount.setHint(resources.getString(R.string.transactionAmount));
        txtCategory.setHint(resources.getString(R.string.transactionCategory));
        txtTitle.setHint(resources.getString(R.string.transactionTitle));
        txtLocation.setHint(resources.getString(R.string.transactionLocation));
        txtAccount.setHint(resources.getString(R.string.transactionAccount));


        /*Getting firebase authentication to get users info*/
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

        /*when Location field click*/
                txtLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPlacePickerActivity();
                    }
                });


        final String[] recurringPeriod = resources.getStringArray(R.array.spinnerRecurring);   //get recurring time periods from string.xml

        checkRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtRecurring.setVisibility(View.VISIBLE);
                }
                else
                    txtRecurring.setVisibility(View.INVISIBLE);
            }
        });


        txtRecurring.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (count==4)
                    count=0;
                switch (count){
                    case 0 :
                        txtRecurring.setText(recurringPeriod[0]);break;
                    case 1 :
                        txtRecurring.setText(recurringPeriod[1]);break;
                    case 2 :
                        txtRecurring.setText(recurringPeriod[2]);break;
                    case 3 :
                        txtRecurring.setText(recurringPeriod[3]);break;
                }
                count++;
            }
        });

        final String[] cur = resources.getStringArray(R.array.spinnerCurrency);
        txtCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialogbox_list);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1,cur );
                ListView lv = (ListView) dialog.findViewById(R.id.dialogList);
                lv.setAdapter(arrayAdapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        txtCurrency.setText(cur[position]);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialogbox_list);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1,accountsList);
                ListView lv = (ListView) dialog.findViewById(R.id.dialogList);
                lv.setAdapter(arrayAdapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        txtAccount.setText(accountsList.get(position));
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });



        /*Getting */
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
                currency = extras.getString("currency");
                account = extras.getString("account");
                type = extras.getString("transactionType");
                update = extras.getString("Update");
                key = extras.getString("key");
                eUserID = extras.getString("userID");
                eFamilyID = extras.getString("familyID");
                previousAmount = extras.getString("previousAmount");
                templateChecked = extras.getBoolean("templateChecked");
                recurrPeriod = extras.getString("recurrPeriod");

                txtTitle.setText(title);
                txtAmount.setText(amount);
                txtDate.setText(date);
                txtTime.setText(time);
                txtLocation.setText(location);
                txtAccount.setText(account);
                txtCurrency.setText(currency);
                checkRecurring.setChecked(templateChecked);
                txtRecurring.setText(recurrPeriod);

            }

        }

        txtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = txtAmount.getText().toString();
                date = txtDate.getText().toString();
                time = txtTime.getText().toString();
                title = txtTitle.getText().toString();
                currency = txtCurrency.getText().toString();
                account = txtAccount.getText().toString();
                location = txtLocation.getText().toString();
                templateChecked = checkRecurring.isChecked();
                recurrPeriod = txtRecurring.getText().toString();
                Intent intent = new Intent(AddTransaction.this,TransactionCategory.class);
                intent.putExtra("title",title);
                intent.putExtra("amount",amount);
                intent.putExtra("date",date);
                intent.putExtra("time",time);
                intent.putExtra("location",location);
                intent.putExtra("currency",currency);
                intent.putExtra("account",account);
                intent.putExtra("transactionType",type);
                intent.putExtra("Update",update);
                intent.putExtra("key",key);
                intent.putExtra("userID",eUserID);
                intent.putExtra("familyID",eFamilyID);
                intent.putExtra("previousAmount",previousAmount);
                intent.putExtra("templateChecked",templateChecked);
                intent.putExtra("recurrPeriod",recurrPeriod);
                startActivity(intent);
            }
        });

        if (recurrPeriod==null){
            txtRecurring.setText(recurringPeriod[0]);
        }
        if (currency==null) {
            txtCurrency.setText(cur[0]);
        }
        if (categoryName !=null){
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


        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateDialog dialog = new DateDialog(v);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft,"DatePicker");

            }
        });


        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeDialog dialog = new TimeDialog(v);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft,"TimePicker");
            }
        });

    }


    /*launches the place picker activity*/
    private void startPlacePickerActivity() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*retrieves the place that the user has selected*/
    private void displaySelectedPlaceFromPlacePicker(Intent data) {
        Place placeSelected = PlacePicker.getPlace(data, this);
        String name = placeSelected.getName().toString();
        String address = placeSelected.getAddress().toString();
        txtLocation.setText(name + ", " + address);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        txtCategory = (EditText) findViewById(R.id.txtCategory);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        }

    }

    /* Method to keep the input amount/price as valid price/amount */
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





    /*Save & Update Trnsactions*/
    public void saveTransaction(View view) {
        Toast.makeText(context, "Working", Toast.LENGTH_SHORT).show();
        boolean validExpense=false, validIncome=false;
        amount = txtAmount.getText().toString();
        date = txtDate.getText().toString();
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
        date = trns.dateToDB(date);
        time = txtTime.getText().toString();
        time = trns.timeToDB(time);
        title = txtTitle.getText().toString();
        currency = txtCurrency.getText().toString();
        currency = trns.currencyToDB(currency);
        location = txtLocation.getText().toString();
        account = txtAccount.getText().toString();
        recurrPeriod = txtRecurring.getText().toString();

        if (categoryName==null){
            categoryName = "Other";
            categoryID = (R.drawable.cat_other);
        }
        categoryName = trns.categoryToDB(categoryName);

        if (checkRecurring.isChecked()) {
            if (amount.isEmpty()) {
                Toast.makeText(this, " Set the Amount first", Toast.LENGTH_SHORT).show();
            }
            else {
                TransactionDetails td;
                try {
                    if (update.equals("False")){
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        td = new TransactionDetails(userID,amount, title, categoryName, date, categoryID, time, account, location, type, currency,familyID, recurrPeriod);
                        mDatabase.child("RecurringTransactions").push().setValue(td);
                        Toast.makeText(this, "Transaction Added", Toast.LENGTH_LONG).show();
                    }
                    else if (update.equals("True")){
                        mDatabase = FirebaseDatabase.getInstance().getReference("RecurringTransactions");
                        td = new TransactionDetails(eUserID,amount, title, categoryName, date, categoryID, time, account, location, type, currency,eFamilyID, recurrPeriod);
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

        else {
        if (amount.isEmpty()) {
            Toast.makeText(this, " Set the Amount first", Toast.LENGTH_SHORT).show();
        }
        else {
            TransactionDetails td;
            try {
                if (update.equals("False")){
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    td = new TransactionDetails(userID,amount, title, categoryName, date, categoryID, time, account, location, type, currency,familyID);
                    Double amountDouble =Double.parseDouble(amount);
                    if (type.equals("Expense")){
                        validExpense = av.getAmount(account, amountDouble);

                    }
                    else if(type.equals("Income")){
                        validIncome = av.addIncome(account, amountDouble);
                    }
                    if (validExpense==true || validIncome==true && !account.isEmpty()) {
                        mDatabase.child("Transactions").push().setValue(td);
                        //Toast.makeText(this, "Transaction Added ", Toast.LENGTH_LONG).show();
                    }
                }
                else if (update.equals("True")){
                    mDatabase = FirebaseDatabase.getInstance().getReference("Transactions");

                    td = new TransactionDetails(eUserID,amount, title, categoryName, date, categoryID, time, account, location, type, currency,eFamilyID);
                    Double amountDouble =Double.parseDouble(amount)-Double.parseDouble(previousAmount);

                    if (type.equals("Expense")){
                        validExpense = av.getAmount(account, amountDouble);
                    }
                    else if(type.equals("Income")){
                        validIncome = av.addIncome(account, amountDouble);
                    }
                    if (validExpense==true || validIncome==true && !account.isEmpty()) {
                        Map<String, Object> postValues = td.toMap();
                        mDatabase.child(key).updateChildren(postValues);
                        Toast.makeText(this, "Successfully Updated "+previousAmount, Toast.LENGTH_LONG).show();
                    }


                }
            }catch (Exception e){

            }

            if (validExpense==true || validIncome==true && !account.isEmpty()) {
                Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
                startActivity(intent);
            }
            else if(type.equals("Expense") && validExpense==false ){
                Toast.makeText(this, "Account limit reached", Toast.LENGTH_LONG).show();
            }
            else if (account.isEmpty()){
                Toast.makeText(this, "Please select Account first", Toast.LENGTH_LONG).show();
            }
        }

        }


    }



}
