package rs.com.loctionbased.reminder.enums;

import androidx.annotation.StringRes;

import java.io.Serializable;

import rs.com.loctionbased.reminder.R;

public enum ViewPagerTaskDisplayType implements Serializable {
    UNPROGRAMMED(R.string.activity_home_tab_unprogrammed),
    PROGRAMMED(R.string.activity_home_tab_programmed),
    DONE(R.string.activity_home_tab_done);

    private @StringRes
    int friendlyNameRes;

    ViewPagerTaskDisplayType(@StringRes int friendlyNameRes) {
        this.friendlyNameRes = friendlyNameRes;

    }

    public int getFriendlyNameRes() {
        return friendlyNameRes;
    }


}
