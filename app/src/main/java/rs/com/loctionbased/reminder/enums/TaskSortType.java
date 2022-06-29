package rs.com.loctionbased.reminder.enums;

import androidx.annotation.StringRes;

import rs.com.loctionbased.reminder.R;

public enum TaskSortType {
    DATE(R.string.task_sort_type_date, R.string.task_sort_type_date_message),
    PLACE(R.string.task_sort_type_location, R.string.task_sort_type_location_message);

    private @StringRes
    int friendlyNameRes;
    int friendlyMessageRes;

    TaskSortType(@StringRes int friendlyNameRes, @StringRes int friendlyMessageRes) {
        this.friendlyNameRes = friendlyNameRes;
        this.friendlyMessageRes = friendlyMessageRes;

    }

    public int getFriendlyMessageRes() {
        return friendlyMessageRes;
    }

}
