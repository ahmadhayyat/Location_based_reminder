package rs.com.loctionbased.reminder.HabitsTracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class BootBroadcastReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        Log.i("BootBroadcastReceiver", "Received");
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Habit> habitsResults = realm.where(Habit.class).findAll();
        for (Habit habit:habitsResults){
            List<AlarmID> alarms = habit.getAlarmsList();
            for(AlarmID alarm : alarms){
                createPendingIntent(alarm.getId(), alarm.getTime(),alarm.getInterval(),habit.getName());
            }
        }
        //create default reminder
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.AM_PM, Calendar.PM);
        cal.set(Calendar.HOUR, 9);
        cal.set(Calendar.MINUTE,0);
        createPendingIntent(0, cal.getTimeInMillis(),24*60*60*1000,"");
    }

    private void createPendingIntent(Number id, long time, long interval, String name){
        //Create the alarms/notifications for the user
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("name", name);
        alarmIntent.putExtra("id", id.intValue());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id.intValue(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);

        Log.i("createPendingIntents", "Alarm:" +id+ " resubmitted successfully.");
    }
}
