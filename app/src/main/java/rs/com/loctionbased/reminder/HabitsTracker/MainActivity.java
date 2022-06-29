package rs.com.loctionbased.reminder.HabitsTracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rs.com.loctionbased.reminder.R;

public class MainActivity extends AppCompatActivity {

    private RealmResults<Habit> habitsResults;
    private Realm realm;
    HabitAdapter adapter;
    private AdView mAdView;
    private MultiStateToggleButton filterType;
    private TextView indicate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_habit);
        // No ads enabled.
        //initAds();
        setDefaultHabitReminder();
    }

    //Used to initialize ads
    private void initAds(){
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
//            .addTestDevice("745550807238A6B8EF5232342F3B9DFD")
                .build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }


    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        AdRequest adRequest = new AdRequest.Builder().build();
        if (mAdView != null) {
            mAdView.loadAd(adRequest);
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void setDefaultHabitReminder() {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("id", 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        //set a reminder every day at 9.
        cal.set(Calendar.HOUR,9);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.AM_PM, Calendar.PM);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.i("setDefaultHabitReminder", "Reminder set.");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        indicate = (TextView) findViewById(R.id.indicate);
        initFilter();
        //getHabits(filterType.getValue());
        resetTracker();
        initListView();
    }

    private void initFilter() {
        filterType = (MultiStateToggleButton) findViewById(R.id.filter);
        final SharedPreferences pref = getPreferences(MODE_PRIVATE);
        int val = pref.getInt("filter", 1);
        filterType.setValue(val); //Set default button to assignment.
        filterType.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                Log.i("onChanged", "Value changed!");
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("filter", value);
                edit.apply();
                //getHabits(value);
                habitsResults.removeAllChangeListeners();
                initListView();
            }
        });
    }

    private void initListView() {
        adapter = new HabitAdapter(this,habitsResults);
        ListView mListView = (ListView) findViewById(R.id.habits_list);
        mListView.setAdapter(adapter);
        RealmChangeListener changeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                adapter.notifyDataSetChanged();
            }
        };
        habitsResults.addChangeListener(changeListener);
    }

    public void addHabit(View view){
        Log.i("Button Clicked","Bringing to next Page");
        Intent intent = new Intent(this, AddHabit.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            Log.i("Return to Main Habit:","Got back data from AddHabit activity.");
        }
    }

    private void getHabits(int val){
        if(val==0){
            Log.i("Get Habits", "Getting habits for Today");
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            String today = dayToString(day);
            //habitsResults = realm.where(Habit.class).contains("repeat", today).or().contains("repeat", "Daily").findAllSorted("name");
            if (habitsResults.size() == 0){
                indicate.setVisibility(View.VISIBLE);
            }
            else{
                indicate.setVisibility(View.GONE);
            }
        }
        else {
            Log.i("Get Habits", "Getting habits for All");
           // habitsResults = realm.where(Habit.class).findAllSorted("name");
            if (habitsResults.size() == 0){
                indicate.setVisibility(View.VISIBLE);
            }
            else{
                indicate.setVisibility(View.GONE);
            }
        }
        Log.i("Get Habits", "Got all habits");
    }

    private String dayToString (int day){
        String res ="";
        switch (day) {
            case Calendar.SUNDAY:
                res = "Su";
                break;
            case Calendar.MONDAY:
                res = "M";
                break;
            case Calendar.TUESDAY:
                res = "T";
                break;
            case Calendar.WEDNESDAY:
                res = "W";
                break;
            case Calendar.THURSDAY:
                res = "Th";
                break;
            case Calendar.FRIDAY:
                res = "F";
                break;
            case Calendar.SATURDAY:
                res = "S";
                break;
        }
        return res;
    }

    private void resetTracker(){
        Calendar cal = Calendar.getInstance();
        int currentWeekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        int weekOfYear = sharedPreferences.getInt("weekOfYear", 0);

        if(weekOfYear != currentWeekOfYear){ //different week
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("weekOfYear", currentWeekOfYear);
            editor.apply();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (Habit habit : habitsResults) {
                            habit.setTracker(0);
                            if(habit.isWeeklyCompletion()){
                                Log.i("Completion", ""+habit.getName()+" was completed last week.");
                                habit.setWeeklyCompletion(false);
                            }
                            else {
                                habit.setCompletion(0);
                            }
                        }
                    }
                });
        }
    }

    public void settingsClick(View view){
        Log.i("Settings Clicked","Bringing to next Page");
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
