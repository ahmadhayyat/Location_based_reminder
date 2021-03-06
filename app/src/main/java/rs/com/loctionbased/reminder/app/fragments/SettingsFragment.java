package rs.com.loctionbased.reminder.app.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.Constants;
import rs.com.loctionbased.reminder.app.activities.PlaceListActivity;
import rs.com.loctionbased.reminder.app.activities.SettingsActivity;
import rs.com.loctionbased.reminder.database.RemindyDbHelper;
import rs.com.loctionbased.reminder.enums.DateFormat;
import rs.com.loctionbased.reminder.enums.TriggerMinutesBeforeNotificationType;
import rs.com.loctionbased.reminder.util.PermissionUtil;

public class SettingsFragment extends PreferenceFragmentCompat {

    //CONSTS
    private static final int REQUEST_CODE_WRITE_STORAGE_EXPORT = 90;
    private static final int REQUEST_CODE_WRITE_STORAGE_IMPORT = 100;

    //UI
    private Preference mManagePlaces;
    private ListPreference mDateFormat;
    private ListPreference mTimeFormat;
    private ListPreference mTriggerMinutesBeforeNotification;
    private Preference mBackup;
    private Preference mRestore;
    private Preference mNotification;
    private Preference mAbout;
    private Preference mRate;
    private Preference mContact;


    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.remindy_settings);
        final Context packageContext = getActivity().getApplicationContext();


        mManagePlaces = findPreference(getResources().getString(R.string.settings_manage_places_key));
        mManagePlaces.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent goToPlaceListActivity = new Intent(packageContext, PlaceListActivity.class);
                startActivity(goToPlaceListActivity);

                //TODO: Yes, this is a dirty hack, someday to be fixed
                ((SettingsActivity)getActivity()).mForceHomeRefresh = true;
                return true;
            }
        });
        mDateFormat = (ListPreference) findPreference(getResources().getString(R.string.settings_date_format_key));
        mDateFormat.setEntries(getDateFormatEntries());

        mTimeFormat = (ListPreference) findPreference(getResources().getString(R.string.settings_time_format_key));

        mTriggerMinutesBeforeNotification = (ListPreference) findPreference(getResources().getString(R.string.settings_trigger_minutes_before_notification_key));
        mTriggerMinutesBeforeNotification.setEntries(getTriggerMinutesBeforeNotificationEntries());
        mTriggerMinutesBeforeNotification.setEntryValues(getTriggerMinutesBeforeNotificationEntryValues());

        mBackup = findPreference(getResources().getString(R.string.settings_backup_key));
        mRestore = findPreference(getResources().getString(R.string.settings_restore_key));

        mBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String[] nonGrantedPermissions = PermissionUtil.checkIfPermissionsAreGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(nonGrantedPermissions != null) {
                    requestPermissions(nonGrantedPermissions, REQUEST_CODE_WRITE_STORAGE_EXPORT);
                }else {
                    handleExportAction();
                }
                return false;
            }
        });

        mRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String[] nonGrantedPermissions = PermissionUtil.checkIfPermissionsAreGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (nonGrantedPermissions != null) {
                    requestPermissions(nonGrantedPermissions, REQUEST_CODE_WRITE_STORAGE_IMPORT);
                } else {
                    handleImportAction();
                }
                return false;
            }
        });

        mNotification = findPreference(getString(R.string.notification_setting));
        mNotification.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent();
            intent.setAction("android.settings.CHANNEL_NOTIFICATION_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("android.provider.extra.CHANNEL_ID", Constants.NOTIFICATION_CHANNEL_ID);
            intent.putExtra("android.provider.extra.APP_PACKAGE", getContext().getPackageName());
            startActivity(intent);
            return false;
        });
        // mAbout = findPreference(getResources().getString(R.string.settings_about_key));
/*
        mAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent goToAboutActivity = new Intent(packageContext, ActivitySample.class);
                startActivity(goToAboutActivity);
                return true;
            }
        });
*/


       /* mRate = findPreference(getResources().getString(R.string.remindy_settings_rate_key));
        mRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
                playStoreIntent.setData(Uri.parse(getResources().getString(R.string.url_market)));
                startActivity(playStoreIntent);
                return true;
            }
        });*/
        /*mContact = findPreference(getResources().getString(R.string.remindy_settings_contact_key));
        mContact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",getResources().getString(R.string.address_email), null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                return true;
            }
        });*/
    }

    private CharSequence[] getDateFormatEntries() {
        CharSequence entries[] = new CharSequence[DateFormat.values().length];

        for (int i = 0; i < DateFormat.values().length; i++)
            entries[i] = DateFormat.values()[i].formatCalendar(Calendar.getInstance());

        return entries;
    }

    private CharSequence[] getTriggerMinutesBeforeNotificationEntries() {
        CharSequence entries[] = new CharSequence[TriggerMinutesBeforeNotificationType.values().length];

        for (int i = 0; i < TriggerMinutesBeforeNotificationType.values().length; i++) {
            String format = (i == 0 ? getResources().getString(R.string.settings_trigger_minutes_before_notification_summary_single) :
                    getResources().getString(R.string.settings_trigger_minutes_before_notification_summary));

            entries[i] = String.format(Locale.getDefault(), format,
                    TriggerMinutesBeforeNotificationType.values()[i].getMinutes());
        }
        return entries;
    }

    private CharSequence[] getTriggerMinutesBeforeNotificationEntryValues() {
        CharSequence entries[] = new CharSequence[TriggerMinutesBeforeNotificationType.values().length];

        for (int i = 0; i < TriggerMinutesBeforeNotificationType.values().length; i++)
            entries[i] = TriggerMinutesBeforeNotificationType.values()[i].name();

        return entries;
    }

    private void handleExportAction() {
        try {
            if(new RemindyDbHelper(getActivity()).exportDatabase())
                Toast.makeText(getActivity(), getResources().getString(R.string.backup_successful), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), getResources().getString(R.string.backup_not_successful), Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Toast.makeText(getActivity(), getResources().getString(R.string.backup_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImportAction() {
        try {
            if(new RemindyDbHelper(getActivity()).importDatabase()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.restore_successful), Toast.LENGTH_SHORT).show();
                //TODO: Yes, this is a dirty hack, someday to be fixed
                ((SettingsActivity)getActivity()).mForceHomeRefresh = true;
            }
            else
                Toast.makeText(getActivity(), getResources().getString(R.string.restore_not_successful), Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Toast.makeText(getActivity(), getResources().getString(R.string.restore_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_WRITE_STORAGE_EXPORT && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleExportAction();
        }
        else if (requestCode == REQUEST_CODE_WRITE_STORAGE_IMPORT && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleImportAction();
        }
        else {
            Toast.makeText(getActivity(), getResources().getString(R.string.backup_restore_no_permissions), Toast.LENGTH_SHORT).show();
        }
    }

}
