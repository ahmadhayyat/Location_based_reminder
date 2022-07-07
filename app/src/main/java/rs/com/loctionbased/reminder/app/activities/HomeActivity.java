package rs.com.loctionbased.reminder.app.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.Customization.FontsOverride;
import rs.com.loctionbased.reminder.HabitsTracker.MainActivity;
import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.adapters.HomeViewPagerAdapter;
import rs.com.loctionbased.reminder.app.fragments.HomeListFragment;
import rs.com.loctionbased.reminder.app.services.NotificationIntentService;
import rs.com.loctionbased.reminder.enums.ReminderType;
import rs.com.loctionbased.reminder.enums.TaskSortType;
import rs.com.loctionbased.reminder.enums.ViewPagerTaskDisplayType;
import rs.com.loctionbased.reminder.ui.login.LoginActivity;
import rs.com.loctionbased.reminder.util.SnackbarUtil;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnHabbitTracker;

    //CONST
    public static final String TAG = HomeActivity.class.getSimpleName();
    public static final int NEW_TASK_REQUEST_CODE = 490;
    public static final String NEW_TASK_RETURN_REMINDER_TYPE = "NEW_TASK_RETURN_REMINDER_TYPE";
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION_NOTIFICATION_GEOFENCE_SERVICE = 149;

    //UI
    private ViewPager mViewpager;
    private HomeViewPagerAdapter mHomeViewPagerAdapter;
    private TabLayout mTabLayout;
    private FloatingActionButton mFab;
    private HomeListFragment mUnprogrammedTasksListFragment;
    private HomeListFragment mProgrammedTasksListFragment;
    private HomeListFragment mDoneTasksListFragment;

    //DATA
    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private TaskSortType mTaskSortType = TaskSortType.DATE;

    AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.activity_home_toolbar_title);

        btnHabbitTracker = findViewById(R.id.btnHabbitTracker);
        /*MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adViewOne);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        FontsOverride.setDefaultFont(HomeActivity.this, "DEFAULT", "fonts/ProximaNovaReg.ttf");
        mViewpager = (ViewPager) findViewById(R.id.activity_home_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.activity_home_tab_layout);
        mFab = (FloatingActionButton) findViewById(R.id.activity_home_fab);
        mFab.setOnClickListener(this);

        setupViewPagerAndTabLayout();

        btnHabbitTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setupViewPagerAndTabLayout() {

        mUnprogrammedTasksListFragment = new HomeListFragment();
        mProgrammedTasksListFragment = new HomeListFragment();
        mDoneTasksListFragment = new HomeListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.ARGUMENT_TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.UNPROGRAMMED);
        mUnprogrammedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.ARGUMENT_TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.PROGRAMMED);
        mProgrammedTasksListFragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putSerializable(HomeListFragment.ARGUMENT_TASK_TYPE_TO_DISPLAY, ViewPagerTaskDisplayType.DONE);
        mDoneTasksListFragment.setArguments(bundle);

        titleList.clear();
        fragmentList.clear();

        titleList.add(getResources().getString(ViewPagerTaskDisplayType.UNPROGRAMMED.getFriendlyNameRes()));
        titleList.add(getResources().getString(ViewPagerTaskDisplayType.PROGRAMMED.getFriendlyNameRes()));
        titleList.add(getResources().getString(ViewPagerTaskDisplayType.DONE.getFriendlyNameRes()));

        fragmentList.add(mUnprogrammedTasksListFragment);
        fragmentList.add(mProgrammedTasksListFragment);
        fragmentList.add(mDoneTasksListFragment);

        mHomeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager(), titleList, fragmentList);
        mViewpager.setAdapter(mHomeViewPagerAdapter);
        mViewpager.setCurrentItem(1);     //Start at page 2
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mHomeViewPagerAdapter.getRegisteredFragments().size(); i++) {
                    int key = mHomeViewPagerAdapter.getRegisteredFragments().keyAt(i);

                    if (mHomeViewPagerAdapter.getRegisteredFragments().get(key).mActionMode != null)
                        mHomeViewPagerAdapter.getRegisteredFragments().get(key).mActionMode.finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mTabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION_NOTIFICATION_GEOFENCE_SERVICE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    doStartNotificationService();
                else {
                    SnackbarUtil.showSnackbar(mViewpager, SnackbarUtil.SnackbarType.ERROR, R.string.geofence_snackbar_warning_no_permissons, SnackbarUtil.SnackbarDuration.LONG, null);
                }
                break;
        }

    }

    private void doStartNotificationService() {
        Intent startNotificationServiceIntent = new Intent(getApplicationContext(), NotificationIntentService.class);
        startService(startNotificationServiceIntent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_home_sort:
                mTaskSortType = (mTaskSortType == TaskSortType.DATE ? TaskSortType.PLACE : TaskSortType.DATE);
                mHomeViewPagerAdapter.getRegisteredFragment(1).setSortTypeAndRefresh(mTaskSortType);
                mHomeViewPagerAdapter.getRegisteredFragment(2).setSortTypeAndRefresh(mTaskSortType);
                SnackbarUtil.showSnackbar(mViewpager, SnackbarUtil.SnackbarType.NOTICE, mTaskSortType.getFriendlyMessageRes(), SnackbarUtil.SnackbarDuration.SHORT, null);
                return true;

            case R.id.menu_home_settings:
                Intent goToSettingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(goToSettingsActivity, SettingsActivity.SETTINGS_ACTIVITY_REQUEST_CODE);
                return true;

            case R.id.menu_home_Logout:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_home_fab:
                Intent openTaskActivity = new Intent(this, TaskActivity.class);
                startActivityForResult(openTaskActivity, NEW_TASK_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            ReminderType rt;
            if (data.hasExtra(NEW_TASK_RETURN_REMINDER_TYPE)) {
                rt = (ReminderType) data.getSerializableExtra(NEW_TASK_RETURN_REMINDER_TYPE);

                try {
                    switch (rt) {
                        case NONE:
                            mHomeViewPagerAdapter.getRegisteredFragment(0).refreshRecyclerView();
                            break;
                        case LOCATION_BASED:
                        case ONE_TIME:
                        case REPEATING:
                            mHomeViewPagerAdapter.getRegisteredFragment(1).refreshRecyclerView();
                    }
                } catch (NullPointerException e) {
                }
            } else {
                setupViewPagerAndTabLayout();
            }
        }

        if (requestCode == TaskDetailActivity.TASK_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {     //Task has been deleted or edited

            if (data.hasExtra(TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_TYPE) && data.hasExtra(TaskDetailActivity.TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX)) {

                int position = data.getIntExtra(TaskDetailActivity.TASK_DETAIL_RETURN_TASK_POSITION, -1);
                int viewPagerIndex = data.getIntExtra(TaskDetailActivity.TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX, -1);

                if (position == -1) {
                    setupViewPagerAndTabLayout();
                    return;
                }

                switch (data.getIntExtra(TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_TYPE, -1)) {
                    case TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_DELETED:
                        try {
                            mHomeViewPagerAdapter.getRegisteredFragment(viewPagerIndex).removeViewHolderItem(position);
                        } catch (NullPointerException e) {
                        }
                        break;
                    case TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_EDITED:
                        try {
                            mHomeViewPagerAdapter.getRegisteredFragment(viewPagerIndex).updateViewholderItem(position);
                        } catch (NullPointerException e) {
                        }
                        break;

                    case TaskDetailActivity.TASK_DETAIL_RETURN_ACTION_EDITED_REMINDER:
                        setupViewPagerAndTabLayout();
                        break;
                }
            } else {
                Log.d(TAG, "Error! TASK_DETAIL_RETURN_ACTION_TYPE or TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX == null");
                SnackbarUtil.showSnackbar(mViewpager, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        }


        if (requestCode == SettingsActivity.SETTINGS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
                setupViewPagerAndTabLayout();
        }
    }

}
