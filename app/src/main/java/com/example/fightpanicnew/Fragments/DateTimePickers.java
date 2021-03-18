/*
Date time pickeri za pill diary i panic calendar aktivnosti.
*/

package com.example.fightpanicnew.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.fightpanicnew.R;

import java.util.Calendar;
import java.util.Locale;

public class DateTimePickers {

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            EditText panicAttackPillDiaryTimeEditText = getActivity().findViewById(R.id.AddPanicAttackRecordEditTextTime);
            if (panicAttackPillDiaryTimeEditText == null) {
                panicAttackPillDiaryTimeEditText = getActivity().findViewById(R.id.AddPillDiaryRecordEditTextTime);
            }
            panicAttackPillDiaryTimeEditText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String dateString = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
            EditText panicAttackPillDiaryDateEditText = getActivity().findViewById(R.id.AddPanicAttackRecordEditTextDate);
            if (panicAttackPillDiaryDateEditText == null) {
                panicAttackPillDiaryDateEditText = getActivity().findViewById(R.id.AddPillDiaryRecordEditTextDate);
            }
            panicAttackPillDiaryDateEditText.setText(dateString);

        }
    }


}
