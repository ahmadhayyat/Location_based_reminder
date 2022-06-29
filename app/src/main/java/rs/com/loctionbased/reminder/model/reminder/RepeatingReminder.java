package rs.com.loctionbased.reminder.model.reminder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Calendar;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.enums.DateFormat;
import rs.com.loctionbased.reminder.enums.ReminderRepeatEndType;
import rs.com.loctionbased.reminder.enums.ReminderRepeatType;
import rs.com.loctionbased.reminder.enums.ReminderType;
import rs.com.loctionbased.reminder.model.Time;
import rs.com.loctionbased.reminder.util.SharedPreferenceUtil;

public class RepeatingReminder extends Reminder implements Serializable {

    private Calendar date;
    private Time time;
    private ReminderRepeatType repeatType;
    private int repeatInterval;
    private ReminderRepeatEndType repeatEndType;
    private int repeatEndNumberOfEvents;
    private Calendar repeatEndDate;


    public RepeatingReminder() {
    }

    public RepeatingReminder(@NonNull Calendar date, @NonNull Time time, @NonNull ReminderRepeatType repeatType, int repeatInterval,
                             @NonNull ReminderRepeatEndType repeatEndType, int repeatEndNumberOfEvents, @Nullable Calendar repeatEndDate) {
        init(date, time, repeatType, repeatInterval, repeatEndType, repeatEndNumberOfEvents, repeatEndDate);
    }

    public RepeatingReminder(int id, int taskId, @NonNull Calendar date, @NonNull Time time, @NonNull ReminderRepeatType repeatType, int repeatInterval,
                             @NonNull ReminderRepeatEndType repeatEndType, int repeatEndNumberOfEvents, @Nullable Calendar repeatEndDate) {
        super(id, taskId);
        init(date, time, repeatType, repeatInterval, repeatEndType, repeatEndNumberOfEvents, repeatEndDate);

    }

    private void init(@NonNull Calendar date, @NonNull Time time, @NonNull ReminderRepeatType repeatType, int repeatInterval,
                      @NonNull ReminderRepeatEndType repeatEndType, int repeatEndNumberOfEvents, @Nullable Calendar repeatEndDate) {
        this.date = date;
        this.time = time;
        this.repeatType = repeatType;
        this.repeatInterval = repeatInterval;
        this.repeatEndType = repeatEndType;
        this.repeatEndNumberOfEvents = repeatEndNumberOfEvents;
        this.repeatEndDate = repeatEndDate;
    }


    @Override
    public ReminderType getType() {
        return ReminderType.REPEATING;
    }


    public String getRepeatText(Context context) {
        String result = context.getResources().getString(R.string.fragment_edit_repeating_reminder_repeat_interval_every) + " ";
        switch (getRepeatType()) {
            case DAILY:
                result += getRepeatInterval() + " " + context.getResources().getString(R.string.fragment_edit_repeating_reminder_repeat_interval_days);
                break;
            case WEEKLY:
                result += getRepeatInterval() + " " + context.getResources().getString(R.string.fragment_edit_repeating_reminder_repeat_interval_weeks);
                break;
            case MONTHLY:
                result += getRepeatInterval() + " " + context.getResources().getString(R.string.fragment_edit_repeating_reminder_repeat_interval_months);
                break;
            case YEARLY:
                result += getRepeatInterval() + " " + context.getResources().getString(R.string.fragment_edit_repeating_reminder_repeat_interval_years);
                break;
        }

        result += ", ";

        switch (getRepeatEndType()) {
            case FOREVER:
                result += context.getResources().getString(R.string.reminder_repeat_end_type_forever);
                break;
            case FOR_X_EVENTS:
                result += context.getResources().getString(R.string.fragment_edit_repeating_reminder_repeat_end_for_x_events_1) + " " + getRepeatEndNumberOfEvents() + " " +
                        context.getResources().getString(R.string.fragment_edit_repeating_reminder_repeat_end_for_x_events_2);
                break;
            case UNTIL_DATE:
                DateFormat df = SharedPreferenceUtil.getDateFormat(context);
                result += context.getResources().getString(R.string.fragment_edit_repeating_reminder_repeat_end_until_date) + " " + df.formatCalendar(getRepeatEndDate());
                break;
        }

        return result;
    }


    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public ReminderRepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(ReminderRepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public ReminderRepeatEndType getRepeatEndType() {
        return repeatEndType;
    }

    public void setRepeatEndType(ReminderRepeatEndType repeatEndType) {
        this.repeatEndType = repeatEndType;
    }

    public int getRepeatEndNumberOfEvents() {
        return repeatEndNumberOfEvents;
    }

    public void setRepeatEndNumberOfEvents(int repeatEndNumberOfEvents) {
        this.repeatEndNumberOfEvents = repeatEndNumberOfEvents;
    }

    public Calendar getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(Calendar repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }


    @Override
    public String toString() {
        String res = "Reminder ID=" + getId() + "\r\n";
        res += " Type=" + getType().name() + "\r\n";
        res += " TaskID=" + getTaskId() + "\r\n";
        res += " Date=" + date.toString() + "\r\n";
        res += " Time=" + time.toString() + "\r\n";
        res += " RepeatType=" + repeatType.toString() + "\r\n";
        res += " RepeatInterval=" + repeatInterval + "\r\n";
        res += " RepeatEndType=" + repeatEndType.toString() + "\r\n";
        res += " RepeatEndNumberOfEvents=" + repeatEndNumberOfEvents + "\r\n";
        if (repeatEndDate != null) res += " RepeatEndDate=" + repeatEndDate.toString();
        return res;
    }
}
