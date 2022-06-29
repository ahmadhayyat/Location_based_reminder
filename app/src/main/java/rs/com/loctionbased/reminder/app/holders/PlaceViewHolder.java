package rs.com.loctionbased.reminder.app.holders;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.activities.PlaceActivity;
import rs.com.loctionbased.reminder.app.activities.PlaceListActivity;
import rs.com.loctionbased.reminder.app.adapters.PlaceAdapter;
import rs.com.loctionbased.reminder.model.Place;

public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {

    private PlaceAdapter mAdapter;
    private AppCompatActivity mActivity;

    //UI
    private RelativeLayout mContainer;
    private TextView mAlias;
    private TextView mAddress;

    private MapView mMapView;
    private GoogleMap mMap;


    //DATA
    private Place mCurrent;
    private int mPlacePosition;

    public PlaceViewHolder(View itemView) {
        super(itemView);

        mContainer = (RelativeLayout) itemView.findViewById(R.id.item_place_container);
        mAlias = (TextView) itemView.findViewById(R.id.item_place_alias);
        mAddress = (TextView) itemView.findViewById(R.id.item_place_address);
        mMapView = (MapView) itemView.findViewById(R.id.item_place_map);
        mMapView.setClickable(false);

        mMapView.onCreate(null);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapsInitializer.initialize(mActivity.getApplicationContext());
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);

        updateMapView();
    }


    public void setData(PlaceAdapter adapter, AppCompatActivity activity, Place current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPlacePosition = position;

        mAlias.setText(mCurrent.getAlias());
        mAddress.setText(mCurrent.getAddress());

        if (mMap != null)
            updateMapView();

    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
    }


    private void updateMapView() {

        if (mCurrent != null) {
            mMap.clear();

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);

            LatLng loc = new LatLng(mCurrent.getLatitude(), mCurrent.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13f));
            mMap.addMarker(new MarkerOptions().position(loc).icon(icon));

        }
    }




    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_place_container:
                Intent editPlaceIntent = new Intent(mActivity, PlaceActivity.class);
                editPlaceIntent.putExtra(PlaceActivity.PLACE_TO_EDIT, mCurrent);
                mActivity.startActivityForResult(editPlaceIntent, PlaceListActivity.ADD_OR_EDIT_PLACE_REQUEST_CODE);
                break;
        }
    }


}
