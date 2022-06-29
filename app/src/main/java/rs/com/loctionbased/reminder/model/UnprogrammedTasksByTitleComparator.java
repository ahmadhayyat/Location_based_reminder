package rs.com.loctionbased.reminder.model;

import java.util.Comparator;

public class UnprogrammedTasksByTitleComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if(o1.getTitle() == null)
            return -1;
        if(o2.getTitle() == null)
            return 1;
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
