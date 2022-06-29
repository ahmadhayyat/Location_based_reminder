package rs.com.loctionbased.reminder.enums;

import android.content.Context;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.R;

public enum ReminderRepeatType {
    DAILY(R.string.reminder_repeat_type_daily),
    WEEKLY(R.string.reminder_repeat_type_weekly),
    MONTHLY(R.string.reminder_repeat_type_monthly),
    YEARLY(R.string.reminder_repeat_type_yearly);

    private @StringRes
    int friendlyNameRes;

    ReminderRepeatType(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (ReminderRepeatType rc : values()) {
            friendlyValues.add(context.getResources().getString(rc.friendlyNameRes));
        }
        return friendlyValues;
    }
}
