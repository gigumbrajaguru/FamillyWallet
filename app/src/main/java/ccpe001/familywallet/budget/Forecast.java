package ccpe001.familywallet.budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ccpe001.familywallet.R;

public class Forecast extends AppCompatActivity {
    CombinedChart Chart;
	String budgetcat;
    ArrayList<BarEntry> group1 = new ArrayList<>();
    ArrayList<Double> usedlist = new ArrayList<>();
    ArrayList<Double> amountlist = new ArrayList<>();
    public static String getfid;
    public static BarData barData;
    private static DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
		Intent myIntent = getIntent();
        budgetcat = myIntent.getStringExtra("Category");
        TextView catText=(TextView)findViewById(R.id.viCat) ;
        catText.setText(budgetcat);
        Chart = (CombinedChart) findViewById(R.id.forecastChart);
        CombinedData data = new CombinedData();
        data.setData(barData());
        Chart.setData(data);
    }
    
    public BarData barData() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Budget").orderByChild("familyId").equalTo(getFamilyId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.child("catagory").getValue().toString().equals(budgetcat)) {
                            String xy = child.child("BudgetName").getValue().toString();
                            String percentage = child.child("percentage").getValue().toString();
                            String Amount = child.child("Amount").getValue().toString();
                            double percentages = Double.parseDouble(percentage);
                            double Amounts = Double.parseDouble(Amount);
                            double usedamount = (Amounts * percentages) / 100;
                            i = i + 1;
                            usedlist.add(usedamount);
                            amountlist.add(Amounts);
                            group1.add(new BarEntry(i, (int) usedamount));
                        }
                    }
                    forecasts(usedlist,amountlist);
                }
                BarDataSet barDataSet = new BarDataSet(group1, "Used amount for budget");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                 barData = new BarData(barDataSet);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return barData;
    }
    public String getFamilyId(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("UserInfo").orderByChild("userId").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Datatrasmit dp = new Datatrasmit();
                    dp.setfamilyId(child.child("familyId").getValue().toString());
                    getfid = dp.getfamilyId();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return getfid;
    }
    public void forecasts(ArrayList<Double> usedlist,ArrayList<Double> amountlist){
        double amounttot=0,avgamount=0,deffbudget=0,avgdeffdamount=0;
        String forecastamount;
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
        for(int i=0;i<diffrenceTwobudget.length;i++)
        {
            deffbudget=deffbudget+diffrenceTwobudget[i];
        }
        avgdeffdamount=deffbudget/diffrenceTwobudget.length;
        forecastamount=String.valueOf(avgamount+avgdeffdamount);
        TextView textForecast=(TextView)findViewById(R.id.forecastamoount);
        textForecast.setText("Calculated next budget amount:"+forecastamount);

    }

}
