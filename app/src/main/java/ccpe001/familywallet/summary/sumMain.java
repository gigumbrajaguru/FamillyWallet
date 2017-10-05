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
    // creating database referrence to read data
    private DatabaseReference rtrvdata;
    // List to add transaction data
    public final ArrayList<String> transacval = new ArrayList<String>();
    // List to add category
    public final ArrayList<String> dbcat = new ArrayList<String>();
    public String []testcat;
    public Float[]testtransac;
    public String transacdt, catdata;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sum_main, container, false);
        charts = (PieChart) view.findViewById(R.id.chart);
        rtrvdata= FirebaseDatabase.getInstance().getReference();
        rtrvdata.child("Transactions").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            /**
             * Data retrieving method
             */
            public void onDataChange(DataSnapshot dataSnapshot) {
                transacval.clear();
                dbcat.clear();
                for (DataSnapshot Transacdata : dataSnapshot.getChildren()) {
                    //Assigning database values to variables
                    transacdt = (Transacdata.child("amount").getValue(String.class)).toString();
                    catdata = (Transacdata.child("categoryName").getValue(String.class)).toString();
                    transacval.add(transacdt);
                    dbcat.add(catdata);
                }

                int lstsize=transacval.size();
                int lstsizet=dbcat.size();
                testtransac=new Float[lstsize];
                testcat=new String[lstsizet];

                //ref : https://stackoverflow.com/questions/7379680/how-to-convert-arrayliststring-to-float
                for (int i = 0; i < lstsize; i++)
                {
                    testtransac[i] = Float.parseFloat(transacval.get(i));
                }
                //ref : https://stackoverflow.com/questions/7379680/how-to-convert-arrayliststring-to-float
                for (int j = 0; j < lstsizet; j++)
                {
                    testcat[j] = dbcat.get(j);
                }
                //Pie chart method to populate
                SetupChart();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

        PieDataSet dataSet = new PieDataSet(pieEntries, "Transactions Done");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        charts.setData(data);
        charts.animateY(1000);
        charts.invalidate();

    }
}

