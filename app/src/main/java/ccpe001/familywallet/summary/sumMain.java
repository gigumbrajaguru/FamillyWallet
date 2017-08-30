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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rtrvdata= FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rtrvdata.child("Transaction").orderByChild("userID").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // Data retrieving method

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    //Assigning database values to variables
                    Float transacdt = (Float) child.child("amount").getValue();
                    String catdata = (String) child.child("categoryName").getValue();
                    transacval.add(transacdt);
                    dbcat.add(catdata);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
           Log.i("test",transacval.toString());
        View view = inflater.inflate(R.layout.sum_main, container, false);
        charts = (PieChart) view.findViewById(R.id.chart);
        //Pie chart method to populate
        SetupChart();
        return view;
    }
    private void SetupChart() //ref : https://www.youtube.com/watch?v=iS7EgKnyDeY
    {

        Float testtransac[] = transacval.toArray(new Float[transacval.size()]); //Covert Float array List to float array
        String testcat[] = dbcat.toArray(new String[dbcat.size()]);        //Convert String array List to String array
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

