package ccpe001.familywallet.budget;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import ccpe001.familywallet.R;

public class budgetTrack extends AppCompatActivity{
    Button btnUpdates,btnDelete;
    TextView budNames,CategoryTexts,PercentageTexts,strtDates;
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase;
    private int day,mon,yr,x,y,z;
    String budgetI;
    EditText endDys,budam;
    Switch switch1;
    String Notify;
    private DatabaseReference Mdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_track);
        Intent myIntent = getIntent();
        budgetI = myIntent.getStringExtra("budgetID");
        final ProgressBar progressbar=(ProgressBar) findViewById(R.id.progressBar3);
        btnUpdates = (Button) findViewById(R.id.btnUpdate);
        btnDelete=(Button)findViewById(R.id.btnDudDel);
        budNames=(TextView) findViewById(R.id.budName);
        CategoryTexts=(TextView)findViewById(R.id.CategoryText);
        PercentageTexts=(TextView)findViewById(R.id.PercentageText);
        strtDates=(TextView)findViewById(R.id.strtDate);
        budam=(EditText)findViewById(R.id.budamounts);
        endDys=(EditText)findViewById(R.id.endDates);
        switch1=(Switch)findViewById(R.id.switchnoty) ;
        Mdatabase = FirebaseDatabase.getInstance().getReference("Budget");
        Mdatabase.child(budgetI).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals("BudgetName")) {
                            budNames.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("Amount")) {
                            budam.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("catagory")) {
                            CategoryTexts.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("endDays")) {
                            endDys.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("percentage")) {
                            PercentageTexts.setText(child.getValue().toString());
                            int x = Integer.parseInt(child.getValue().toString());
                            progressbar.setProgress(x);
                        }
                        if (child.getKey().equals("startDate")) {
                            strtDates.setText(child.getValue().toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Notify="True";
                }
                else {
                    Notify="False";
                }

            }
        });
        endDys.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentDate=Calendar.getInstance();
                yr=mcurrentDate.get(Calendar.YEAR);
                mon=mcurrentDate.get(Calendar.MONTH);
                day=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(budgetTrack.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDys.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                    }
                },day,mon,yr);
                datePickerDialog.show();  }
        });

    btnUpdates.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            x=0;z=0;y=0;
            final String edate=endDys.getText().toString();
            final String amoun=budam.getText().toString();
            mDatabase = FirebaseDatabase.getInstance().getReference("Budget");
            AlertDialog.Builder builder = new AlertDialog.Builder(budgetTrack.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Do you want proceed ?");
            builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mDatabase.child(budgetI).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot child: dataSnapshot.getChildren()) {
                                if (child.getKey().equals("Amount")) {
                                    if (x==0){
                                        child.getRef().setValue(amoun);
                                        x=1;
                                    }
                                }
                                else if (child.getKey().equals("endDays")){
                                    if (y==0) {
                                        child.getRef().setValue(edate);
                                        y=1;
                                    }
                                }
                                else if (child.getKey().equals("notification")){
                                    if (z==0) {
                                        child.getRef().setValue(Notify);
                                        z=1;
                                    }
                                }

                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
    });
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference("Budget");
                mDatabase.child(budgetI).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(budgetTrack.this);
                        builder.setTitle(R.string.app_name);
                        builder.setMessage("Do you want proceed ?");
                        builder.setIcon(R.drawable.ic_launcher);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dataSnapshot.getRef().removeValue();
                                moveBack();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }
    public void moveBack(){
        Intent newInt3 = new Intent(budgetTrack.this, BudgetHandling.class);
        startActivity(newInt3);
    }


}
