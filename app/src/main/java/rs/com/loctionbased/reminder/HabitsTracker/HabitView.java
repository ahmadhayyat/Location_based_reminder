package rs.com.loctionbased.reminder.HabitsTracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import rs.com.loctionbased.reminder.R;

public class HabitView extends AppCompatActivity {

    private Realm realm;
    private Habit habit;
    private EditText habitName;
    private TextView habitRepeat;
    private TextView seekBarText;
    private SeekBar seekBar;
    private WheelPicker timeHour;
    private WheelPicker timeMin;
    private WheelPicker timePeriod;
    private Switch reminderSwitch;
    private View time;
    private String name;
    private String repeatDays;
    private int habitTarget;
    private List<Integer> hours;
    private List<Integer> minutes;
    private Date habitReminder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_view);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        Long id = getIntent().getLongExtra("ID", -1);
        Log.i("Habit View ID", ""+id);
        setupHabit(id);
        setupView();
    }

    private void setupView() {
        habitName = (EditText) findViewById(R.id.habitName);

        habitName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("setUpView", "habitName change!");
            }
        });
        habitRepeat = (TextView) findViewById(R.id.repeat_days);
        seekBarText = (TextView) findViewById(R.id.seekbar_text);
        seekBar = (SeekBar) findViewById(R.id.target_seekbar);
        reminderSwitch = (Switch) findViewById(R.id.reminder_switch);
        initSwitch();
        initWheelPicker();
        habitName.setText(habit.getName());
        habitRepeat.setText(habit.getRepeat());
        seekBarText.setText(Integer.toString(habit.getTarget()));
        seekBar.setProgress(habit.getTarget());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue+1;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarText.setText(""+progress+"");
            }
        });

    }

    public void updateButtonClick(View view){
        Log.i("Add Button Clicked", "Adding habit to database");
        name = "";
        if(!habitName.getText().toString().equals("")){
            name = habitName.getText().toString();
            setAttributes();
            updateHabit();
            Log.i("Add Button:", "Finishing activity.");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else {
            Toast.makeText(this, "Please enter a habit name.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHabit(){
        realm.beginTransaction();
        habit.setName(name);
        habit.setRepeat(repeatDays);
        habit.setTarget(habitTarget);
        if(reminderSwitch.isChecked()){
            //Remove all old habits reminders.
            removeReminders(habit);
            //Add new reminders;
            habit.setReminder(habitReminder);
            addReminder();
        }
        else {
            removeReminders(habit);
        }
        realm.commitTransaction();
        Toast.makeText(this, "Habit has been updated!", Toast.LENGTH_SHORT).show();

    }

    private void addReminder(){
        //Set the calendar times.
        Calendar now = Calendar.getInstance();
        long time;
        long interval;

        //If not repeating daily, set it based on the day

        if(!repeatDays.equals("Daily")){
            Log.i("addReminder", "Days selected");

            String[] days = repeatDays.split(" ");

            //for each day, create a new alarm.

            for (String day:days){
                int dayOfWeek = getRepeatDay(day);
                Log.i("DayOfWeek", ""+dayOfWeek);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, habitReminder.getHours());
                calendar.set(Calendar.MINUTE, habitReminder.getMinutes());
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                if(calendar.before(now)){
                    calendar.add(Calendar.DATE, 7);
                }
                time = calendar.getTimeInMillis();
                interval = 7*24*60*60*1000;
                createNotifications(time, interval);
            }
        }
        //if its daily
        else {
            Log.i("addReminder", "Daily selected");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, habitReminder.getHours());
            calendar.set(Calendar.MINUTE, habitReminder.getMinutes());
            if(calendar.before(now)){
                calendar.add(Calendar.DATE, 1);
            }
            time = calendar.getTimeInMillis();
            interval = 24*60*60*1000;
            createNotifications(time,interval);
        }
    }

    private void createNotifications(long time, long interval) {
        Log.i("Reminder at",""+habitReminder.getHours()+":"+habitReminder.getMinutes());
        int id;
        //Create the new alarm id object
        Number currentIdNum = realm.where(AlarmID.class).max("id");
        if(currentIdNum == null) {
            id = 1;
        } else {
            id = currentIdNum.intValue() + 1;
        }

        //Create the habit realm object
        AlarmID alarmID = realm.createObject(AlarmID.class, id);
        alarmID.setTime(time);
        alarmID.setInterval(interval);
        habit.getAlarmsList().add(alarmID);

        //Create the alarms/notifications for the user
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("name", name);
        alarmIntent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);

        Log.i("createNotifications", "Alarm manager is created.");

    }

    private int getRepeatDay(String day){
        if(day.equals("M")){
            return 1;
        }
        else if(day.equals("T")){
            return 2;
        }
        else if(day.equals("W")){
            return 3;
        }
        else if(day.equals("Th")){
            return 4;
        }
        else if(day.equals("F")){
            return 5;
        }
        else if(day.equals("S")){
            return 6;
        }
        else {
            return 7;
        }
    }

    private void removeReminders(Habit habit) {
        List<AlarmID> alarms = habit.getAlarmsList();
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        for(AlarmID alarm: alarms){
            Number id = alarm.getId();
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(this, id.intValue(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT );
            alarmManager.cancel(pIntent);
            Log.i("removeReminders", "Successfully cancelled alarm:" +id);
        }
        habit.getAlarmsList().deleteAllFromRealm();
        habit.setReminder(null);
    }

    private void setAttributes(){
        Log.i("Habit name", name);

        repeatDays = habitRepeat.getText().toString();
        Log.i("Habit repeat", repeatDays);

        habitTarget = Integer.parseInt(seekBarText.getText().toString());
        Log.i("Habit target", Integer.toString(habitTarget));

        if (reminderSwitch.isChecked()){
            int h = timeHour.getCurrentItemPosition();
            h = hours.get(h);
            int m = timeMin.getCurrentItemPosition();
            m = minutes.get(m);
            int p = timePeriod.getCurrentItemPosition();
            //if am is selected
            if(p==0 && h==12){
                h=0;
            }
            else if (p==1){
                h=h+12;
            }
            habitReminder = new Date();
            habitReminder.setHours(h);
            habitReminder.setMinutes(m);
        }
    }

    private void initSwitch(){
        time = findViewById(R.id.time_layout);
        reminderSwitch = (Switch) findViewById(R.id.reminder_switch);
        //attach a listener to check for changes in state
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    time.setVisibility(View.VISIBLE);
                }
                else{
                    time.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initWheelPicker(){
        List<String> periods = Arrays.asList("AM", "PM");
        minutes = Arrays.asList(0,5,10,15,20,25,30,35,40,45,50,55);
        hours = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);

        timeHour = (WheelPicker) findViewById(R.id.wheel_hours);
        timeMin = (WheelPicker) findViewById(R.id.wheel_minutes);
        timePeriod = (WheelPicker) findViewById(R.id.wheel_time);

        timeMin.setData(minutes);
        timeHour.setData(hours);
        timePeriod.setData(periods);

        Date reminder = habit.getReminder();
        if(reminder != null){
            reminderSwitch.setChecked(true);
            int m = reminder.getMinutes();
            int h = reminder.getHours();
            if(h>12){
                h=h-12;
                timePeriod.setSelectedItemPosition(1);
            }
            System.out.println("Min:" + m/5 + ", Hour:" + (h-1));
            timeMin.setSelectedItemPosition(m/5);
            timeHour.setSelectedItemPosition(h-1);
        }

    }

    public void repeatClick(View view){
        Intent intent = new Intent(getApplicationContext(), Repeat.class);
        startActivityForResult(intent, 1);
    }

    private void setupHabit(Long id) {
        habit = realm.where(Habit.class).equalTo("id", id).findFirst();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            String repeatDays = intent.getStringExtra("result");
            int num = intent.getIntExtra("num",0);
            seekBar.setProgress(num-1);
            seekBarText.setText(Integer.toString(seekBar.getProgress()+1));
            Log.i("Return:","Got back data " + repeatDays + " from repeat activity.");
            setRepeat(repeatDays); // update repeat
        }
    }

    private void setRepeat (String repeat) {
        habitRepeat.setText(repeat);
    }
}
