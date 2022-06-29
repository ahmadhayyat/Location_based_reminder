package rs.com.loctionbased.reminder.util;

import java.security.InvalidParameterException;
import java.util.Calendar;

import rs.com.loctionbased.reminder.enums.ReminderRepeatType;
import rs.com.loctionbased.reminder.enums.ReminderType;
import rs.com.loctionbased.reminder.model.reminder.OneTimeReminder;
import rs.com.loctionbased.reminder.model.reminder.Reminder;
import rs.com.loctionbased.reminder.model.reminder.RepeatingReminder;
import rs.com.loctionbased.reminder.util.sorting.CalendarPeriod;
import rs.com.loctionbased.reminder.util.sorting.CalendarPeriodType;

public class TaskUtil {


    public static boolean checkIfOverdue(Reminder reminder) {
        if (reminder == null)
            return false;

        if (reminder.getType().equals(ReminderType.LOCATION_BASED))
            return false;

        if (reminder.getType().equals(ReminderType.REPEATING)) {
            return (getRepeatingReminderNextCalendar((RepeatingReminder) reminder) == null);
        }

        CalendarPeriod overdueCP = new CalendarPeriod(CalendarPeriodType.OVERDUE);
        Calendar endDate = getReminderEndCalendar(reminder);
        return overdueCP.isInPeriod(endDate);
    }


    public static Calendar getReminderEndCalendar(Reminder reminder) {
        switch (reminder.getType()) {
            case NONE:
            case LOCATION_BASED:
                return null;

            case ONE_TIME:
                return CalendarUtil.getCalendarFromDateAndTime(((OneTimeReminder) reminder).getDate(), ((OneTimeReminder) reminder).getTime());

            case REPEATING:
                return getRepeatingReminderEndCalendar(((RepeatingReminder) reminder));

            default:
                throw new InvalidParameterException("Invalid ReminderType param on TaskUtil.getReminderEndCalendar()");
        }
    }


    public static Calendar getRepeatingReminderEndCalendar(RepeatingReminder repeatingReminder) {
        Calendar cal = Calendar.getInstance();

        switch (repeatingReminder.getRepeatEndType()) {
            case FOREVER:
                //TODO: Need to return Date.MAX or so
                cal.add(Calendar.YEAR, 100);
                break;

            case UNTIL_DATE:
                cal = CalendarUtil.getCalendarFromDateAndTime(repeatingReminder.getRepeatEndDate(), repeatingReminder.getTime());
                break;

            case FOR_X_EVENTS:
                cal = CalendarUtil.getCalendarFromDateAndTime(repeatingReminder.getDate(), repeatingReminder.getTime());
                int dateField = getDateFieldFromRepeatType(repeatingReminder.getRepeatType());

                for (int i = 0; i < repeatingReminder.getRepeatEndNumberOfEvents(); i++)
                    cal.add(dateField, repeatingReminder.getRepeatInterval());
                break;
        }
        return cal;
    }


    public static Calendar getRepeatingReminderNextCalendar(RepeatingReminder repeatingReminder) {

        Calendar today = Calendar.getInstance();
        Calendar endDate = getRepeatingReminderEndCalendar(repeatingReminder);
        Calendar cal = CalendarUtil.getCalendarFromDateAndTime(repeatingReminder.getDate(), repeatingReminder.getTime());


        while (true) {
            if (cal.compareTo(endDate) >= 0)
                return null;
            if (cal.compareTo(today) >= 0) {
                return cal;
            }
            switch (repeatingReminder.getRepeatType()) {
                case DAILY:
                    cal.add(Calendar.DAY_OF_WEEK, repeatingReminder.getRepeatInterval());
                    break;
                case WEEKLY:
                    cal.add(Calendar.WEEK_OF_YEAR, repeatingReminder.getRepeatInterval());
                    break;
                case MONTHLY:
                    cal.add(Calendar.MONTH, repeatingReminder.getRepeatInterval());
                    break;
                case YEARLY:
                    cal.add(Calendar.YEAR, repeatingReminder.getRepeatInterval());
                    break;
                default:
                    throw new InvalidParameterException("Invalid RepeatType parameter in TaskUtil.getRepeatingReminderEndCalendar()");
            }
        }
    }

    private static int getDateFieldFromRepeatType(ReminderRepeatType repeatType) {

        switch (repeatType) {
            case DAILY:
                return Calendar.DAY_OF_MONTH;
            case WEEKLY:
                return Calendar.WEEK_OF_YEAR;
            case MONTHLY:
                return Calendar.MONTH;
            case YEARLY:
                return Calendar.YEAR;
            default:
                throw new InvalidParameterException("Invalid RepeatType parameter in TaskUtil.getRepeatingReminderEndCalendar()");
        }
    }


}
