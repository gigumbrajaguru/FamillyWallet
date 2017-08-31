package ccpe001.familywallet.budget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import ccpe001.familywallet.R;

public class BudgetHandling extends AppCompatActivity implements View.OnClickListener {
    EditText strDt,endDt,Bname,tAmount;
    Button smit;
    Switch noty;
    private static DatabaseReference mDatabases;
    private  int day,mon,yr;
    private String[] arraySpinner;
    String selected,sttDay,endday,bName,amounts,notify="Off",Fname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.arraySpinner = new String[] {
                "Food", "Travel"
        };
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabases = FirebaseDatabase.getInstance().getReference();
        mDatabases.child("UserInfo").orderByChild("userId").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Fname=child.child("familyId").getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        setContentView(R.layout.budget_handling);
        strDt=(EditText)findViewById(R.id.startDate);
        endDt=(EditText)findViewById(R.id.endDate);
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
                selected = parent.getItemAtPosition(position).toString();
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
                notify="On";
            }
        });
        final acountCtrl Ctrl = new acountCtrl();
        smit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sttDay=strDt.getText().toString();
                endday=endDt.getText().toString();
                bName=Bname.getText().toString();
                amounts=tAmount.getText().toString();
                Boolean msgBoxOut =(Ctrl.addbdget(currentUser.getUid(),Fname,bName,sttDay,endday,amounts,notify,selected));
                if (msgBoxOut) {
                    alertBox.alertBoxOut(BudgetHandling.this, "Data Stored", "Succeed");
                }
             else {
                alertBox.alertBoxOut(BudgetHandling.this, "Account Name ", "Change your account name");
            }
            }
        });
    }
    @Override
    public void onClick(View v) {
        if(v==strDt){
            final Calendar c= Calendar.getInstance();
            day=c.get(Calendar.DAY_OF_MONTH);
            mon=c.get(Calendar.MONTH);
            yr=c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    strDt.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                }
            }
                    ,day,mon,yr);
            datePickerDialog.show();
        }
        if (v==endDt){
            final Calendar c= Calendar.getInstance();
            day=c.get(Calendar.DAY_OF_MONTH);
            mon=c.get(Calendar.MONTH);
            yr=c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    endDt.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                }
            },day,mon,yr);
            datePickerDialog.show();
        }
    }
    public void nxtForecast(View v){
        Intent newInt1 = new Intent(this,forecast.class);
        startActivity(newInt1);
    }
}
