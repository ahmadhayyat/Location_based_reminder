package rs.com.loctionbased.reminder.app.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.enums.DateFormat;
import rs.com.loctionbased.reminder.model.reminder.RepeatingReminder;
import rs.com.loctionbased.reminder.util.SharedPreferenceUtil;
import rs.com.loctionbased.reminder.util.SnackbarUtil;
import rs.com.loctionbased.reminder.util.TaskUtil;

public class RepeatingReminderDetailFragment extends Fragment {

    //CONST
    public static final String REMINDER_TO_DISPLAY = "REMINDER_TO_DISPLAY";

    //DATA
    private RepeatingReminder mReminder;

    //UI
    private LinearLayout mContainer;
    private ImageView mDateIcon;
    private TextView mDate;
    private TextView mTime;
    private TextView mRepeat;
    private TextView mNext;
    private LinearLayout mNextContainer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments().containsKey(REMINDER_TO_DISPLAY)) {
            mReminder = (RepeatingReminder) getArguments().getSerializable(REMINDER_TO_DISPLAY);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    getActivity().finish();
                }
            };
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.fragment_location_based_reminder_detail_snackbar_error_no_reminder, SnackbarUtil.SnackbarDuration.LONG, callback);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_reminder_repeating, container, false);

        mContainer = (LinearLayout) rootView.findViewById(R.id.fragment_reminder_repeating_container);
        mDateIcon = (ImageView) rootView.findViewById(R.id.fragment_reminder_repeating_date_icon);
        mDate = (TextView) rootView.findViewById(R.id.fragment_reminder_repeating_date);
        mTime = (TextView) rootView.findViewById(R.id.fragment_reminder_repeating_time);
        mRepeat = (TextView) rootView.findViewById(R.id.fragment_reminder_repeating_repeat);
        mNext = (TextView) rootView.findViewById(R.id.fragment_reminder_repeating_next);
        mNextContainer = (LinearLayout) rootView.findViewById(R.id.fragment_reminder_repeating_next_container);

        DateFormat df = SharedPreferenceUtil.getDateFormat(getActivity());
        mDate.setText(df.formatCalendar(mReminder.getDate()));
        mTime.setText(mReminder.getTime().toString());
        mRepeat.setText(mReminder.getRepeatText(getActivity()));

        if(!TaskUtil.checkIfOverdue(mReminder)) {
            mNextContainer.setVisibility(View.VISIBLE);
            mNext.setText(df.formatCalendar(TaskUtil.getRepeatingReminderNextCalendar(mReminder)));
        }

        return rootView;
    }


}
