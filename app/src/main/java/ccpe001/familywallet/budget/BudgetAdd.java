package ccpe001.familywallet.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import ccpe001.familywallet.R;

public class BudgetAdd extends AppCompatActivity implements View.OnClickListener {
    EditText strDt,endDt,Bname,tAmount;
    Button smit,forcast;
    Switch noty;
    String budgetcat,max="",min="";
    ArrayList<BarEntry> group1 = new ArrayList<>();
    ArrayList<Double> usedlist = new ArrayList<>();
    ArrayList<Double> amountlist = new ArrayList<>();
    private static DatabaseReference mDatabases;
    private  int day,mon,yr;
    private String[] arraySpinner;
    String pickeditem,selected,sttDay,endday,bName,amounts,notify="Off",FamilyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.arraySpinner = getResources().getStringArray(R.array.categorylist);
        final DataStores ds=new DataStores();
        /*this.arraySpinner = new String[] {
                "Food & Drinks", "Travel", "Gifts","Bill","Entertainment","Home","Utilities","Shopping","Accommodation","Healthcare","Clothing","Groceries","Pets","Education","Kids","Loan","Business"
        };*/
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabases = FirebaseDatabase.getInstance().getReference();
        mDatabases.child("UserInfo").orderByChild("userId").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    FamilyId=child.child("familyId").getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        setContentView(R.layout.budget_handling);
        setTitle(getString(R.string.budgettitle));
        strDt=(EditText)findViewById(R.id.startDate);
        endDt=(EditText)findViewById(R.id.endDate);
        forcast=(Button)findViewById(R.id.btnForecast);
        strDt.setOnClickListener(this);
        endDt.setOnClickListener(this);
        Spinner s = (Spinner) findViewById(R.id.catSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, arraySpinner);
        s.setAdapter(adapter);
        Bname=(EditText)findViewById(R.id.txtBname);
        tAmount=(EditText)findViewById(R.id.txtAmount);
        s.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pickeditem = parent.getItemAtPosition(position).toString();
                selected=ds.datachange(pickeditem);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        noty=(Switch)findViewById(R.id.switch4);
        smit=(Button)findViewById(R.id.btnSubmit);
        noty.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    notify = "On";
                }
                else{
                    notify = "Off";
                }
            }
        });
        final AccountCtrl Ctrl = new AccountCtrl();
        smit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sttDay=strDt.getText().toString();
                endday=endDt.getText().toString();
                bName=Bname.getText().toString();
                amounts=tAmount.getText().toString();
                if(!sttDay.isEmpty() && !endday.isEmpty() && !amounts.isEmpty() && !bName.isEmpty()) {
                    Log.i("asd",selected);
                    Boolean msgBoxOut = (Ctrl.addbdget(currentUser.getUid(), FamilyId, bName, sttDay, endday, amounts, notify, selected));

                    if (msgBoxOut) {
                        AlertBox.alertBoxOut(BudgetAdd.this, getString(R.string.success), getString(R.string.budgetcriticalmsg));
                        strDt.setText("");
                        endDt.setText("");
                        Bname.setText("");
                        tAmount.setText("");
                    } else {
                        AlertBox.alertBoxOut(BudgetAdd.this, getString(R.string.error), getString(R.string.namecheckmsg));
                    }
                }
                else{
                    AlertBox.alertBoxOut(BudgetAdd.this, getString(R.string.error), getString(R.string.emptymsg));
                }
            }
        });
        forcast.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabases = FirebaseDatabase.getInstance().getReference();
                mDatabases.child("Budget").orderByChild("familyId").equalTo(FamilyId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        AlertBox alert=new AlertBox();
                        int i = 0;
                        if (dataSnapshot.hasChildren()) {
                            if(dataSnapshot.getChildrenCount()>2) {

                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    if (child.child("catagory").getValue().toString().equals(selected)) {
                                        String budgname = child.child("BudgetName").getValue().toString();
                                        String percentage = child.child("percentage").getValue().toString();
                                        String Amount = child.child("Amount").getValue().toString();
                                        double percentages = Double.parseDouble(percentage);
                                        double Amounts = Double.parseDouble(Amount);
                                        double usedamount = (Amounts * percentages) / 100;
                                        i = i + 1;
                                        usedlist.add(usedamount);
                                        amountlist.add(Amounts);
                                    }
                                }
                                if(i>1) {
                                    forecasts(usedlist, amountlist);
                                }
                                else{
                                    alert.alertBoxOut(BudgetAdd.this,getString(R.string.budgetforecast),getString(R.string.errorforecastmsg));
                                }
                            }
                            else{
                                alert.alertBoxOut(BudgetAdd.this,getString(R.string.budgetforecast),getString(R.string.errorforecastmsg));
                            }
                        }
                        else
                        {
                            alert.alertBoxOut(BudgetAdd.this,getString(R.string.budgetforecast),getString(R.string.errorforecastmsg));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            public void forecasts(ArrayList<Double> usedlist,ArrayList<Double> amountlist){
                double amounttot=0,avgamount=0,deffbudget=0,avgdeffdamount=0,forecastamount=0;
                for(int i=0;i<amountlist.size();i++)
                {
                    amounttot=amounttot+amountlist.get(i);
                }
                avgamount=amounttot/amountlist.size();
                Double[] diffrenceTwobudget = new Double[usedlist.size()];
                for(int i=0;i<usedlist.size()-1;i++)
                {
                    diffrenceTwobudget[i]=usedlist.get(i+1)-usedlist.get(i);
                }
                for(int i=0;i<diffrenceTwobudget.length-1;i++)
                {
                    deffbudget=deffbudget+diffrenceTwobudget[i];
                }
                avgdeffdamount=deffbudget/diffrenceTwobudget.length;
                forecastamount=avgamount+avgdeffdamount;
                if(forecastamount<avgamount){
                    min= String.format("%.2f",forecastamount);
                    max= String.format("%.2f",avgamount);
                }
                else{
                    min= String.format("%.2f",avgamount);
                    max= String.format("%.2f",forecastamount);
                }
                AlertBox alert=new AlertBox();
                alert.alertBoxOut(BudgetAdd.this,getString(R.string.budgetforecast),getString(R.string.lowvalueforecast)+" "+min+"\n"+getString(R.string.highvalueforecast)+" "+max);
            }
        });
    }
    @Override
    public void onClick(View v) {

        if(v==strDt){
            Calendar c= Calendar.getInstance();
            day=c.get(Calendar.DAY_OF_MONTH);
            mon=c.get(Calendar.MONTH);
            yr=c.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    strDt.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                }
            }
                    ,yr,mon,day);
            datePickerDialog.show();
        }
        if (v==endDt){
            Calendar c= Calendar.getInstance();
            day=c.get(Calendar.DAY_OF_MONTH);
            mon=c.get(Calendar.MONTH);
            yr=c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int yr, int mon, int day) {
                    endDt.setText(day+"/"+(mon+1)+"/"+yr);
                }
            },yr,mon,day);
            datePickerDialog.show();
        }
    }
}

