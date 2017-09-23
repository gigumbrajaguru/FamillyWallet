package ccpe001.familywallet.summary;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;*/

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import ccpe001.familywallet.R;

import static ccpe001.familywallet.R.mipmap.category;

public class sumMain extends Fragment {

    public  PieChart charts;
    private DatabaseReference rtrvdata; // creating database referrence to read data
    public final ArrayList<String> transacval = new ArrayList<>(); // List to add transaction data
    public final ArrayList<String> dbcat = new ArrayList<>(); // List to add category
    TextView integerTextView,stringTextView;
    public String []testcat;
    public Float[]testtransac;
    public String transacdt;
    public String catdata;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sum_main, container, false);
        integerTextView = (TextView)view.findViewById(R.id.textView29);
        stringTextView = (TextView)view.findViewById(R.id.textView30);
        rtrvdata= FirebaseDatabase.getInstance().getReference();
        rtrvdata.child("Transactions").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // Data retrieving method
                Log.d("dbval",dataSnapshot.toString());
                for (DataSnapshot Transacdata : dataSnapshot.getChildren()) {
                    transacval.clear();
                    dbcat.clear();
                    //Assigning database values to variables
                    transacdt = (Transacdata.child("amount").getValue(String.class)).toString();
                    catdata = (Transacdata.child("categoryName").getValue(String.class)).toString();
                    transacval.add(transacdt);
                    dbcat.add(catdata);
                }
                for(int i=0; i < transacval.size(); i++){

                    integerTextView.setText(integerTextView.getText() + " " + transacval.get(i) + " , ");
                }
                for(int i=0; i < dbcat.size(); i++){

                    stringTextView.setText(stringTextView.getText() + dbcat.get(i) + " , ");
                }
               /* testtransac=new Float[transacval.size()];
                for (int i = 0; i < transacval.size(); i++) //ref : https://stackoverflow.com/questions/7379680/how-to-convert-arrayliststring-to-float
                {
                    testtransac[i] = Float.parseFloat(transacval.get(i));
                }
                testcat=new String[dbcat.size()];
                for (int j = 0; j < dbcat.size(); j++) //ref : https://stackoverflow.com/questions/7379680/how-to-convert-arrayliststring-to-float
                {
                    testcat[j] = dbcat.get(j);
                }*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       // charts = (PieChart) view.findViewById(R.id.chart);
        //Pie chart method to populate
        //SetupChart();
        return view;
    }
    /*private void SetupChart() //ref : https://www.youtube.com/watch?v=iS7EgKnyDeY
    {

       ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < testtransac.length; i++) {
            pieEntries.add(new PieEntry(testtransac[i], testcat[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Transactions Done");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        charts.setData(data);
        charts.animateY(1000);
        charts.invalidate();

    }*/
}

