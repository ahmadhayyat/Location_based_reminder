package rs.com.loctionbased.reminder.model;

import java.util.Comparator;

public class TasksByDoneDateComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if(o1.getDoneDate() == null)
            return 1;
        if(o2.getDoneDate() == null )
            return -1;

        return o1.getDoneDate().compareTo(o2.getDoneDate());
    }
}
