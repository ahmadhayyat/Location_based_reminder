package rs.com.loctionbased.reminder.HabitsTracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rs.com.loctionbased.reminder.R;

public class HabitAdapter extends BaseSwipeAdapter{

    private Context mContext;
    private RealmResults<Habit> mDataSource;
    private Realm realm;
    private SwipeLayout swipeLayout;
    private MediaPlayer mp;

    public HabitAdapter(Context context, RealmResults<Habit> items) {
        mContext = context;
        mDataSource = items;
        Realm.init(mContext);
        realm = Realm.getDefaultInstance();
        mp = MediaPlayer.create(mContext, R.raw.click);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Habit getItem(int i) {
        return mDataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mDataSource.get(i).getId();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, final ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_habit, null);
        swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.delete));
            }
        });
        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAllItems();
                deleteHabit(position);
            }
        });
        return v;
    }

    private void deleteHabit(int position) {
        //To-DO: Create a delete alert.
        realm.beginTransaction();
        Habit habit = mDataSource.get(position);
        removeReminders(habit);
        Log.i("DeleteHabit:", "Deleting " + habit.getName());
        habit.deleteFromRealm();
        Log.i("DeleteHabit:", "Deleted habit");
        realm.commitTransaction();
        Toast.makeText(mContext, "Habit Deleted.", Toast.LENGTH_SHORT).show();
    }

    private void removeReminders(Habit habit) {
        List<AlarmID> alarms = habit.getAlarmsList();
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        for(AlarmID alarm: alarms){
            Number id = alarm.getId();
            Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(mContext, id.intValue(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT );
            alarmManager.cancel(pIntent);
            Log.i("removeReminders", "Successfully cancelled alarm:" +id);
        }
        habit.getAlarmsList().deleteAllFromRealm();
    }


    @Override
    public void fillValues(final int position, View convertView) {

        ViewHolder holder;

        // check if the view already exists if so, no need to inflate and findViewById again!
        if (convertView.getTag()==null) {
            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.habitName= (TextView) convertView.findViewById(R.id.habitlist_name);
            holder.habitTracker = (Button) convertView.findViewById(R.id.habitlist_tracker);
            holder.habitChecked = (ImageButton) convertView.findViewById(R.id.habitlist_checked);
            holder.completionView = (RelativeLayout) convertView.findViewById(R.id.completionView);
            holder.completion = (TextView) convertView.findViewById(R.id.completion);
            holder.habitClicker = (RelativeLayout) convertView.findViewById(R.id.habit_clicker);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        }
        else {
            // skip all the expensive inflation/findViewById and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        // Get relevant subviews of row view
        TextView habitName = holder.habitName;
        Button habitTracker = holder.habitTracker;
        final ImageButton habitChecked = holder.habitChecked;
        RelativeLayout completionView = holder.completionView;
        TextView habitCompletion = holder.completion;
        RelativeLayout habitClicker = holder.habitClicker;


        //Get corresponding habit for row
        Habit habit = getItem(position);

        // Update row view's textviews to display habit information
        habitName.setText(habit.getName());

        setColor(completionView, habit.getCompletion());

        //Set habit tracker to show either tracker/target or just target
        String target = Integer.toString(habit.getTarget());
        String tracker = Integer.toString(habit.getTracker());
        String completionText = Integer.toString(habit.getCompletion());
        habitTracker.setText(tracker+"/"+target);
        habitCompletion.setText(completionText);

        //if habit has been updated today, set color to green, else set to grey
        realm.beginTransaction();
        if (isSameDay(position)){
            habitChecked.setBackgroundResource(R.drawable.green);
            habit.setChange(1);
        }
        else {
            habitChecked.setBackgroundResource(R.drawable.grey);
            habit.setChange(0);
        }
        realm.commitTransaction();

        //Set habitChecked button listen and link to updateHabit
        habitChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("In on Click:", ""+position+"");
                updateHabit(position, habitChecked);
            }
        });

        habitClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("In on Click:", ""+position+"");
                openHabitView(mDataSource.get(position).getId());
            }
        });
    }

    private void openHabitView(long id) {
        Intent i = new Intent(mContext, HabitView.class);
        i.putExtra("ID", id);
        mContext.startActivity(i);
    }

    private void setColor(RelativeLayout completionView, int val) {
        if(val == 0){
            completionView.setBackgroundColor(mContext.getResources().getColor(R.color.washed));
        }
        else if (val == 1){
            completionView.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
        }
        else if (val == 2){
            completionView.setBackgroundColor(mContext.getResources().getColor(R.color.purple));
        }
        else if (val >= 3){
            completionView.setBackgroundColor(mContext.getResources().getColor(R.color.gold));

        }
    }

    private void updateHabit(int position, ImageButton habitChecked){
        Habit habit = mDataSource.get(position);
        realm.beginTransaction();
        if(habit.getChange() == 0){ //If the habit hasn't already been updated today
            habit.setTracker(habit.getTracker()+1);
            mp.start();
            habit.setLastUpdated(habit.getUpdated()); //set the last update to the updated time
            //update completion if weekly target is hit.
            if(checkCompletion(habit)){
                habit.setCompletion(habit.getCompletion()+1);
                habit.setWeeklyCompletion(true);

            }
            habit.setUpdated(new Date()); //update updated date with current time
            habit.setChange(1); //Set the habit to changed;
        }
        else{
            //check if the target was hit, and decrement it.
            if(checkCompletion(habit)){
                habit.setCompletion(habit.getCompletion()-1);
                habit.setWeeklyCompletion(false);
            }
            habit.setTracker(habit.getTracker()-1);
            habit.setUpdated(habit.getLastUpdated()); //set habit back to the last updated time.
            habit.setChange(0); //Set the habit back to unchanged;
        }
        realm.commitTransaction();
    }

    private boolean checkCompletion(Habit habit) {
        //check if habit hit the goal
        return (habit.getTarget() == habit.getTracker());
    }

    private boolean isSameDay(int position){
        Date updatedDate = mDataSource.get(position).getUpdated();
        //Checks if the item was updated today.
        return DateUtils.isToday(updatedDate.getTime());
    }

    private static class ViewHolder {
        public TextView habitName;
        public ImageButton habitChecked;
        public Button habitTracker;
        public RelativeLayout completionView;
        public RelativeLayout habitClicker;
        public TextView completion;
    }
}
