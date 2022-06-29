package rs.com.loctionbased.reminder.enums;

import android.content.Context;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.R;

public enum ReminderRepeatEndType {
    FOREVER(R.string.reminder_repeat_end_type_forever),
    UNTIL_DATE(R.string.reminder_repeat_end_type_until_date),
    FOR_X_EVENTS(R.string.reminder_repeat_end_type_for_x_events);

    private @StringRes
    int friendlyNameRes;

    ReminderRepeatEndType(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (ReminderRepeatEndType rc : values()) {
            friendlyValues.add(context.getResources().getString(rc.friendlyNameRes));
        }
        return friendlyValues;
    }
}
