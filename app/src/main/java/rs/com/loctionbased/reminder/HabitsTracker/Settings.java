package rs.com.loctionbased.reminder.HabitsTracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import rs.com.loctionbased.reminder.R;

public class Settings extends AppCompatActivity {
    private Switch rSwitch;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_two);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        setLayout();
    }

    private void setLayout() {
        rSwitch = (Switch) findViewById(R.id.reminderToggle);
        boolean rem = sharedPref.getBoolean("reminders", true);
        final SharedPreferences.Editor edit = sharedPref.edit();
        rSwitch.setChecked(rem);

        rSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Daily reminders are on.", Toast.LENGTH_SHORT).show();
                    setDefaultHabitReminder(true);
                    edit.putBoolean("reminders", true);
                    edit.apply();
                } else {
                    Toast.makeText(getApplicationContext(), "Daily reminders are off.", Toast.LENGTH_SHORT).show();
                    setDefaultHabitReminder(false);
                    edit.putBoolean("reminders", false);
                    edit.apply();
                }
            }
        });

    }

    private void setDefaultHabitReminder(boolean flag) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("id", 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (flag) {
            Calendar cal = Calendar.getInstance();
            //set a reminder every day at 9.
            cal.set(Calendar.HOUR, 9);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.AM_PM, Calendar.PM);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            Log.i("setDefaultHabitReminder", "Reminder set.");

        } else {
            alarmManager.cancel(pendingIntent);
            Log.i("setDefaultHabitReminder", "Reminder cancelled.");

        }

    }
}