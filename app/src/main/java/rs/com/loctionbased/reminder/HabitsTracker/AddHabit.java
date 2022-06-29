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


public class AddHabit extends AppCompatActivity {

    private Switch mySwitch;
    private View time;
    private SeekBar weeklyTarget;
    private TextView targetText;
    private String repeatDays;
    private String habitName;
    private int habitTarget;
    private Date habitReminder;
    private List<Integer> hours;
    private List<Integer> minutes;
    private WheelPicker timeHour;
    private WheelPicker timeMin;
    private WheelPicker timePeriod;
    private Realm realm;
    private Habit habit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        repeatDays="Daily";
        setRepeat(repeatDays); //Default repeat is daily
        initSwitch();
        initSeekBar();
        initWheelPicker();
    }

    private void setRepeat (String repeat) {
        TextView targetDays = (TextView) findViewById(R.id.repeat_holder);
        targetDays.setText(repeat);
    }

    private void initSwitch(){
        time = findViewById(R.id.time_layout);
        mySwitch = (Switch) findViewById(R.id.reminder_switch);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
    }

    private void initSeekBar(){
        // Initialize the textview with 7 to correspond with Daily.
        targetText = (TextView) findViewById(R.id.seekbar_text);
        weeklyTarget = (SeekBar) findViewById(R.id.target_seekbar);
        weeklyTarget.setProgress(7);
        targetText.setText(Integer.toString(weeklyTarget.getProgress()+1));

        weeklyTarget.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                targetText.setText(""+progress+"");
            }
        });
    }

    public void add(View view){
        Log.i("Add Button Clicked", "Adding habit to database");
        setAttributes();
        createNewHabit();
        Log.i("Add Button:", "Finishing activity.");
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void createNewHabit() {
        Log.i("Creating New Habit", "Adding habit attributes");
        realm.beginTransaction();
        // increment index
        Number currentIdNum = realm.where(Habit.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        //Create the habit realm object
        habit = realm.createObject(Habit.class, nextId);

        //Setup the habit date times
        habit.setCreated(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);

        //Set the last updated date one day before creation to allow for updating on creation
        Date dateBeforeCreated = cal.getTime();
        habit.setUpdated(dateBeforeCreated);

        //Set habit attributes
        habit.setName(habitName);
        habit.setRepeat(repeatDays);
        habit.setTarget(habitTarget);
        if(habitReminder!=null){
            habit.setReminder(habitReminder);
            addReminder();
        }
        habit.setCompletion(0);
        realm.commitTransaction();

        Toast.makeText(this, "Habit Added!", Toast.LENGTH_SHORT).show();
        Log.i("Creating New Habit", "Added habit finished");
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
        alarmIntent.putExtra("name", habitName);
        alarmIntent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);

        Log.i("createNotifications", "Alarm manager is created.");

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

    private void setAttributes(){
        EditText nameHeader = (EditText) findViewById(R.id.habitName);
        habitName = nameHeader.getText().toString();
        Log.i("Habit name", habitName);
        Log.i("Habit repeat", repeatDays);
        habitTarget = Integer.parseInt(targetText.getText().toString());
        Log.i("Habit target", Integer.toString(habitTarget));

        if (mySwitch.isChecked()){
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



    public void repeatClick(View view){
        Intent intent = new Intent(this, Repeat.class);
        startActivityForResult(intent,1);
        Log.i("Repeat", "Bringing to next page");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            repeatDays = intent.getStringExtra("result");
            int num = intent.getIntExtra("num",0);
            weeklyTarget.setProgress(num-1);
            targetText.setText(Integer.toString(weeklyTarget.getProgress()+1));
            Log.i("Return:","Got back data " + repeatDays + " from repeat activity.");
            setRepeat(repeatDays); // update repeat
        }
    }
}
