package rs.com.loctionbased.reminder.app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.SharedClass;
import rs.com.loctionbased.reminder.app.adapters.AutoCompleteAdapter;
import rs.com.loctionbased.reminder.app.dialogs.EditPlaceDialogFragment;
import rs.com.loctionbased.reminder.app.services.AddressResultReceiver;
import rs.com.loctionbased.reminder.app.services.FetchAddressIntentService;
import rs.com.loctionbased.reminder.database.RemindyDAO;
import rs.com.loctionbased.reminder.enums.TapTargetSequenceType;
import rs.com.loctionbased.reminder.exception.CouldNotDeleteDataException;
import rs.com.loctionbased.reminder.exception.CouldNotGetDataException;
import rs.com.loctionbased.reminder.exception.CouldNotInsertDataException;
import rs.com.loctionbased.reminder.exception.CouldNotUpdateDataException;
import rs.com.loctionbased.reminder.model.Place;
import rs.com.loctionbased.reminder.model.Task;
import rs.com.loctionbased.reminder.util.GeofenceUtil;
import rs.com.loctionbased.reminder.util.SnackbarUtil;
import rs.com.loctionbased.reminder.util.TapTargetSequenceUtil;

public class PlaceActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        AddressResultReceiver.AddressReceiverListener,
        EditPlaceDialogFragment.EditPlaceDialogDismissListener {

    //CONST
    public static final String PLACE_TO_EDIT = "PLACE_TO_EDIT";
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP = 40;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION_GET_LAST_LOCATION = 41;

    //DATA
    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;
    private Place mPlaceToEdit;
    private Place mPlace;
    private Marker mPlaceMarker;
    private Circle mPlaceCircle;
    private boolean mAliasAddressAlreadySet;
    private RemindyDAO mDao;
    AutoCompleteTextView autoCompleteTextView;
    public static ImageView btnCloseDestOne;
    PlacesClient placesClient;
    AutoCompleteAdapter adapter;

    //UI
    private PlaceAutocompleteFragment mAutocompleteFragment;
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private RelativeLayout mMapContainer;
    private SeekBar mRadius;
    private TextView mRadiusDisplay;
    private TextView mAlias;
    private TextView mAddress;
    private ImageView mAliasAddressEdit;
    private LinearLayout mAliasAddressContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        if(getIntent().hasExtra(PLACE_TO_EDIT)) {
            mPlaceToEdit = (Place) getIntent().getSerializableExtra(PLACE_TO_EDIT);
            mPlace = new Place(mPlaceToEdit);
        } else {
            mPlace = new Place();
        }

        btnCloseDestOne = findViewById(R.id.btnCloseDestOne);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this ,
                        this )
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        mMapContainer = (RelativeLayout) findViewById(R.id.activity_place_map_container);
        mRadius = (SeekBar) findViewById(R.id.activity_place_radius_seekbar);
        mRadius.setMax(14);
        mRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPlace.setRadius((progress+1) * 100);
                mRadiusDisplay.setText(String.valueOf(mPlace.getRadius()) + " m");
                if(mPlaceCircle != null)
                    mPlaceCircle.setRadius(mPlace.getRadius());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mRadiusDisplay = (TextView) findViewById(R.id.activity_place_radius_display);
        mAlias = (TextView) findViewById(R.id.activity_place_alias);
        mAddress = (TextView) findViewById(R.id.activity_place_address);
        mAliasAddressEdit = (ImageView) findViewById(R.id.activity_place_alias_address_edit);
        mAliasAddressEdit.setOnClickListener(this);
        mAliasAddressContainer = (LinearLayout) findViewById(R.id.activity_place_alias_address_container);

        setUpToolbar();
        TapTargetSequenceUtil.showTapTargetSequenceFor(this, TapTargetSequenceType.PLACE_ACTIVITY);

        String apiKey = getString(R.string.places_api_key);
        if (apiKey.isEmpty()) {
            autoCompleteTextView.setText(getString(R.string.error));
            return;
        }
        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(getApplicationContext(), apiKey);
        }

        initAutoCompleteTextView();

    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_place_toolbar);
        mToolbar.setTitle(getResources().getString( (mPlaceToEdit != null ? R.string.activity_place_title_edit : R.string.activity_place_title_new) ));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_place_map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended " + i, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed " + connectionResult.toString(), Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                mPlace.setLatitude(latLng.latitude);
                mPlace.setLongitude(latLng.longitude);

                if(mPlaceMarker == null)
                    drawMarkerWithCircle(latLng, mPlace.getRadius());
                else
                    updateMarkerWithCircle(latLng);

                Location loc = new Location(LocationManager.GPS_PROVIDER);
                loc.setLatitude(mPlaceMarker.getPosition().latitude);
                loc.setLongitude(mPlaceMarker.getPosition().longitude);

                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.NOTICE, R.string.activity_place_snackbar_fetching_address, SnackbarUtil.SnackbarDuration.LONG, null);
                fetchAddressFromLocation(loc);
            }
        });

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)  {
            setUpMap();
        }
        else
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP);
    }

    @SuppressWarnings({"MissingPermission"})
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        if(mPlaceToEdit == null) {
            moveCameraToLastKnownLocation();
            mRadius.setProgress(1);
            mRadiusDisplay.setText("100 m");

        } else {
            drawMarkerWithCircle(new LatLng(mPlace.getLatitude(), mPlace.getLongitude()), mPlace.getRadius());        //If editing a place, go to that place and add a marker, circle

            Location loc = new Location(LocationManager.GPS_PROVIDER);
            loc.setLatitude(mPlace.getLatitude());
            loc.setLongitude(mPlace.getLongitude());
            moveCameraToLocation(loc);

            TransitionManager.beginDelayedTransition(mMapContainer, new Slide(Gravity.BOTTOM));
            mAlias.setText(mPlace.getAlias());
            mAddress.setText(mPlace.getAddress());
            mAliasAddressContainer.setVisibility(View.VISIBLE);
            mAliasAddressAlreadySet = true;

            mRadius.setProgress(mPlace.getRadius()/100 -1 );
            mRadiusDisplay.setText(String.valueOf(mPlace.getRadius()) + " m");

        }
    }

    private void drawMarkerWithCircle(LatLng position, double circleRadiusInMeters){
        int strokeColor = 0xffff0000;
        int shadeColor = 0x44ff0000;

        CircleOptions circleOptions = new CircleOptions().center(position).radius(circleRadiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
        if(mPlaceCircle != null)
            mPlaceCircle.remove();
        mPlaceCircle = mMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position);
        if(mPlaceMarker != null)
            mPlaceMarker.remove();
        mPlaceMarker = mMap.addMarker(markerOptions);
        mPlaceCircle.setZIndex(100);
    }

    private void updateMarkerWithCircle(LatLng position) {
        mPlaceCircle.setCenter(position);
        mPlaceMarker.setPosition(position);
    }

    private void moveCameraToLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                moveCameraToLocation(lastLocation);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION_GET_LAST_LOCATION);
        }
    }

    private void moveCameraToLocation(Location location) {
        if (location != null) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPos = new CameraPosition.Builder().tilt(60).target(latlng).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null);
        }
    }

    protected void fetchAddressFromLocation(Location location) {
        mResultReceiver = new AddressResultReceiver(new Handler());
        mResultReceiver.setReceiverListener(this);

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    @Override
    public void onReceiveAddressResult(int resultCode, Bundle resultData) {
        String alias = resultData.getString(FetchAddressIntentService.RESULT_ALIAS_KEY);
        String address = resultData.getString(FetchAddressIntentService.RESULT_ADDRESS_KEY);

        if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
            setAliasAndAddress(alias, address);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    setAliasAndAddress("", "");
                }
            };
            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_fetching_address, SnackbarUtil.SnackbarDuration.SHORT, callback);
        }
    }

    private void setAliasAndAddress(String alias, String address) {
        mPlace.setAlias(alias);
        mPlace.setAddress(address);

        if(!mAliasAddressAlreadySet) {
            TransitionManager.beginDelayedTransition(mMapContainer, new Slide(Gravity.BOTTOM));
            mAlias.setText(alias);
            mAddress.setText(address);
            mAliasAddressContainer.setVisibility(View.VISIBLE);
            mAliasAddressAlreadySet = true;

        } else {
            mAlias.setText(alias);
            mAddress.setText(address);
        }
    }


    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION_SHOW_ICON_IN_MAP:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpMap();
                }
                break;
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION_GET_LAST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (lastLocation != null) {
                        moveCameraToLocation(lastLocation);
                    }
                }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_place_alias_address_edit:
                FragmentManager fm = getSupportFragmentManager();
                EditPlaceDialogFragment dialog = EditPlaceDialogFragment.newInstance(mPlace.getAlias(), mPlace.getAddress());
                dialog.setListener(this);
                dialog.show(fm, "EditPlaceDialogFragment");
        }
    }

    @Override
    public void onFinishEditPlaceDialog(String alias, String address) {
        mPlace.setAlias(alias);
        mPlace.setAddress(address);

        mAlias.setText(alias);
        mAddress.setText(address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place, menu);

        if(mPlaceToEdit == null)
            menu.findItem(R.id.menu_place_delete).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_place_save:
                handleSaveOrUpdatePlace();
                break;

            case R.id.menu_place_delete:
                handleDeletePlace();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSaveOrUpdatePlace() {
        BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                setResult(RESULT_OK);
                finish();
            }
        };

        mDao = new RemindyDAO(getApplicationContext());

        //Verify mPlace is set
        if(mPlace.getLongitude() == 0) {
            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_no_place, SnackbarUtil.SnackbarDuration.LONG, null);
            return;
        }
        if(mPlace.getAlias() == null || mPlace.getAlias().isEmpty() || mPlace.getAlias().equals(getResources().getString(R.string.activity_place_alias_hint))) {
            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.NOTICE, R.string.activity_place_snackbar_error_no_alias, SnackbarUtil.SnackbarDuration.LONG, null);
            return;
        }

        if(mPlaceToEdit != null) {
            try {
                mDao.updatePlace(mPlace);
                GeofenceUtil.updateGeofences(getApplicationContext(), mGoogleApiClient);    //Update geofences when updating places!
                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_place_snackbar_edit_succesful, SnackbarUtil.SnackbarDuration.SHORT, callback);
            } catch (CouldNotUpdateDataException e ) {
                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_saving, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        } else {
            try {
                mDao.insertPlace(mPlace);
                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_place_snackbar_save_succesful, SnackbarUtil.SnackbarDuration.SHORT, callback);
            } catch (CouldNotInsertDataException e ) {
                SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_saving, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        }
    }

    private void handleDeletePlace() {
        mDao = new RemindyDAO(getApplicationContext());

        final BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                setResult(RESULT_OK);
                finish();
            }
        };

        List<Task> locationBasedTasks = new ArrayList<>();
        try {
            locationBasedTasks = mDao.getLocationBasedTasksAssociatedWithPlace(mPlace.getId(), -1);
        }catch (CouldNotGetDataException e) {
            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_deleting, SnackbarUtil.SnackbarDuration.LONG, null);
        }

        @StringRes int title = (locationBasedTasks.size() > 0 ? R.string.activity_place_dialog_delete_with_associated_tasks_title : R.string.activity_place_dialog_delete_title);
        @StringRes int message = (locationBasedTasks.size() > 0 ? R.string.activity_place_dialog_delete_with_associated_tasks_message : R.string.activity_place_dialog_delete_message);
        @StringRes int positive = (locationBasedTasks.size() > 0 ? R.string.activity_place_dialog_delete_with_associated_tasks_positive : R.string.activity_place_dialog_delete_positive);
        @StringRes int negative = (locationBasedTasks.size() > 0 ? R.string.activity_place_dialog_delete_negative : R.string.activity_place_dialog_delete_negative);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(title))
                .setMessage(getResources().getString(message))
                .setPositiveButton(getResources().getString(positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mDao.deletePlace(mPlace.getId());
                            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_place_snackbar_delete_succesful, SnackbarUtil.SnackbarDuration.SHORT, callback);
                            dialog.dismiss();
                        } catch (CouldNotDeleteDataException e) {
                            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_place_snackbar_error_deleting, SnackbarUtil.SnackbarDuration.LONG, callback);
                        }
                    }
                })
                .setNegativeButton(getResources().getString(negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_place_dialog_exit_title))
                .setMessage(getResources().getString(R.string.activity_place_dialog_exit_message))
                .setPositiveButton(getResources().getString(R.string.activity_place_dialog_exit_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_place_dialog_exit_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void initAutoCompleteTextView() {

        autoCompleteTextView = findViewById(R.id.auto);
        autoCompleteTextView.setThreshold(1);

        Point pointSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(pointSize);
        autoCompleteTextView.setDropDownWidth(pointSize.x-70);

        btnCloseDestOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCloseDestOne.setVisibility(View.GONE);
                autoCompleteTextView.setText("");
                initAutoCompleteTextView();
                SharedClass.destlat = 0.0;
                SharedClass.destlng = 0.0;
                SharedClass.destlatOne = 0.0;
                SharedClass.destlngOne = 0.0;
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    btnCloseDestOne.setVisibility(View.VISIBLE);
                } else {
                    btnCloseDestOne.setVisibility(View.GONE);
                }
            }
        });

        placesClient = com.google.android.libraries.places.api.Places.createClient(this);
        autoCompleteTextView.setOnItemClickListener(autocompleteClickListener);
        adapter = new AutoCompleteAdapter(PlaceActivity.this, placesClient);
        autoCompleteTextView.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            try {
                final AutocompletePrediction item = adapter.getItem(i);
                String placeID = null;
                if (item != null) {
                    placeID = item.getPlaceId();
                }

                List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID,
                        com.google.android.libraries.places.api.model.Place.Field.NAME,
                        com.google.android.libraries.places.api.model.Place.Field.ADDRESS
                        , com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            SharedClass.destlat = task.getPlace().getLatLng().latitude;
                            SharedClass.destlng = task.getPlace().getLatLng().longitude;
                            SharedClass.destlatOne = task.getPlace().getLatLng().latitude;
                            SharedClass.destlngOne = task.getPlace().getLatLng().longitude;

                            //Save lat and long
                            mPlace.setLatitude(task.getPlace().getLatLng().latitude);
                            mPlace.setLongitude(task.getPlace().getLatLng().longitude);

                            if(mPlaceMarker == null)
                                drawMarkerWithCircle(task.getPlace().getLatLng(), mPlace.getRadius());
                            else
                                updateMarkerWithCircle(task.getPlace().getLatLng());

                            Location loc = new Location(LocationManager.GPS_PROVIDER);
                            loc.setLatitude(mPlace.getLatitude());
                            loc.setLongitude(mPlace.getLongitude());
                            moveCameraToLocation(loc);

                            setAliasAndAddress(task.getPlace().getName().toString(), task.getPlace().getAddress().toString());

//                            autoCompleteTextView.setText(task.getPlace().getName() + "\n" + task.getPlace().getAddress());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            autoCompleteTextView.setText(e.getMessage());
                            SnackbarUtil.showSnackbar(mMapContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


}
