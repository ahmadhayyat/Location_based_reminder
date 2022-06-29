package rs.com.loctionbased.reminder.HabitsTracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import rs.com.loctionbased.reminder.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "Received!");
        String name = intent.getStringExtra("name");
        int req = intent.getIntExtra("id",0);
        String title = name + " Reminder!";
        String message = "Your reminder to keep up your habit!";
        if(req == 0){
            Log.i("AlarmOnReceive", "Daily Review alarm");
            title = "Daily Habit Review";
            message = "Lets take a moment to review your habits for today.";
        }
        long when = System.currentTimeMillis();
        Log.i("Triggered at:", ""+when);
        Log.i("Id of habit reminder:", ""+req);
        Intent in = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0,in, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context)
                .setContentIntent(contentIntent)
                .setWhen(when)
                .setShowWhen(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_action_name)
                .setAutoCancel(true)
                .build();
        nM.notify(req,notification);
    }
}
