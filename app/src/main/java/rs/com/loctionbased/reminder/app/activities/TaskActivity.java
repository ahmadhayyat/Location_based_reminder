package rs.com.loctionbased.reminder.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.Customization.FontsOverride;
import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.adapters.TaskViewPagerAdapter;
import rs.com.loctionbased.reminder.app.fragments.ReminderFragment;
import rs.com.loctionbased.reminder.app.fragments.TaskFragment;
import rs.com.loctionbased.reminder.app.holders.ImageAttachmentViewHolder;
import rs.com.loctionbased.reminder.database.RemindyDAO;
import rs.com.loctionbased.reminder.enums.ReminderType;
import rs.com.loctionbased.reminder.exception.CouldNotInsertDataException;
import rs.com.loctionbased.reminder.model.Task;
import rs.com.loctionbased.reminder.model.attachment.ImageAttachment;
import rs.com.loctionbased.reminder.model.reminder.LocationBasedReminder;
import rs.com.loctionbased.reminder.model.reminder.OneTimeReminder;
import rs.com.loctionbased.reminder.model.reminder.RepeatingReminder;
import rs.com.loctionbased.reminder.util.AlarmManagerUtil;
import rs.com.loctionbased.reminder.util.AttachmentUtil;
import rs.com.loctionbased.reminder.util.FileUtil;
import rs.com.loctionbased.reminder.util.GeofenceUtil;
import rs.com.loctionbased.reminder.util.SharedPreferenceUtil;
import rs.com.loctionbased.reminder.util.SnackbarUtil;

public class TaskActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    //CONST
    public static final String TASK_TO_EDIT = "TASK_TO_EDIT";
    public static final int TASK_ACTIVITY_REQUEST_CODE = 92;

    //UI
    private Toolbar mToolbar;
    private LinearLayout mContainer;
    private ViewPager mViewpager;
    private TaskViewPagerAdapter mTaskViewPagerAdapter;
    private TabLayout mTabLayout;
    private TaskFragment mTaskFragment;
    private ReminderFragment mReminderFragment;

    //DATA
    private Task mTask;
    private boolean editingTask;
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;

    AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ProximaNovaReg.ttf");

        /*MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        if(getIntent().hasExtra(TASK_TO_EDIT)) {
            mTask = (Task) getIntent().getSerializableExtra(TASK_TO_EDIT);
            editingTask = true;
        } else {
            mTask = new Task();
            editingTask = false;
        }

        mContainer = (LinearLayout) findViewById(R.id.activity_task_container);
        mViewpager = (ViewPager) findViewById(R.id.activity_task_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.activity_task_tab_layout);

        setUpToolbar();
        setupViewPagerAndTabLayout();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_task_toolbar);
        mToolbar.setTitle(editingTask ? R.string.activity_task_toolbar_title_edit : R.string.activity_task_toolbar_title_new);
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupViewPagerAndTabLayout() {

        mTaskFragment = new TaskFragment();
        mReminderFragment = new ReminderFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskFragment.TASK_ARGUMENT, mTask);
        mTaskFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(ReminderFragment.TASK_ARGUMENT, mTask);
        mReminderFragment.setArguments(bundle);

        mTitleList.clear();
        mFragmentList.clear();

        mTitleList.add(getResources().getString(R.string.activity_task_fragment_task_tab_title));
        mTitleList.add(getResources().getString(R.string.activity_task_fragment_reminder_tab_title));

        mFragmentList.add(mTaskFragment);
        mFragmentList.add(mReminderFragment);

        mTaskViewPagerAdapter = new TaskViewPagerAdapter(getSupportFragmentManager(), mTitleList, mFragmentList);
        mViewpager.setAdapter(mTaskViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewpager);
    }


    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_task_exit_dialog_title))
                .setMessage(getResources().getString(R.string.activity_task_exit_dialog_message))
                .setPositiveButton(getResources().getString(R.string.activity_task_exit_dialog_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileUtil.deleteAttachmentFiles(TaskActivity.this, mTask.getAttachments());
                        dialog.dismiss();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_task_exit_dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_task_save:
                updateTaskDataFromFragments();
                if(checkTaskData()) {
                    if(mTask.getReminderType().equals(ReminderType.NONE)) { //No reminder
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(getResources().getString(R.string.activity_task_warn_no_reminder_dialog_title))
                                .setMessage(getResources().getString(R.string.activity_task_warn_no_reminder_dialog_message))
                                .setPositiveButton(getResources().getString(R.string.activity_task_warn_no_reminder_dialog_positive),  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        handleTaskSave();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.activity_task_warn_no_reminder_dialog_negative), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        mViewpager.setCurrentItem(1, true); //Go to Reminder tab
                                    }
                                })
                                .create();
                        dialog.show();
                    } else {
                        androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(getResources().getString(R.string.activity_task_save_dialog_title))
                                .setMessage(getResources().getString(R.string.activity_task_save_dialog_message))
                                .setPositiveButton(getResources().getString(R.string.activity_task_save_dialog_positive),  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        handleTaskSave();
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.activity_task_save_dialog_negative), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateTaskDataFromFragments() {
        mTaskFragment.updateData();
        mReminderFragment.updateData();
    }

    private boolean checkTaskData() {

        ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mContainer.getWindowToken(), 0);

        if(mTask.getTitle() == null || mTask.getTitle().isEmpty()) {
            mViewpager.setCurrentItem(0, true); //Go to Task page
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_snackbar_error_no_title, SnackbarUtil.SnackbarDuration.LONG, null);
            return false;
        }

        switch (mTask.getReminderType()) {
            case NONE:
                return true;

            case ONE_TIME:
                if( ((OneTimeReminder)mTask.getReminder()).getDate() == null) {
                    mViewpager.setCurrentItem(1, true);
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_one_time_snackbar_error_invalid_date, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                if( ((OneTimeReminder)mTask.getReminder()).getTime() == null) {
                    mViewpager.setCurrentItem(1, true);
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_one_time_snackbar_error_invalid_time, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                break;

            case REPEATING:
                if( ((RepeatingReminder)mTask.getReminder()).getDate() == null) {
                    mViewpager.setCurrentItem(1, true);
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_repeating_snackbar_error_invalid_date, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                if( ((RepeatingReminder)mTask.getReminder()).getTime() == null) {
                    mViewpager.setCurrentItem(1, true);
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_repeating_snackbar_error_invalid_time, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                if( ((RepeatingReminder)mTask.getReminder()).getRepeatInterval() < 1) {
                    mViewpager.setCurrentItem(1, true);
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_repeating_snackbar_error_invalid_interval, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                switch ( ((RepeatingReminder)mTask.getReminder()).getRepeatEndType()) {
                    case FOR_X_EVENTS:
                        if( ((RepeatingReminder)mTask.getReminder()).getRepeatEndNumberOfEvents() < 1) {
                            mViewpager.setCurrentItem(1, true);
                            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_repeating_snackbar_error_invalid_number_events, SnackbarUtil.SnackbarDuration.SHORT, null);
                            return false;
                        }
                        break;
                    case UNTIL_DATE:
                        if( ((RepeatingReminder)mTask.getReminder()).getRepeatEndDate() == null) {
                            mViewpager.setCurrentItem(1, true);
                            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_repeating_snackbar_error_invalid_end_date, SnackbarUtil.SnackbarDuration.SHORT, null);
                            return false;
                        }
                        if( ((RepeatingReminder)mTask.getReminder()).getRepeatEndDate().compareTo(((RepeatingReminder)mTask.getReminder()).getDate()) <= 0) {
                            mViewpager.setCurrentItem(1, true);
                            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_repeating_snackbar_error_end_date_before_date, SnackbarUtil.SnackbarDuration.SHORT, null);
                            return false;
                        }
                        break;
                }
                break;

            case LOCATION_BASED:
                if( !((LocationBasedReminder)mTask.getReminder()).getTriggerEntering() && !((LocationBasedReminder)mTask.getReminder()).getTriggerExiting() ) {
                    mViewpager.setCurrentItem(1, true); //Go to Reminder page
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_location_based_snackbar_error_set_entering_exiting, SnackbarUtil.SnackbarDuration.SHORT, null);
                    return false;
                }
                break;
        }

        return true;
    }


    private void handleTaskSave() {
        AttachmentUtil.cleanInvalidAttachments(mTask.getAttachments());

        try {
            RemindyDAO dao = new RemindyDAO(this);

            if (!editingTask) {

                //Insert the task
                dao.insertTask(mTask);

                //Update geofences
                if(mTask.getReminderType().equals(ReminderType.LOCATION_BASED))
                    GeofenceUtil.updateGeofences(getApplicationContext(), mGoogleApiClient);

                //Update alarms
                if(mTask.getReminderType().equals(ReminderType.ONE_TIME) || mTask.getReminderType().equals(ReminderType.REPEATING)) {

                    //Remove task from triggeredTasks list
                    SharedPreferenceUtil.removeIdFromTriggeredTasks(getApplicationContext(), mTask.getId());

                    //Update alarms
                    AlarmManagerUtil.updateAlarms(getApplicationContext());
                }

            }

            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(HomeActivity.NEW_TASK_RETURN_REMINDER_TYPE, mTask.getReminderType());
                    returnIntent.putExtra(TaskActivity.TASK_TO_EDIT, mTask);
                    setResult(RESULT_OK, returnIntent);       //Task was created, set result to OK
                    finish();
                }
            };
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_task_snackbar_save_successful, SnackbarUtil.SnackbarDuration.SHORT, callback);

        } catch (CouldNotInsertDataException e) {
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_snackbar_error_saving, SnackbarUtil.SnackbarDuration.SHORT, null);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EditImageAttachmentActivity.EDIT_IMAGE_ATTACHMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra(EditImageAttachmentActivity.HOLDER_POSITION_EXTRA, -1);
            ImageAttachment imageAttachment = (ImageAttachment) data.getSerializableExtra(EditImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA);
            if(position != -1) {
                TaskFragment taskFragment = (TaskFragment) mTaskViewPagerAdapter.getRegisteredFragment(0);
                ImageAttachmentViewHolder holder = (ImageAttachmentViewHolder) taskFragment.mRecyclerView.findViewHolderForAdapterPosition(position);
                holder.updateImageAttachment(imageAttachment);
            }
        }

        if(requestCode == ViewImageAttachmentActivity.VIEW_IMAGE_ATTACHMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra(ViewImageAttachmentActivity.HOLDER_POSITION_EXTRA, -1);
            ImageAttachment imageAttachment = (ImageAttachment) data.getSerializableExtra(ViewImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA);
            if(position != -1) {
                TaskFragment taskFragment = (TaskFragment) mTaskViewPagerAdapter.getRegisteredFragment(0);
                ImageAttachmentViewHolder holder = (ImageAttachmentViewHolder) taskFragment.mRecyclerView.findViewHolderForAdapterPosition(position);
                holder.updateImageAttachment(imageAttachment);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
