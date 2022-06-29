package rs.com.loctionbased.reminder.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import java.util.Calendar;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.interfaces.TaskDataInterface;
import rs.com.loctionbased.reminder.enums.DateFormat;
import rs.com.loctionbased.reminder.enums.TimeFormat;
import rs.com.loctionbased.reminder.model.Time;
import rs.com.loctionbased.reminder.model.reminder.OneTimeReminder;
import rs.com.loctionbased.reminder.util.CalendarUtil;
import rs.com.loctionbased.reminder.util.SharedPreferenceUtil;

public class EditOneTimeReminderFragment extends Fragment implements TaskDataInterface {

    //CONST
    public static final String REMINDER_ARGUMENT = "REMINDER_ARGUMENT";

    //DATA
    OneTimeReminder mReminder;
    DateFormat mDateFormat;

    //UI
    private EditText mDate;
    private EditText mTime;
    private CalendarDatePickerDialogFragment mDatePicker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments().containsKey(REMINDER_ARGUMENT))
            mReminder = (OneTimeReminder) getArguments().getSerializable(REMINDER_ARGUMENT);

        else
            Toast.makeText(getActivity(), getResources().getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();


        mDateFormat = SharedPreferenceUtil.getDateFormat(getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_one_time_reminder, container, false);

        mDate = (EditText) rootView.findViewById(R.id.fragment_edit_one_time_reminder_date);
        mTime = (EditText) rootView.findViewById(R.id.fragment_edit_one_time_reminder_time);

        setupDateAndTimePickers();
        setReminderValues();

        return rootView;
    }


    private void setupDateAndTimePickers() {

        final Calendar mToday = Calendar.getInstance();
        final Calendar mTomorrow = Calendar.getInstance();
        mTomorrow.add(Calendar.DAY_OF_MONTH, 1);

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePicker = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                if(mReminder.getDate() == null) {
                                    mReminder.setDate(CalendarUtil.getNewInstanceZeroedCalendar());
                                }
                                mReminder.getDate().set(year, monthOfYear, dayOfMonth);
                                mDate.setText(mDateFormat.formatCalendar(mReminder.getDate()));
                            }
                        })
                        .setPreselectedDate(mToday.get(Calendar.YEAR), mToday.get(Calendar.MONTH), mToday.get(Calendar.DAY_OF_MONTH))
                        .setDateRange(new MonthAdapter.CalendarDay(mToday), null)
                        .setDoneText(getResources().getString(R.string.datepicker_ok))
                        .setCancelText(getResources().getString(R.string.datepicker_cancel));
                mDatePicker.show(getFragmentManager(), "mDate");
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                if(mReminder.getTime() == null) {
                                    mReminder.setTime(new Time());
                                    mReminder.getTime().setDisplayTimeFormat(SharedPreferenceUtil.getTimeFormat(getActivity()));
                                }
                                mReminder.getTime().setHour(hourOfDay);
                                mReminder.getTime().setMinute(minute);
                                mTime.setText(mReminder.getTime().toString());
                            }
                        })
                        .setStartTime(12, 0)
                        .setDoneText(getResources().getString(R.string.datepicker_ok))
                        .setCancelText(getResources().getString(R.string.datepicker_cancel));
                if(SharedPreferenceUtil.getTimeFormat(getActivity()).equals(TimeFormat.FORMAT_12H)) rtpd.setForced12hFormat();
                else rtpd.setForced24hFormat();
                        rtpd.show(getFragmentManager(), "mTime");
            }
        });
    }

    private void setReminderValues() {
        if(mReminder.getDate() != null) {
            mDate.setText(mDateFormat.formatCalendar(mReminder.getDate()));
        }

        if(mReminder.getTime() != null) {
            mTime.setText(mReminder.getTime().toString());
        }
    }

    @Override
    public void updateData() {
    }

}
