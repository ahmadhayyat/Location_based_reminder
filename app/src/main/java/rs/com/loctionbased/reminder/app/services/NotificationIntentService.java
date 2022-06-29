package rs.com.loctionbased.reminder.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import rs.com.loctionbased.reminder.util.AlarmManagerUtil;
import rs.com.loctionbased.reminder.util.GeofenceUtil;
import rs.com.loctionbased.reminder.util.SharedPreferenceUtil;

public class NotificationIntentService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //CONST
    private static final String TAG = NotificationIntentService.class.getSimpleName();

    //DATA
    private GoogleApiClient mGoogleApiClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        //Reset triggered tasks
        SharedPreferenceUtil.setTriggeredTaskList(new ArrayList<Integer>(), getApplicationContext());

        //Update Alarms
        AlarmManagerUtil.updateAlarms(getApplicationContext());


        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected()");
        GeofenceUtil.addGeofences(getApplicationContext(), mGoogleApiClient);
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended()");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed()");

    }

}
