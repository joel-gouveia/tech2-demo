package tech2.demo.com.demo.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import tech2.demo.com.demo.R;
import tech2.demo.com.demo.common.DateUtils;

/**
 * Created by Joel on 02-Mar-16.
 */
public class FilterFragment extends DialogFragment {

    private OnDialogConfirmation mListener;
    private TextView mDateFrom, mDateTo, mTimeFrom, mTimeTo;
    private ImageView mConfirm, mReset;
    private Calendar mFrom, mTo;
    private CheckBox mDateCheck, mTimeCheck;

    public FilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fragment_filter);
        dialog.show();

        mDateFrom = (TextView) dialog.findViewById(R.id.fragment_dialog_date_from);
        mDateTo = (TextView) dialog.findViewById(R.id.fragment_dialog_date_to);
        mTimeFrom = (TextView) dialog.findViewById(R.id.fragment_dialog_time_from);
        mTimeTo = (TextView) dialog.findViewById(R.id.fragment_dialog_time_to);
        mConfirm = (ImageView) dialog.findViewById(R.id.fragment_dialog_confirm);
        mReset = (ImageView) dialog.findViewById(R.id.fragment_dialog_reset);
        mDateCheck = (CheckBox) dialog.findViewById(R.id.fragment_dialog_checkbox_date);
        mTimeCheck = (CheckBox) dialog.findViewById(R.id.fragment_dialog_checkbox_time);

        Calendar cal = Calendar.getInstance();

        mDateFrom.setText(DateUtils.formatDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        mDateTo.setText(DateUtils.formatDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        mTimeFrom.setText(DateUtils.formatTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
        mTimeTo.setText(DateUtils.formatTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

        mFrom = mTo = Calendar.getInstance();

        mDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear, mMonth, mDay;
                mYear = mFrom.get(Calendar.YEAR);
                mMonth = mFrom.get(Calendar.MONTH);
                mDay = mFrom.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String date = DateUtils.formatDate(dayOfMonth, monthOfYear, year);
                                mDateFrom.setText(date);
                                mFrom.set(year, monthOfYear, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear, mMonth, mDay;
                mYear = mTo.get(Calendar.YEAR);
                mMonth = mTo.get(Calendar.MONTH);
                mDay = mTo.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String date = DateUtils.formatDate(dayOfMonth, monthOfYear, year);
                                mDateTo.setText(date);
                                mTo.set(year, monthOfYear, dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour, mMinute;
                mHour = mFrom.get(Calendar.HOUR_OF_DAY);
                mMinute = mFrom.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String time = DateUtils.formatTime(hourOfDay, minute);
                                mTimeFrom.setText(time);
                                mFrom.set(mFrom.get(Calendar.YEAR),
                                        mFrom.get(Calendar.MONTH),
                                        mFrom.get(Calendar.DAY_OF_MONTH),
                                        hourOfDay, minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        mTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour, mMinute;
                mHour = mTo.get(Calendar.HOUR_OF_DAY);
                mMinute = mTo.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String time = DateUtils.formatTime(hourOfDay, minute);
                                mTimeTo.setText(time);
                                mTo.set(mTo.get(Calendar.YEAR),
                                        mTo.get(Calendar.MONTH),
                                        mTo.get(Calendar.DAY_OF_MONTH),
                                        hourOfDay, minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimeCheck.isChecked() && mDateCheck.isChecked()){
                    mListener.onDatesSet(mDateFrom.getText().toString(), mTimeFrom.getText().toString(),
                            mDateTo.getText().toString(), mTimeTo.getText().toString());
                    dialog.dismiss();
                } else {
                    if(mTimeCheck.isChecked()){
                        mListener.onDatesSet("", mTimeFrom.getText().toString(), "", mTimeTo.getText().toString());
                        dialog.dismiss();
                    } else if(mDateCheck.isChecked()){
                        mListener.onDatesSet(mDateFrom.getText().toString(), "", mDateTo.getText().toString(), "");
                        dialog.dismiss();
                    }
                }

            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDatesSet("", "", "", "");

                dialog.dismiss();
            }
        });

        return dialog;
    }

    public void setOnDialogConfirmation(OnDialogConfirmation mListener) {
        this.mListener = mListener;
    }

    public interface OnDialogConfirmation {
        void onDatesSet(String fromDate, String fromTime, String toDate, String toTime);
    }
}
