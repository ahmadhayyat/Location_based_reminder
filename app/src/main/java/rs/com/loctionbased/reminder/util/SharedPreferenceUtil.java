package rs.com.loctionbased.reminder.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.enums.DateFormat;
import rs.com.loctionbased.reminder.enums.TimeFormat;
import rs.com.loctionbased.reminder.enums.TriggerMinutesBeforeNotificationType;
import rs.com.loctionbased.reminder.enums.TapTargetSequenceType;

public class SharedPreferenceUtil {

    public static boolean doShowTapTargetSequenceFor(Context context, TapTargetSequenceType tapTargetSequenceType) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean flag = preferences.getBoolean(tapTargetSequenceType.name(), true);

        if(flag)
            preferences.edit().putBoolean(tapTargetSequenceType.name(), false).apply();

        return flag;
    }

    public static DateFormat getDateFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dfPref = preferences.getString(context.getResources().getString(R.string.settings_date_format_key), null);
        DateFormat pref;
        try {
            pref = DateFormat.valueOf(dfPref);
        } catch (Exception e) {
            pref = null;
        }

        if(pref == null) {
            Log.d("SharedPreferenceUtil", "getDateFormat() found null, setting PRETTY_DATE");
            DateFormat df = DateFormat.PRETTY_DATE;
            setDateFormat(df, context);
            return df;
        }
        else return pref;
    }

    public static void setDateFormat(DateFormat df, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_date_format_key), df.name());
        editor.apply();
    }

    public static TimeFormat getTimeFormat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tfPref = preferences.getString(context.getResources().getString(R.string.settings_time_format_key), null);
        TimeFormat pref;
        try {
            pref = TimeFormat.valueOf(tfPref);
        } catch (Exception e) {
            pref = null;
        }

        if(pref == null) {
            Log.d("SharedPreferenceUtil", "getTimeFormat() found null, setting FORMAT_24H");
            TimeFormat tf = TimeFormat.FORMAT_24H;
            setTimeFormat(tf, context);
            return tf;
        }
        else return pref;
    }

    public static void setTimeFormat(TimeFormat tf, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_time_format_key), tf.name());
        editor.apply();
    }


    public static List<Integer> getTriggeredTaskList(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString(context.getResources().getString(R.string.settings_triggered_task_list_key), null);
        List<Integer> tasks = new ArrayList<>();

        if(json != null) {
            try {
                Type listType = new TypeToken<List<Integer>>() {}.getType();
                Gson gson = new Gson();
                tasks = gson.fromJson(json, listType);
            } catch (Exception e) { /* Do nothing */}
        }

        return tasks;
    }

    public static void setTriggeredTaskList(List<Integer> tasks, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_triggered_task_list_key), new Gson().toJson(tasks));
        editor.apply();
    }


    public static void removeIdFromTriggeredTasks(Context context, int taskId) {
        List<Integer> triggeredTasks = getTriggeredTaskList(context);
        Iterator<Integer> iter = triggeredTasks.iterator();
        while(iter.hasNext()) {
            if (iter.next().equals(taskId)) {
                iter.remove();
            }
        }

        setTriggeredTaskList(triggeredTasks, context);
    }

    public static TriggerMinutesBeforeNotificationType getTriggerMinutesBeforeNotification(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tmPref = preferences.getString(context.getResources().getString(R.string.settings_trigger_minutes_before_notification_key), null);
        TriggerMinutesBeforeNotificationType pref;
        try {
            pref = TriggerMinutesBeforeNotificationType.valueOf(tmPref);
        } catch (Exception e) {
            pref = null;
        }

        if(pref == null) {
            Log.d("SharedPreferenceUtil", "getTriggerMinutesBeforeNotification() found null, setting 5 minutes");
            TriggerMinutesBeforeNotificationType tm = TriggerMinutesBeforeNotificationType.MINUTES_5;
            setTriggerMinutesBeforeNotification(tm, context);
            return tm;
        }
        else return pref;
    }

    public static void setTriggerMinutesBeforeNotification(TriggerMinutesBeforeNotificationType triggerMinutesBeforeNotification, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getResources().getString(R.string.settings_trigger_minutes_before_notification_key), triggerMinutesBeforeNotification.name());
        editor.apply();
    }

}
