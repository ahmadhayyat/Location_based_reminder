package rs.com.loctionbased.reminder.app.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.model.reminder.LocationBasedReminder;
import rs.com.loctionbased.reminder.util.ConversionUtil;
import rs.com.loctionbased.reminder.util.SnackbarUtil;

public class LocationBasedReminderDetailFragment extends Fragment implements OnMapReadyCallback {

    //CONST
    public static final String REMINDER_TO_DISPLAY = "REMINDER_TO_DISPLAY";
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP = 49;

    //DATA
    private LocationBasedReminder mReminder;

    //UI
    private GoogleMap mMap;
    private LinearLayout mContainer;
    private TextView mAddress;
    private TextView mRadius;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments().containsKey(REMINDER_TO_DISPLAY)) {
            mReminder = (LocationBasedReminder) getArguments().getSerializable(REMINDER_TO_DISPLAY);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    getActivity().finish();
                }
            };
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.fragment_location_based_reminder_detail_snackbar_error_no_reminder, SnackbarUtil.SnackbarDuration.LONG, callback);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_reminder_location_based, container, false);

        mContainer = (LinearLayout) rootView.findViewById(R.id.fragment_reminder_location_based_container);
        mAddress = (TextView) rootView.findViewById(R.id.fragment_reminder_location_based_address);
        mAddress.setText(mReminder.getPlace().getAddress());
        mRadius = (TextView) rootView.findViewById(R.id.fragment_reminder_location_based_radius);
        mRadius.setText(String.format(Locale.getDefault(),
                getResources().getString(R.string.fragment_detail_location_based_reminder_radius),
                mReminder.getPlace().getRadius(),
                mReminder.getEnteringExitingString(getActivity())) );

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_reminder_location_based_map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            setUpMap();
        else
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP);
    }

    @SuppressWarnings({"MissingPermission"})
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        int strokeColor = ContextCompat.getColor(getActivity(), R.color.map_circle_stroke);
        int shadeColor = ContextCompat.getColor(getActivity(), R.color.map_circle_shade);
        LatLng latLng = ConversionUtil.placeToLatLng(mReminder.getPlace());
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(mReminder.getPlace().getRadius())
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(2));
        mMap.addMarker(new MarkerOptions().position(latLng));

        CameraPosition cameraPos = new CameraPosition.Builder().tilt(60).target(latLng).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setUpMap();
                //TODO: Else Error message no permissions!
                break;
        }

    }
}