package rs.com.loctionbased.reminder.enums;

import android.content.Context;

import androidx.annotation.StringRes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.R;


public enum ReminderType implements Serializable {
    NONE(R.string.reminder_type_none),
    LOCATION_BASED(R.string.reminder_type_location_based),
    ONE_TIME(R.string.reminder_type_one_time),
    REPEATING(R.string.reminder_type_repeating);

    private @StringRes
    final
    int friendlyNameRes;

    ReminderType(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public int getFriendlyNameRes() {
        return friendlyNameRes;
    }

    public static List<String> getFriendlyValues(Context context) {
        List<String> friendlyValues = new ArrayList<>();
        for (ReminderType rt : values()) {
            friendlyValues.add(context.getResources().getString(rt.friendlyNameRes));
        }
        return friendlyValues;
    }
}
