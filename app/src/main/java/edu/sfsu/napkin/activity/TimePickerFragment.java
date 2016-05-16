package edu.sfsu.napkin.activity;

/**
 * Created by ty on 11/16/15.
 */


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.widget.TimePicker;

import edu.sfsu.napkin.R;


/**
 * A simple {@link Fragment} subclass.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int timeSet = c.get(Calendar.AM_PM);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
}

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){

        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) Texiew widget
        TextView tv = (TextView) getActivity().findViewById(R.id.timerView);
        //Display the user changed time on TextView
        if (hourOfDay>12){
            hourOfDay -=12;
        }
        tv.setText(tv.getText() + String.valueOf(hourOfDay) + (":") + String.valueOf(minute));
    }
}