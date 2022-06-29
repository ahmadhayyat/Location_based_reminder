package rs.com.loctionbased.reminder.model.reminder;

import java.io.Serializable;

import rs.com.loctionbased.reminder.enums.ReminderType;


public abstract class Reminder implements Serializable {

    private int id;
    private int taskId;

    public Reminder(){
        this.id = -1;
        this.taskId = -1;
    }

    public Reminder(int id, int taskId) {
        this.id = id;
        this.taskId = taskId;
    }

    public abstract ReminderType getType();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
