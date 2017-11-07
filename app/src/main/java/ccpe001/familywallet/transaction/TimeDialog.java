package ccpe001.familywallet.transaction;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;

import ccpe001.familywallet.Translate;

/**
 * Created by Knight on 5/13/2017.
 */

public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TextView txtTime;
    private Translate tran = new Translate();

    public TimeDialog(View view) {
        txtTime = (TextView) view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int am_pm = c.get(Calendar.AM_PM);

        return new TimePickerDialog(getActivity(), this, hour, minute, false);

    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        String am_pm = (hour < 12) ? "AM" : "PM";

        if (hour>12){
            hour=hour-12;
        }

        if (hour==0)
            hour=12;

        String time1 = pad(hour) + ":" + pad(minute)+" "+am_pm;
        txtTime.setText(tran.timeView(time1,getActivity()));
    }

    public static String pad(int input)
    {

        String str = "";

        if (input >= 10) {

            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);

        }
        return str;
    }
}
