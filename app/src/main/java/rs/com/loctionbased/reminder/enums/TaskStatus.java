package rs.com.loctionbased.reminder.enums;

import androidx.annotation.StringRes;

import java.io.Serializable;

import rs.com.loctionbased.reminder.R;

public enum TaskStatus implements Serializable {
    UNPROGRAMMED(R.string.task_status_unprogrammed),
    PROGRAMMED(R.string.task_status_programmed),
    DONE(R.string.task_status_done);

    private @StringRes
    int friendlyNameRes;

    TaskStatus(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

}
