package ccpe001.familywallet.summary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.FloatProperty;
import android.util.Log;
import android.view.*;


import java.util.*;

import android.widget.*;
import ccpe001.familywallet.Translate;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import ccpe001.familywallet.R;
import org.apache.poi.util.ArrayUtil;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.media.CamcorderProfile.get;

public class sumMain extends Fragment {

    private  PieChart charts;
    private Spinner spinner;
    // creating database referrence to read data
    private DatabaseReference databaseReference;
    // List to add transaction data
    private final ArrayList<String> transAmount = new ArrayList<String>();
    // List to add category
    private final ArrayList<String> transCat = new ArrayList<String>();
    private String [] transCatChart;
    private Float[] transAmountChart;
    private Query query;
    private Translate translate;

    private String startDateVal,endDateVal,InGroup,familyID,uid;
    private int position;
    private SharedPreferences sharedPref;


    public sumMain(int position) {
        transAmount.clear();
        transCat.clear();
        this.position = position;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.sum_main, container, false);
        charts = (PieChart) view.findViewById(R.id.chart);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        translate = new Translate();

        ArrayList<String> rangesArr = new ArrayList<>();
        rangesArr.add(getString(R.string.this_week));
        rangesArr.add(getString(R.string.this_month));
        rangesArr.add(getString(R.string.last_month));
        rangesArr.add(getString(R.string.last_3_month));
        rangesArr.add(getString(R.string.last_year));
        rangesArr.add(getString(R.string.custom));
        CustomSpinner customSpinnerAdapter=new CustomSpinner(getActivity(),rangesArr);
        spinner.setAdapter(customSpinnerAdapter);


        sharedPref = getContext().getSharedPreferences("fwPrefs",0);
        InGroup = sharedPref.getString("InGroup", "");
        familyID = sharedPref.getString("uniFamilyID", "");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Transactions").child(uid);
        if (familyID.equals(uid) && !InGroup.equals("true")){
            databaseReference = databaseReference.child("Transactions").child(uid);
        }else {
            databaseReference =  databaseReference.child("Transactions").child("Groups").child(familyID);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transAmount.clear();
                transCat.clear();
                for (DataSnapshot Transacdata : dataSnapshot.getChildren()) {
                    if(position==0) {
                        if(Transacdata.child("type").getValue(String.class).toString().equals("Income")) {
                            transAmount.add((Transacdata.child("amount").getValue(String.class)).toString());
                            transCat.add(translate.categoryView((Transacdata.child("categoryName").getValue(String.class)).toString(),getActivity()));
                        }
                    }else if(position==1){
                        if(Transacdata.child("type").getValue(String.class).toString().equals("Expense")) {
                            transAmount.add((Transacdata.child("amount").getValue(String.class)).toString());
                            transCat.add(translate.categoryView((Transacdata.child("categoryName").getValue(String.class)).toString(),getActivity()));
                        }
                    }

                }

                //getting rid of duplicate categories
                Set<String> lump = new HashSet<String>();
                int z=0;
                try {
                    for (String i : transCat) {
                        z++;
                        if (!lump.contains(i)) {
                            float t = Float.parseFloat(transAmount.get(z)) + Float.parseFloat(transAmount.get(z + 1));
                            transAmount.set(z, String.valueOf(t));
                            transAmount.remove(z + 1);
                            lump.add(i);
                        }
                    }
                }catch(Exception e){

                }

                transAmountChart =new Float[transAmount.size()];
                transCatChart =new String[transCat.size()];

                for (int i = 0; i < transAmount.size(); i++) {
                    Log.d("LOG",""+transAmount.get(i)+" "+i);
                    transAmountChart[i] = Float.parseFloat(transAmount.get(i));
                }

                for (int j = 0; j < transCat.size(); j++) {
                    Log.d("LOG",""+transCat.get(j)+" "+j);
                    transCatChart[j] = transCat.get(j);
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
                Calendar calendar = Calendar.getInstance();

                String todayDate = translate.dateToValue(translate.dateWithDoubleDigit(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),getActivity()));


                if (i == 0) {
                    calendar.setTimeInMillis(calendar.getTimeInMillis());
                    while (calendar.get(Calendar.DATE) > 1) {
                        calendar.add(Calendar.DATE, -1); // Substract 1 day until first day of month.
                    }

                    startDateVal = translate.dateToValue(translate.dateWithDoubleDigit(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),getActivity()));
                    endDateVal = todayDate;

                    filter(startDateVal,endDateVal);
                }else if (i == 1) {
                    calendar.setTimeInMillis(calendar.getTimeInMillis());
                    while (calendar.get(Calendar.DATE) > 1) {
                        calendar.add(Calendar.DATE, -1); // Substract 1 day until first day of month.
                    }
                    startDateVal = String.valueOf(calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH));
                    endDateVal = todayDate;

                    filter(startDateVal,endDateVal);
                } else if (i == 2) {
                    calendar.add(Calendar.MONTH, -1);
                    startDateVal = String.valueOf(calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH));
                    endDateVal = todayDate;
                    filter(startDateVal,endDateVal);

                } else if (i == 3) {
                    calendar.add(Calendar.MONTH, -3);
                    startDateVal = String.valueOf(calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH));
                    endDateVal = todayDate;
                    filter(startDateVal,endDateVal);

                }else if (i == 4) {
                    calendar.add(Calendar.MONTH, -3);
                    startDateVal = String.valueOf(calendar.get(Calendar.YEAR)+calendar.get(Calendar.MONTH)+calendar.get(Calendar.DAY_OF_MONTH));
                    endDateVal = todayDate;
                    filter(startDateVal,endDateVal);

                }  else if (i == 5) {
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

                    datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                            String date = translate.dateWithDoubleDigit(i,i1+1,i2,getActivity());
                            if(startDate.isFocused()) {
                                startDate.setText(date);
                                startDateVal = translate.dateToValue(date);
                            }else {
                                endDate.setText(date);
                                endDateVal = translate.dateToValue(date);
                            }
                        }
                    });

                    nameBuilder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            filter(startDateVal,endDateVal);
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


     private void filter(final String startDateVal, final String endDateVal){
        Log.d("LOG", "onDataChange"+startDateVal+"  "+endDateVal);

        if (familyID.equals(uid) && !InGroup.equals("true")){
            query = FirebaseDatabase.getInstance().getReference("Transactions").child(uid).orderByChild("date").startAt(startDateVal).endAt(endDateVal);
        }else {
            query = FirebaseDatabase.getInstance().getReference("Transactions").child("Groups").child(familyID).orderByChild("date").startAt(startDateVal).endAt(endDateVal);
        }


        String amonut,cat;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        transAmount.clear();
                        transCat.clear();
                        if (position == 0) {
                            if (dataSnapshot1.child("type").getValue(String.class).toString().equals("Income")) {
                                transAmount.add(dataSnapshot1.child("amount").getValue(String.class).toString());
                                transCat.add(translate.categoryView((dataSnapshot1.child("categoryName").getValue(String.class)).toString(),getActivity()));
                            }
                        } else if (position == 1) {
                            if (dataSnapshot1.child("type").getValue(String.class).toString().equals("Expense")) {
                                transAmount.add((dataSnapshot1.child("amount").getValue(String.class)).toString());
                                transCat.add(translate.categoryView((dataSnapshot1.child("categoryName").getValue(String.class)).toString(),getActivity()));
                            }
                        }
                }

                if(dataSnapshot.exists()) {
                    transAmountChart = new Float[transAmount.size()];
                    transCatChart = new String[transCat.size()];

                    for (int i = 0; i < transAmount.size(); i++) {
                        transAmountChart[i] = Float.parseFloat(transAmount.get(i));
                    }

                    for (int j = 0; j < transCat.size(); j++) {
                        transCatChart[j] = transCat.get(j);
                    }

                    SetupChart();
                }else{
                    charts.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    /**
     * Pie chart method implementation
     * https://www.youtube.com/watch?v=iS7EgKnyDeY
     */
    private void SetupChart() {

       /*try {
           for (int i = 0; i < transAmountChart.length; i++) {
               //Log.d("LOG", "outer" + i + transCatChart[i] + transAmountChart[i]);

               if (i == transAmountChart.length) {
                   Log.d("LOG", "if" + i + transCatChart[i] + transAmountChart[i]);

                   return;
               } else if (transCatChart[i].equals(transCatChart[i + 1])) {
                   Log.d("LOG", "else if" + i + transCatChart[i] + transAmountChart[i]+"//njb"+transAmountChart.length);
                   transAmountChart[i] = transAmountChart[i] + transAmountChart[i + 1];
               }
           }
       }catch (Exception e){
           Log.d("LOG", "error" +e.getMessage());
       }*/




        //showing pieEntries in chart
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < transAmountChart.length; i++) {
            pieEntries.add(new PieEntry(transAmountChart[i], transCatChart[i]));
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

    boolean duplicates(String[] zipcodelist)
    {
        Set<String> lump = new HashSet<String>();
        for (String i : zipcodelist)
        {
            if (lump.contains(i)) return true;
            lump.add(i);
        }
        return false;
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

