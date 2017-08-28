package ccpe001.familywallet.transaction;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import java.util.Calendar;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import ccpe001.familywallet.Translate;

/**
 * Created by Knight on 5/13/2017.
 */

public class DateDialog  extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    TextView txtDate;
    Translate trns = new Translate();

    public DateDialog(){

    }


    public DateDialog(View view){
        txtDate=(TextView)view;
    }


    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this,year,month,day);
    }

    //fdsdf
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String date = dayOfMonth +"-"+MONTHS[month]+"-"+year;
        String dateVal = trns.dateToDB(date);
        txtDate.setText(trns.valueToDate(dateVal,getActivity()));
    }

    public String pad(int input)
    {

        String str = "";

        if (input > 10) {

            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);

        }
        return str;
    }


}
