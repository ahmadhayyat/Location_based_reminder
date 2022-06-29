package rs.com.loctionbased.reminder.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rs.com.loctionbased.reminder.R;

public class FetchAddressIntentService extends IntentService {

    //CONSTS
    public static final String TAG = FetchAddressIntentService.class.getSimpleName();
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_ALIAS_KEY = PACKAGE_NAME + ".RESULT_ALIAS_KEY";
    public static final String RESULT_ADDRESS_KEY = PACKAGE_NAME + ".RESULT_ADDRESS_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";


    protected ResultReceiver mReceiver;


    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String errorMessage = "";

        Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        } catch (IOException ioException) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);

        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(FAILURE_RESULT, errorMessage, "");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(SUCCESS_RESULT, address.getFeatureName(), TextUtils.join(", ", addressFragments) );
        }
    }

    private void deliverResultToReceiver(int resultCode, String alias, String address) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_ALIAS_KEY, alias);
        bundle.putString(RESULT_ADDRESS_KEY, address);
        mReceiver.send(resultCode, bundle);
    }


}
