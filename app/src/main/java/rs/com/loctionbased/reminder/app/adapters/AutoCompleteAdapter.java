package rs.com.loctionbased.reminder.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rs.com.loctionbased.reminder.R;

public class AutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {

    private List<AutocompletePrediction> mResultList;
    private PlacesClient placesClient;

    public AutoCompleteAdapter(Context context, PlacesClient placesClient) {
        super(context, R.layout.auto_list_item, android.R.id.text1);
        this.placesClient = placesClient;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = super.getView(position, convertView, parent);

        AutocompletePrediction item = getItem(position);

        TextView textView1 = row.findViewById(android.R.id.text1);
        TextView textView2 = row.findViewById(android.R.id.text2);
        if (item != null) {
            textView1.setText(item.getPrimaryText(null));
            textView2.setText(item.getSecondaryText(null));
        }

        return row;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults results = new FilterResults();

                List<AutocompletePrediction> filterData = new ArrayList<>();

                if (charSequence != null) {
                    filterData = getAutocomplete(charSequence);
                }

                results.values = filterData;
                if (filterData != null) {
                    results.count = filterData.size();
                } else {
                    results.count = 0;
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {

                try {
                    if (results != null && results.count > 0) {
                        mResultList = (List<AutocompletePrediction>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    private List<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(29.598402, -95.490086),
                new LatLng(30.003708, -95.336572));


        final FindAutocompletePredictionsRequest.Builder requestBuilder =
                FindAutocompletePredictionsRequest.builder()
                        .setQuery(constraint.toString())
                        .setCountry("")
                        .setLocationBias(bounds)
//                        .setLocationRestriction(bounds)
                        .setSessionToken(AutocompleteSessionToken.newInstance())
                        .setTypeFilter(TypeFilter.ADDRESS);

        Task<FindAutocompletePredictionsResponse> results =
                placesClient.findAutocompletePredictions(requestBuilder.build());


        //Wait to get results.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (results.isSuccessful()) {
            if (results.getResult() != null) {
                return results.getResult().getAutocompletePredictions();
            }
            return null;
        } else {
            return null;
        }
    }
}
