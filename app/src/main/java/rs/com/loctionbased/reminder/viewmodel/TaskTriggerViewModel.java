package rs.com.loctionbased.reminder.viewmodel;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

import rs.com.loctionbased.reminder.model.Task;
import rs.com.loctionbased.reminder.model.Time;
import rs.com.loctionbased.reminder.util.CalendarUtil;

public class TaskTriggerViewModel implements Serializable {

    //DATA
    private Task task = null;
    private Calendar triggerDate = null;
    private Time triggerTime = null;

    public TaskTriggerViewModel(@NonNull Task task, @NonNull Calendar triggerDate, @NonNull Time triggerTime) {
        this.task = task;
        this.triggerDate = triggerDate;
        this.triggerTime = triggerTime;
    }

    public Task getTask() { return task; }
    public Calendar getTriggerDate() { return triggerDate; }
    public Time getTriggerTime() { return triggerTime; }

    public Calendar getTriggerDateWithTime() {
        Calendar cal = CalendarUtil.getNewInstanceZeroedCalendar();
        cal.setTimeInMillis(getTriggerDate().getTimeInMillis());

        cal.set(Calendar.HOUR_OF_DAY, getTriggerTime().getHour());
        cal.set(Calendar.MINUTE, getTriggerTime().getMinute());

        return cal;
    }
}
