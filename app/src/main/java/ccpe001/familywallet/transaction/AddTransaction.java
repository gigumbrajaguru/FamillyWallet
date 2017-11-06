package ccpe001.familywallet.transaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
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
import ccpe001.familywallet.*;
import ccpe001.familywallet.budget.ActionValidater;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;


public class AddTransaction extends AppCompatActivity {


    /*Initializing layout items*/
    private TextView txtLocation;
    private EditText txtAmount, txtDate, txtTime, txtTitle,txtCurrency, txtCategory, txtRecurring, txtAccount;
    private CheckBox checkRecurring;
    private ImageView imgValue, imgAccount, imgCategory, imgNote, imgCalender, imgLocation, imgSave;
    /*Initializing variables to hold Extra values passed with intent or values from input fields */
    String categoryName,  title, date, amount, currency, time, location, account, type, update, key,
            userID, familyID, eUserID, eFamilyID, previousAmount, recurrPeriod, InGroup, userName;
    Integer   categoryID;
    Boolean templateChecked;
    List<String> accountsList;
    /*Initializing firebase variables */
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private Integer count=1;
    Resources resources;
    int PLACE_PICKER_REQUEST=1;
    final Context context = this;

    final Validate v = new Validate();
    final Translate trns = new Translate();

    /*Initializations done to scan bill feature*/
    private EditText editTextScan;
    private ImageView imageViewScan;
    private CustomAlertDialogs alert;
    private StorageReference storageReference;
    private static final int CROP_CAM = 4;
    private String tId;
    private Uri billImageUri;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){

        }
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


        /*Initializations done to scan bill feature*/
        alert = new CustomAlertDialogs();
        editTextScan = (EditText) findViewById(R.id.editTextScan);
        imageViewScan = (ImageView) findViewById(R.id.imageViewScan);
        editTextScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    final String[] CROPCAMPERARR = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                    if (!CustomAlertDialogs.hasPermissions(AddTransaction.this, CROPCAMPERARR)) {
                        alert.initPermissionPage(AddTransaction.this, getString(R.string.permit_only_camera)).setPositiveButton(R.string.customaletdialog_initPermissionPage_posbtn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(AddTransaction.this, CROPCAMPERARR, CROP_CAM);
                            }
                        }).show();
                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(AddTransaction.this);
                    }

            }
        });


        /*Setting the hints of the input fields according to the language preferred */
        txtAmount.setHint(resources.getString(R.string.transactionAmount));
        txtCategory.setHint(resources.getString(R.string.transactionCategory));
        txtTitle.setHint(resources.getString(R.string.transactionTitle));
        txtLocation.setHint(resources.getString(R.string.transactionLocation));
        txtAccount.setHint(resources.getString(R.string.transactionAccount));
        checkRecurring.setText(resources.getString(R.string.recurring));

        /* Getting the user id and family id from shared prefered  */
        SharedPreferences sharedPref = getSharedPreferences("fwPrefs",0);
        String uid = sharedPref.getString("uniUserID", "");
        String fid = sharedPref.getString("uniFamilyID", "");
        String uname = sharedPref.getString("uniFname", "");
        userID = uid;
        familyID = fid;
        userName = uname;
        InGroup = sharedPref.getString("InGroup", "");


        /* Getting the accounts relevant user to a list*/
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

        /*populating the dialog box with the account list*/
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

        /*when Location field click*/
                txtLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPlacePickerActivity();
                    }
                });

        /* Getting recurring time periods from l */
        final String[] recurringPeriod = resources.getStringArray(R.array.spinnerRecurring);

        /* Making recurring transaction selection visible and invisible according to the selection from the checkbox*/
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


        /* Changing the recurring period on click */
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

        /* Getting the currency list and populating the dialog box according to relevant language  */
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






        /*Getting saved data transferred between classes */
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

                /* Setting text fields from data transferred when changed interfaces  */
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

        /* Redirecting to the category selection activity and sending the current entered data */
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

        /* if recurring period isn't set, set the default as daily */
        if (recurrPeriod==null){
            txtRecurring.setText(recurringPeriod[0]);
        }

        /* if currency isn't set, set the default as LKR */
        if (currency==null) {
            txtCurrency.setText(cur[0]);
        }

        /* if category name isn't set set as default */
        if (categoryName !=null){
            txtCategory.setText(categoryName);
        }

        /* Callign method to set the amount text field so only a valid price can be entered */
        txtAmount.setFilters(new InputFilter[] {new CurrencyFormatInputFilter()});

        /*if date isn't set set the current date*/
        if (date==null){
            long Cdate = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dateString = sdf.format(Cdate);
            txtDate.setText(dateString);
        }

        /*if time isn't set set the current time*/
        if (time==null){
            long Cdate = System.currentTimeMillis();
            SimpleDateFormat stf = new SimpleDateFormat("h:mm a");
            String timeString = stf.format(Cdate);
            txtTime.setText(timeString);
        }
        /* Changing color in items and notification bar in add transaction to Green for expense category*/
        if (type!=null){
            getSupportActionBar().setTitle(type);
            if (type.equals("Income")){
                getSupportActionBar().setTitle(resources.getString(R.string.income));
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
                    imageViewScan.setColorFilter(getResources().getColor(R.color.income));
                    imgSave.setImageResource(R.mipmap.check_income);
                }
            }
            /* Changing color in items and notification bar in add transaction to Red for expense category*/
            else if(type.equals("Expense")){
                getSupportActionBar().setTitle(resources.getString(R.string.expense));
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
                    imageViewScan.setColorFilter(getResources().getColor(R.color.expense));
                    imgSave.setImageResource(R.mipmap.check_expense);
                }
            }
        }


        /* opening date dialog box to set date */
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateDialog dialog = new DateDialog(v);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft,"DatePicker");

            }
        });

        /* opening time dialog box to set time */
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeDialog dialog = new TimeDialog(v);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft,"TimePicker");
            }
        });

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
        boolean validExpense=false, validIncome=false;
        CurrencyResponse cc = new CurrencyResponse();

        /* Getting the input values to sent to the database*/
        amount = txtAmount.getText().toString();
        date = txtDate.getText().toString();
        time = txtTime.getText().toString();
        date = trns.dateToDB(date,time);
        time = trns.timeToDB(time);
        title = txtTitle.getText().toString();
        currency = txtCurrency.getText().toString();
        currency = trns.currencyToDB(currency);
        location = txtLocation.getText().toString();
        account = txtAccount.getText().toString();
        recurrPeriod = txtRecurring.getText().toString();
        recurrPeriod = trns.recurringToDB(recurrPeriod);


        /* if no category selected making the default as other */
        if (categoryName==null){
            categoryName = "Other";
            categoryID = (R.drawable.cat_other);
        }
        categoryName = trns.categoryToDB(categoryName);

        /* if its a recurring transaction */
        if (checkRecurring.isChecked()) {
            if (amount.isEmpty()) {
                Toast.makeText(this, R.string.nullAmount, Toast.LENGTH_LONG).show();
            }
            else if (account.isEmpty()){
                Toast.makeText(this, R.string.nullAccount, Toast.LENGTH_LONG).show();
            }
            else if (!currency.equals("LKR.") && !networkConnected()){
                Toast.makeText(this, R.string.noConnection, Toast.LENGTH_LONG).show();
            }
            else {
                TransactionDetails td;
                try {
                    if (update.equals("False")){

                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        td = new TransactionDetails(userID,userName,amount, title, categoryName, date, categoryID, time, account, location, type, currency,familyID, recurrPeriod);
                        if(currency.equals("LKR."))  {
                            mDatabase = mDatabase.child("RecurringTransactions").child(userID).push();
                            tId = mDatabase.getKey();
                            mDatabase.setValue(td);
                        }
                        else {
                            cc.curr(td,InGroup);
                        }

                        returnToDashboard();
                        Toast.makeText(this, R.string.transactionAdded, Toast.LENGTH_LONG).show();
                    }
                    else if (update.equals("True")){
                        mDatabase = FirebaseDatabase.getInstance().getReference("RecurringTransactions").child(familyID);
                        td = new TransactionDetails(eUserID,userName,amount, title, categoryName, date, categoryID, time, account, location, type, currency,eFamilyID, recurrPeriod);
                        Map<String, Object> postValues = td.toMap();
                        mDatabase.child(key).updateChildren(postValues);
                        returnToDashboard();
                        Toast.makeText(this, R.string.transactionUpdated, Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){

                }
            }
        }

        /* if its a normal transaction */
        else {
        if (amount.isEmpty()) {
            Toast.makeText(this, R.string.nullAmount, Toast.LENGTH_LONG).show();
        }
        else if (account.isEmpty()){
            Toast.makeText(this, R.string.nullAccount, Toast.LENGTH_LONG).show();
        }
        else if (!currency.equals("LKR.") && !networkConnected()){
            Toast.makeText(this, R.string.noConnection, Toast.LENGTH_LONG).show();
        }
        else {
            TransactionDetails td;
            try {
                if (update.equals("False")){
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    td = new TransactionDetails(userID,userName, amount, title, categoryName, date, categoryID, time, account, location, type, currency,familyID);
                    Double amountDouble =Double.parseDouble(amount);
                    ActionValidater actionValidater=new ActionValidater();
                    if (type.equals("Expense")){
                      actionValidater.amountCheck(account, amountDouble);

                    }
                    else if(type.equals("Income")){
                        actionValidater.addIncome(account, amountDouble);
                    }
                        if(currency.equals("LKR."))  {
                            if (familyID.equals(userID) && !InGroup.equals("true")){
                                mDatabase = mDatabase.child("Transactions").child(userID).push();
                                tId = mDatabase.getKey();
                                mDatabase.setValue(td);
                            }
                            else{
                                mDatabase = mDatabase.child("Transactions").child("Groups").child(familyID).push();
                                tId = mDatabase.getKey();
                                mDatabase.setValue(td);
                            }
                        }
                        else {
                            cc.curr(td,InGroup);
                        }
                        returnToDashboard();
                        Toast.makeText(this, R.string.transactionAdded, Toast.LENGTH_LONG).show();

                }
                else if (update.equals("True")){
                    mDatabase = FirebaseDatabase.getInstance().getReference("Transactions").child(familyID);
                    if (familyID.equals(userID) && !InGroup.equals("true")){
                        mDatabase = FirebaseDatabase.getInstance().getReference("Transactions").child(userID);
                        tId = key;
                    }
                    else{
                        mDatabase = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID);
                        tId = key;
                    }

                    td = new TransactionDetails(eUserID,userName,amount, title, categoryName, date, categoryID, time, account, location, type, currency,eFamilyID);
                    Double amountDouble =Double.parseDouble(amount)-Double.parseDouble(previousAmount);
                    ActionValidater actionValidater=new ActionValidater();
                    if (type.equals("Expense")){
                        actionValidater.amountCheck(account, amountDouble);
                    }
                    else if(type.equals("Income")){
                        actionValidater.addIncome(account, amountDouble);
                    }
                        Map<String, Object> postValues = td.toMap();
                        mDatabase.child(key).updateChildren(postValues);
                        returnToDashboard();
                        Toast.makeText(this, R.string.transactionUpdated, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){

            }

        }

        }


    }

    private void returnToDashboard() {
        //updating,inserting the bill image
         if(billImageUri!=null) {
             mAuth = FirebaseAuth.getInstance();
             storageReference = FirebaseStorage.getInstance().getReference().child("ScannedBills").child(mAuth.getCurrentUser().getUid());
            storageReference.child(tId + ".jpg").putFile(billImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), R.string.ocrreader_onactivityresult_uploaddone, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alert.initCommonDialogPage(AddTransaction.this, getString(R.string.common_error), true);
                }
            });
        }

        Intent intent = new Intent("ccpe001.familywallet.DASHBOARD");
        startActivity(intent);
    }

    /*launches the place picker activity when location text field selected */
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
        txtLocation.setText(name + ", " + address);     //setting the location in location text field
    }


    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Method run when place is selected*/
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        /*run when image is cropped*/
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(this);
                View alertDiaView = inflater.inflate(R.layout.scanned_image_view,null);
                ImageView imageView = (ImageView) alertDiaView.findViewById(R.id.imageView);

                imageView.setImageURI(result.getUri());

                nameBuilder.setNegativeButton(R.string.scan_reject_image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        billImageUri = null;
                        dialogInterface.dismiss();
                        editTextScan.setText(R.string.image_no);
                    }
                }).setPositiveButton(R.string.scan_set_image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        billImageUri = result.getUri();
                        dialogInterface.dismiss();
                        editTextScan.setText(R.string.image_yes);
                    }
                });

                nameBuilder.setView(alertDiaView);

                AlertDialog alertDialog = nameBuilder.show();
                alertDialog.getWindow().setLayout((int) (300 * Resources.getSystem().getDisplayMetrics().density) ,
                        (int) (300 * Resources.getSystem().getDisplayMetrics().density));
            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                alert.initCommonDialogPage(AddTransaction.this,getString(R.string.common_error),true);
            }
        }

    }

    /*method run when getting permissions for read,write,camera*/
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CROP_CAM){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED||grantResults[1]==PackageManager.PERMISSION_GRANTED||grantResults[2]==PackageManager.PERMISSION_GRANTED){
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddTransaction.this);
            }else {
                alert = new CustomAlertDialogs();
                alert.initCommonDialogPage(AddTransaction.this,getString(R.string.error_permitting),true);
            }
        }
    }

    private boolean networkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }



}
