package rs.com.loctionbased.reminder.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import rs.com.loctionbased.reminder.app.Constants;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import java.util.List;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.activities.HomeActivity;
import rs.com.loctionbased.reminder.app.activities.TaskDetailActivity;
import rs.com.loctionbased.reminder.app.services.TaskActionsIntentService;
import rs.com.loctionbased.reminder.model.Task;

import static android.content.Context.NOTIFICATION_SERVICE;
import static rs.com.loctionbased.reminder.app.activities.TaskDetailActivity.TASK_ID_TO_DISPLAY;


public class NotificationUtil {

    public static void displayNotification(Context context, Task task, String contentTitle, String contentText) {

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "NOTIFICATION_CHANNEL_NAME";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            createNotificationChannel(notificationManager, Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, Constants.NOTIFICATION_CHANNEL_DESCRIPTION);
        }

        Intent setTaskDoneIntent = new Intent(context, TaskActionsIntentService.class);
        setTaskDoneIntent.setAction(TaskActionsIntentService.ACTION_SET_TASK_DONE);
        setTaskDoneIntent.putExtra(TaskActionsIntentService.PARAM_TASK_ID, task.getId());
        PendingIntent setTaskDonePendingIntent = PendingIntent.getService(context, task.getId(), setTaskDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent postponeTaskIntent = new Intent(context, TaskActionsIntentService.class);
        postponeTaskIntent.setAction(TaskActionsIntentService.ACTION_POSTPONE_TASK);
        postponeTaskIntent.putExtra(TaskActionsIntentService.PARAM_TASK_ID, task.getId());
        PendingIntent postponeTaskPendingIntent = PendingIntent.getService(context, task.getId(), postponeTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent openTaskIntent = new Intent(context, TaskDetailActivity.class);
        openTaskIntent.putExtra(TASK_ID_TO_DISPLAY, task.getId());


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(TaskDetailActivity.class);
        stackBuilder.addNextIntent(openTaskIntent);
        PendingIntent openTaskPendingIntent = stackBuilder.getPendingIntent(task.getId(), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_remindy_notification_small)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setVibrate(new long[] { 50, 50, 200, 50 })
                .setLights(ContextCompat.getColor(context, R.color.primary), 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setChannelId(CHANNEL_ID)

                .setContentIntent(openTaskPendingIntent)
                .setAutoCancel(true)

                .setPriority(Notification.PRIORITY_HIGH)

                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .addAction(R.drawable.icon_notification_done, context.getResources().getString(R.string.notification_big_view_set_done), setTaskDonePendingIntent)
                .addAction(R.drawable.icon_notification_postpone, context.getResources().getString(R.string.notification_big_view_postpone), postponeTaskPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.createNotificationChannel(mChannel);

        mNotifyMgr.notify(task.getId(), mBuilder.build());
    }


    public static void displayLocationBasedNotification(Context context, int notificationId, String contentTitle, String contentText,
                                                        List<Task> triggeredTasks) {

        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "NOTIFICATION_CHANNEL_NAME";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            createNotificationChannel(notificationManager, Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, Constants.NOTIFICATION_CHANNEL_DESCRIPTION);
        }

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_remindy_notification_small)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setVibrate(new long[] { 50, 50, 200, 50 })
                .setLights(ContextCompat.getColor(context, R.color.primary), 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentTitle(contentTitle)
                .setChannelId(CHANNEL_ID)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        if(triggeredTasks.size() == 1) {

            Intent setTaskDoneIntent = new Intent(context, TaskActionsIntentService.class);
            setTaskDoneIntent.setAction(TaskActionsIntentService.ACTION_SET_TASK_DONE);
            setTaskDoneIntent.putExtra(TaskActionsIntentService.PARAM_TASK_ID, triggeredTasks.get(0).getId());
            PendingIntent setTaskDonePendingIntent = PendingIntent.getService(context, triggeredTasks.get(0).getId(), setTaskDoneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent openTaskIntent = new Intent(context, TaskDetailActivity.class);
            openTaskIntent.putExtra(TASK_ID_TO_DISPLAY, triggeredTasks.get(0).getId());


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            stackBuilder.addParentStack(TaskDetailActivity.class);
            stackBuilder.addNextIntent(openTaskIntent);
            PendingIntent openTaskPendingIntent = stackBuilder.getPendingIntent(triggeredTasks.get(0).getId(), PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(openTaskPendingIntent);
            mBuilder.addAction(R.drawable.icon_notification_done, context.getResources().getString(R.string.notification_big_view_set_done), setTaskDonePendingIntent);

            //Set BigView
            mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(contentText));

        } else {

            Intent openHomeIntent = new Intent(context, HomeActivity.class);
            PendingIntent openHomePendingItent = PendingIntent.getActivity(context, Integer.MAX_VALUE, openHomeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(contentTitle);
            for (Task task: triggeredTasks)
                inboxStyle.addLine("- " + task.getTitle());

            mBuilder.setContentIntent(openHomePendingItent);

            mBuilder.setStyle(inboxStyle);
        }

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.createNotificationChannel(mChannel);
        mNotifyMgr.notify(triggeredTasks.get(0).getId(), mBuilder.build());
    }

    private static void createNotificationChannel(NotificationManager notificationManager, @NonNull String channelId, String channelName, String channelDescription) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(channel);
        }
    }
}