package rs.com.loctionbased.reminder.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.holders.PlaceViewHolder;
import rs.com.loctionbased.reminder.model.Place;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    //DATA
    private List<Place> mPlaces;
    private LayoutInflater mInflater;
    private AppCompatActivity mActivity;

    public PlaceAdapter(AppCompatActivity activity, List<Place> places) {
        mPlaces = places;
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceViewHolder(mInflater.inflate(R.layout.list_item_place, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        holder.setData(this, mActivity, mPlaces.get(position), position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }
}
