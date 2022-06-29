package rs.com.loctionbased.reminder.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.adapters.PlaceAdapter;
import rs.com.loctionbased.reminder.database.RemindyDAO;
import rs.com.loctionbased.reminder.model.Place;


public class PlaceListActivity extends AppCompatActivity implements View.OnClickListener {

    AdView mAdView;
    //CONST
    public static final int ADD_OR_EDIT_PLACE_REQUEST_CODE = 500;

    //DATA
    private List<Place> mPlaces = new ArrayList<>();
    private RemindyDAO mDao;

    //UI
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PlaceAdapter mAdapter;
    private RelativeLayout mNoItemsContainer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        /*MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adViewTwo);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_place_list_recycler);
        mNoItemsContainer = (RelativeLayout) findViewById(R.id.activity_place_list_no_items_container);
        mFab = (FloatingActionButton) findViewById(R.id.activity_place_list_fab);
        mFab.setOnClickListener(this);

        setUpToolbar();
        setUpRecyclerView();
        refreshRecyclerView();
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_place_list_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.activity_place_list_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new PlaceAdapter(this, mPlaces);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_decoration_complete_line));
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void refreshRecyclerView() {

        if(mDao == null)
            mDao = new RemindyDAO(getApplicationContext());

        mPlaces.clear();
        mPlaces.addAll(mDao.getPlaces());

        if(mPlaces.size() == 0) {
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoItemsContainer.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.activity_place_list_fab:
                Intent newPlaceIntent = new Intent(getApplicationContext(), PlaceActivity.class);
                startActivityForResult(newPlaceIntent, ADD_OR_EDIT_PLACE_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_OR_EDIT_PLACE_REQUEST_CODE) {
            refreshRecyclerView();
        }
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
}