package rs.com.loctionbased.reminder.enums;

import java.io.Serializable;


public enum TaskViewModelType implements Serializable {
    HEADER,
    UNPROGRAMMED_REMINDER,
    ONE_TIME_REMINDER,
    REPEATING_REMINDER,
    LOCATION_BASED_REMINDER
}
