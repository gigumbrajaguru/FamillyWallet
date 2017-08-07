package ccpe001.familywallet.summary;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import ccpe001.familywallet.R;

//added library
//Not working
public class pieChart extends Fragment{

    float transaction[]={150.0f,235.50f,187.50f};
    String category[]={"food","fuel","other"};
    int i;
    private PieChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_pie_chart, container, false);
        chart = (PieChart) view.findViewById(R.id.chart);
        setupPieChart();
        return  view;

    }

    private void setupPieChart(){
        List<PieEntry> pieEntries=new ArrayList<>();

        for( i = 0; i < transaction.length; i++){}
        pieEntries.add(new PieEntry(transaction[i], category[i]));

        PieDataSet dataSet = new PieDataSet(pieEntries,"Transaction of Categories");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data=new PieData(dataSet);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();

    }
}
