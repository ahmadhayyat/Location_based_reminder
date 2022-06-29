package rs.com.loctionbased.reminder.app.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.fragments.SettingsFragment;
import rs.com.loctionbased.reminder.enums.DateFormat;
import rs.com.loctionbased.reminder.enums.TimeFormat;
import rs.com.loctionbased.reminder.util.SharedPreferenceUtil;

public class SettingsActivity extends AppCompatActivity {

    //CONSTS
    public static final int SETTINGS_ACTIVITY_REQUEST_CODE = 109;

    //DATA
    private TimeFormat mOldTimeFormat;
    private DateFormat mOldDateFormat;
    public boolean mForceHomeRefresh;

    //UI
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mOldTimeFormat = SharedPreferenceUtil.getTimeFormat(this);
        mOldDateFormat = SharedPreferenceUtil.getDateFormat(this);

        setUpToolbar();

        if (savedInstanceState == null) {
            SettingsFragment fragment = new SettingsFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_settings_fragment, fragment);
            ft.commit();
        }
    }

    private void setUpToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.activity_settings_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.activity_settings_toolbar_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!mOldTimeFormat.equals(SharedPreferenceUtil.getTimeFormat(this)) || !mOldDateFormat.equals(SharedPreferenceUtil.getDateFormat(this)) || mForceHomeRefresh)
            setResult(RESULT_OK);
        else
            setResult(RESULT_CANCELED);
        finish();
    }
}
