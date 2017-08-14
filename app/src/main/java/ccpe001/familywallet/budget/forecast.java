package ccpe001.familywallet.budget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import ccpe001.familywallet.R;

public class forecast extends AppCompatActivity {
    CombinedChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        lineChart = (CombinedChart) findViewById(R.id.forecastChart);
        CombinedData data = new CombinedData();
         data.setData(barData());
         data.setData(lineData());
        lineChart.setData(data);
    }
    public ArrayList<String>getXAxisValues() {
            ArrayList<String> labels = new ArrayList<>();
            labels.add("JAN");
            labels.add("FEB");
            labels.add("MAR");
            labels.add("APR");
            labels.add("MAY");
        return labels;
    }
    public LineData lineData(){
            ArrayList<Entry> line = new ArrayList <>();
            line.add(new Entry(2, 2));
            line.add(new Entry(4, 4));
            line.add(new Entry(6, 6));
            line.add(new Entry(8, 8));
            line.add(new Entry(10, 10));
          LineDataSet lineDataSet = new LineDataSet(line, "Month 2");
                lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        LineData lineData = new LineData(lineDataSet);
       return lineData;
  }
    public BarData barData(){
      ArrayList<BarEntry> group1 = new ArrayList<>();
                group1.add(new BarEntry(2, 4));
                group1.add(new BarEntry(4, 18));
                group1.add(new BarEntry(6, 12));
                group1.add(new BarEntry(8, 16));
                group1.add(new BarEntry(10, 32));
       BarDataSet barDataSet = new BarDataSet(group1, "Month 1");

                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
      BarData barData = new BarData(barDataSet);
        return barData;
    }
}

