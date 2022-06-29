package rs.com.loctionbased.reminder.util;

import android.content.res.Resources;

import com.google.android.gms.maps.model.LatLng;

import java.security.InvalidParameterException;

import rs.com.loctionbased.reminder.enums.ReminderType;
import rs.com.loctionbased.reminder.enums.TaskViewModelType;
import rs.com.loctionbased.reminder.model.Place;

public class ConversionUtil {
    public static int dpToPx(int dp, Resources resources) {
        final float scale = resources.getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }

    public static LatLng placeToLatLng(Place place) {
        return new LatLng(place.getLatitude(), place.getLongitude());
    }


    public static TaskViewModelType taskReminderTypeToTaskViewmodelType(ReminderType reminderType) {
        switch (reminderType) {
            case NONE:
                return TaskViewModelType.UNPROGRAMMED_REMINDER;
            case ONE_TIME:
                return TaskViewModelType.ONE_TIME_REMINDER;
            case REPEATING:
                return TaskViewModelType.REPEATING_REMINDER;
            case LOCATION_BASED:
                return TaskViewModelType.LOCATION_BASED_REMINDER;
            default:
                throw new InvalidParameterException("Unhandled ReminderType passed into ConversionUtil.taskReminderTypeToTaskViewmodelType()");
        }
    }

}