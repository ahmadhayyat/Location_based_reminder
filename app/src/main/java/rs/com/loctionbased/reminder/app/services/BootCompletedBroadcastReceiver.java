package rs.com.loctionbased.reminder.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startNotificationServiceIntent = new Intent(context, NotificationIntentService.class);
        context.startService(startNotificationServiceIntent);
    }
}
