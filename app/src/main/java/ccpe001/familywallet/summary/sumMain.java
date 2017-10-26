package ccpe001.familywallet.summary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;*/

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.widget.*;
import ccpe001.familywallet.CustomAlertDialogs;
import ccpe001.familywallet.Validate;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ccpe001.familywallet.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class sumMain extends Fragment {

    private  PieChart charts;
    private Spinner spinner;
    // creating database referrence to read data
    private DatabaseReference rtrvdata;
    // List to add transaction data
    private final ArrayList<String> transAmount = new ArrayList<String>();
    // List to add category
    private final ArrayList<String> transCat = new ArrayList<String>();
    private String []testcat;
    private Float[]testtransac;

    private int position;


    public sumMain(int position) {
        transAmount.clear();
        transCat.clear();
        this.position = position;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.sum_main, container, false);
        charts = (PieChart) view.findViewById(R.id.chart);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        ArrayList<String> rangesArr = new ArrayList<>();
        rangesArr.add(getString(R.string.this_month));
        rangesArr.add(getString(R.string.last_month));
        rangesArr.add(getString(R.string.last_3_month));
        rangesArr.add(getString(R.string.custom));
        CustomSpinner customSpinnerAdapter=new CustomSpinner(getActivity(),rangesArr);
        spinner.setAdapter(customSpinnerAdapter);

        rtrvdata= FirebaseDatabase.getInstance().getReference();
        rtrvdata.child("Transactions").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transAmount.clear();
                transCat.clear();
                for (DataSnapshot Transacdata : dataSnapshot.getChildren()) {
                    if(position==0) {
                        if(Transacdata.child("type").getValue(String.class).toString().equals("Income")) {

                            transAmount.add((Transacdata.child("amount").getValue(String.class)).toString());
                            transCat.add((Transacdata.child("categoryName").getValue(String.class)).toString());
                        }
                    }else if(position==1){
                        if(Transacdata.child("type").getValue(String.class).toString().equals("Expense")) {
                            transAmount.add((Transacdata.child("amount").getValue(String.class)).toString());
                            transCat.add((Transacdata.child("categoryName").getValue(String.class)).toString());
                        }
                    }

                }

                testtransac=new Float[transAmount.size()];
                testcat=new String[transCat.size()];

                for (int i = 0; i < transAmount.size(); i++) {
                    testtransac[i] = Float.parseFloat(transAmount.get(i));
                }

                for (int j = 0; j < transCat.size(); j++) {
                    testcat[j] = transCat.get(j);
                }

                //Pie chart method to populate
                SetupChart();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {

                } else if (i == 1) {

                } else if (i == 2) {

                } else if (i == 3) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    final AlertDialog.Builder nameBuilder = new AlertDialog.Builder(getActivity());
                    View alertDiaView = inflater.inflate(R.layout.date_setter, null);
                    nameBuilder.setView(alertDiaView)
                            .setPositiveButton(R.string.change, null)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            }).create();

                    final DatePicker datePicker = (DatePicker) alertDiaView.findViewById(R.id.datePicker);
                    final EditText startDate = (EditText) alertDiaView.findViewById(R.id.startDate);
                    final EditText endDate = (EditText) alertDiaView.findViewById(R.id.endDate);

                    Calendar calendar = Calendar.getInstance();
                    datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                            int day = datePicker.getDayOfMonth();
                            int month = datePicker.getMonth() + 1;
                            int year = datePicker.getYear();
                            String date = (day+"/"+month+"/"+year);

                            if(startDate.isFocused()) {
                                startDate.setText(date);
                            }else {
                                endDate.setText(date);
                            }
                        }
                    });



                    nameBuilder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {


                        }
                    });

                    AlertDialog alertDialog = nameBuilder.show();
                    alertDialog.getWindow().setLayout((int) (375 * Resources.getSystem().getDisplayMetrics().density),
                            (int) (650 * Resources.getSystem().getDisplayMetrics().density));
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }




    /**
     * Pie chart method implementation
     * https://www.youtube.com/watch?v=iS7EgKnyDeY
     */
    private void SetupChart()
    {

       ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < testtransac.length; i++) {
            pieEntries.add(new PieEntry(testtransac[i], testcat[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, null);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        charts.setData(data);
        charts.animateY(1000);
        charts.setRotationEnabled(true);
        charts.setNoDataText(getString(R.string.no_chart_data));
        charts.getDescription().setEnabled(false);
        charts.setDrawHoleEnabled(true);

        charts.setHoleRadius(7);
        charts.setTransparentCircleRadius(10);
        charts.setDrawSlicesUnderHole(true);

        charts.invalidate();

    }

    //http://blog.nkdroidsolutions.com/android-custom-spinner-dropdown-example-programmatically/
    public class CustomSpinner extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> asr;

        public CustomSpinner(Context context,ArrayList<String> asr) {
            this.asr=asr;
            activity = context;
        }

        public int getCount()
        {
            return asr.size();
        }

        public Object getItem(int i)
        {
            return asr.get(i);
        }

        public long getItemId(int i)
        {
            return (long)i;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(getActivity());
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(getActivity());
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.triangle, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }
    }

}

