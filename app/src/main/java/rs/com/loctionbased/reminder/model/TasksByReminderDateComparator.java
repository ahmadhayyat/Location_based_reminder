package rs.com.loctionbased.reminder.model;

import java.util.Calendar;
import java.util.Comparator;

import rs.com.loctionbased.reminder.enums.ReminderType;
import rs.com.loctionbased.reminder.model.reminder.OneTimeReminder;
import rs.com.loctionbased.reminder.model.reminder.RepeatingReminder;
import rs.com.loctionbased.reminder.util.TaskUtil;

public class TasksByReminderDateComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if(o1.getReminder() == null || (o1.getReminderType() != ReminderType.ONE_TIME && o1.getReminderType() != ReminderType.REPEATING))
            return 1;
        if(o2.getReminder() == null || (o2.getReminderType() != ReminderType.ONE_TIME && o2.getReminderType() != ReminderType.REPEATING))
            return -1;


        Calendar o1Date = null;
        if(o1.getReminderType() == ReminderType.ONE_TIME)
            o1Date = ((OneTimeReminder) o1.getReminder()).getDate();
        else {
            Calendar cal = TaskUtil.getRepeatingReminderNextCalendar(((RepeatingReminder) o1.getReminder()));
            o1Date = (cal != null ? cal : TaskUtil.getRepeatingReminderEndCalendar(((RepeatingReminder) o1.getReminder())));
        }

        Calendar o2Date = null;
        if(o2.getReminderType() == ReminderType.ONE_TIME)
            o2Date = ((OneTimeReminder) o2.getReminder()).getDate();
        else {
            Calendar cal = TaskUtil.getRepeatingReminderNextCalendar(((RepeatingReminder) o2.getReminder()));
            o2Date = (cal != null ? cal : TaskUtil.getRepeatingReminderEndCalendar(((RepeatingReminder) o2.getReminder())));
        }

        return o1Date.compareTo(o2Date);
    }
}
