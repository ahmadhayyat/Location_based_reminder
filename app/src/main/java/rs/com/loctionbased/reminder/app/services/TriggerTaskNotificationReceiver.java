package rs.com.loctionbased.reminder.app.services;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.database.RemindyDAO;
import rs.com.loctionbased.reminder.exception.CouldNotGetDataException;
import rs.com.loctionbased.reminder.model.Task;
import rs.com.loctionbased.reminder.model.reminder.OneTimeReminder;
import rs.com.loctionbased.reminder.model.reminder.RepeatingReminder;
import rs.com.loctionbased.reminder.util.AlarmManagerUtil;
import rs.com.loctionbased.reminder.util.NotificationUtil;
import rs.com.loctionbased.reminder.util.SharedPreferenceUtil;

public class TriggerTaskNotificationReceiver extends BroadcastReceiver {

    //CONSTS
    private static final String TAG = TriggerTaskNotificationReceiver.class.getSimpleName();
    public static final String TASK_ID_EXTRA = "TASK_ID_EXTRA";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "TriggerTaskNotificationReceiver");

        //Get TASK_ID_EXTRA
        int taskId;
        try {
            taskId = intent.getIntExtra(TASK_ID_EXTRA, -1);
        } catch (Exception e) {
            taskId = -1;
        }

        if(taskId != -1) {
            Task task;
            try {
                task = new RemindyDAO(context).getTask(taskId);
            } catch (CouldNotGetDataException e) {
               //TODO: Show some kind of error here
                return;
            }

            if(task != null) {
                Log.d(TAG, "Triggering task ID " + taskId);

                String triggerTime;
                switch (task.getReminderType()) {
                    case ONE_TIME:
                        triggerTime = ((OneTimeReminder)task.getReminder()).getTime().toString();
                        break;
                    case REPEATING:
                        triggerTime = ((RepeatingReminder)task.getReminder()).getTime().toString();
                        break;
                    default:
                        //TODO: Show some kind of error here
                        return;
                }

                String contentTitle = String.format(Locale.getDefault(), context.getResources().getString(R.string.notification_service_normal_title), task.getTitle());
                int triggerMinutesBeforeNotification = SharedPreferenceUtil.getTriggerMinutesBeforeNotification(context).getMinutes();
                @SuppressLint("StringFormatMatches") String contentText = String.format(Locale.getDefault(), context.getResources().getString(R.string.notification_service_normal_text), triggerMinutesBeforeNotification, triggerTime);

                NotificationUtil.displayNotification(context, task, contentTitle, contentText);

                List<Integer> triggeredTasks = SharedPreferenceUtil.getTriggeredTaskList(context);
                triggeredTasks.add(task.getId());
                SharedPreferenceUtil.setTriggeredTaskList(triggeredTasks, context);
            }



        } else {
            Log.d(TAG, "TriggerTaskNotificationReceiver triggered with no TASK_ID_EXTRA!");
        }

        AlarmManagerUtil.updateAlarms(context);
    }
}
