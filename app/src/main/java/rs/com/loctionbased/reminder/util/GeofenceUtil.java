package rs.com.loctionbased.reminder.util;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.app.services.GeofenceNotificationIntentService;
import rs.com.loctionbased.reminder.database.RemindyDAO;
import rs.com.loctionbased.reminder.model.Place;


public class GeofenceUtil {

    //CONSTS
    private static final long NEVER_EXPIRE = -1;
    private static final int LOITERING_DWELL_DELAY = 30 * 1000;

    //DATA
    private static PendingIntent mGeofencePendingIntent;

    public static void addGeofences(final Context context, GoogleApiClient googleApiClient) {
        checkGoogleApiClient(googleApiClient);
        List<Place> places = new RemindyDAO(context).getActivePlaces();

        if(places.size() > 0) {
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                LocationServices.GeofencingApi.addGeofences(
                        googleApiClient,
                        getGeofencingRequest(places),
                        getGeofencePendingIntent(context)
                ).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess())
                            Toast.makeText(context, "Geofences added/updated!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }


    public static void updateGeofences(final Context context, final GoogleApiClient googleApiClient) {
        checkGoogleApiClient(googleApiClient);

        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, getGeofencePendingIntent(context))
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess()) {
                                addGeofences(context, googleApiClient);
                            }
                        }
                    });
        }
    }

    private static void checkGoogleApiClient(GoogleApiClient googleApiClient) {
        if(googleApiClient == null || !googleApiClient.isConnected()) {
            throw new IllegalStateException("Google API client must be connected");
        }
    }

    private static GeofencingRequest getGeofencingRequest(List<Place> places) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(getGeofenceList(places));
        return builder.build();
    }

    private static List<Geofence> getGeofenceList(List<Place> places) {
        List<Geofence> geofenceList = new ArrayList<>();

        for (Place place : places){
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(place.getId()))
                    .setCircularRegion(
                            place.getLatitude(),
                            place.getLongitude(),
                            place.getRadius()
                    )
                    .setExpirationDuration(NEVER_EXPIRE)
                    .setLoiteringDelay(LOITERING_DWELL_DELAY)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)

                    .build());
        }

        return geofenceList;
    }

    private static PendingIntent getGeofencePendingIntent(Context context) {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(context, GeofenceNotificationIntentService.class);
        mGeofencePendingIntent =  PendingIntent.getService(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        return mGeofencePendingIntent;
    }

}
