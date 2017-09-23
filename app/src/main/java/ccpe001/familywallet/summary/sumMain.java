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
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import ccpe001.familywallet.R;

import static ccpe001.familywallet.R.mipmap.category;

public class sumMain extends Fragment {

    public  PieChart charts;
    // float transac[] = {750.0f, 150.0f, 200.0f};
    //String category[] = {"food", "other", "fees"};
    private DatabaseReference rtrvdata; // creating database referrence to read data
    public final ArrayList<Float> transacval = new ArrayList<>(); // List to add transaction data
    public final ArrayList<String> dbcat = new ArrayList<>(); // List to add category
    // public String testcat[];
    // public Float testtransac[];
    // public Object ObjTransac[], ObjTestCat[];
    public Float transacdt;
    public String catdata;
    TextView integerTextView,stringTextView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sum_main, container, false);
        integerTextView = (TextView)view.findViewById(R.id.textView29);
        stringTextView = (TextView)view.findViewById(R.id.textView30);
        rtrvdata= FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rtrvdata.child("Transactions").equalTo(currentUser.getUid()).orderByChild("userID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // Data retrieving method

                for (DataSnapshot Transacdata : dataSnapshot.getChildren()) {
                    transacval.clear();
                    dbcat.clear();
                    //Assigning database values to variables
                    transacdt = Float.parseFloat(String.valueOf(Transacdata.child("amount").getValue()));
                    catdata = Transacdata.child("categoryName").getValue().toString();
                    //Log.d("Test", String.valueOf(transacdt));
                    transacval.add(transacdt);
                    dbcat.add(catdata);
                }

                for(int i=0; i < transacval.size(); i++){

                    integerTextView.setText(integerTextView.getText() + " " + transacval.get(i) + " , ");
                }
                for(int i=0; i < dbcat.size(); i++){

                    stringTextView.setText(stringTextView.getText() + dbcat.get(i) + " , ");
                }
                //SetupChart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       /* ObjTransac = transacval.toArray(new Object[transacval.size()]); //Covert Float array List to Object array
        ObjTestCat = dbcat.toArray(new Object[dbcat.size()]);        //Convert String array List to Object array
        for(int j=0; j < ObjTransac.length; j++)
        {
            testtransac[j]= (Float) ObjTransac[j]; //adding object array values to float array
        }

        for(int k=0; k < ObjTestCat.length; k++)
        {
            testcat [k]= (String) ObjTestCat[k];  //adding object array values to float array
        }*/

        // charts = (PieChart) view.findViewById(R.id.chart);
        //Pie chart method to populate
        return view;
    }
    private void SetupChart() //ref : https://www.youtube.com/watch?v=iS7EgKnyDeY
    {

      /*  ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < testtransac.length; i++) {
            pieEntries.add(new PieEntry(testtransac[i], testcat[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Transactions Done");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        charts.setData(data);
        charts.animateY(1000);
        charts.invalidate();*/

    }
}

