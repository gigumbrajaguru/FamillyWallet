package ccpe001.familywallet.summary;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import ccpe001.familywallet.R;

import static ccpe001.familywallet.R.mipmap.category;

public class sumMain extends Fragment {

    //meeka athule charts tika karapn gihan, navigation drawer eka 'Reports' walin meekaai display weene
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.sum_main, container, false);
            //static array declaration
            float transac[] = {750.0f, 150.0f, 200.0f};
            String category[] = {"food", "other", "fees"};
            //Pie chart method to populate
            List<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < transac.length; i++) {
                pieEntries.add(new PieEntry(transac[i], category[i]));
            }

            PieDataSet dataSet = new PieDataSet(pieEntries, "Transactions Done");
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData data = new PieData(dataSet);
            PieChart charts;
            charts = (PieChart) view.findViewById(R.id.chart);
            charts.setData(data);
            charts.animateY(1000);
            charts.invalidate();
            return view;
        }

    }

