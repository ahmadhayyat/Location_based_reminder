package rs.com.loctionbased.reminder.enums;

public enum TriggerMinutesBeforeNotificationType {
    MINUTES_1(1),
    MINUTES_5(5),
    MINUTES_10(10),
    MINUTES_20(20);

    private int mMinutes;

    TriggerMinutesBeforeNotificationType(int minutes) {
        mMinutes = minutes;
    }

    public int getMinutes() {
        return mMinutes;
    }
}
