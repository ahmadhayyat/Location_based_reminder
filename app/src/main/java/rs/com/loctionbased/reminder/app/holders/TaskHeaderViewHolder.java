package rs.com.loctionbased.reminder.app.holders;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.adapters.HomeAdapter;

public class TaskHeaderViewHolder extends RecyclerView.ViewHolder {

    //UI
    private TextView mHeaderTitle;

    //DATA
    private HomeAdapter mAdapter;
    private Fragment mFragment;
    private int mPosition;

    public TaskHeaderViewHolder(View itemView) {
        super(itemView);

        mHeaderTitle = (TextView) itemView.findViewById(R.id.item_task_header_title);
    }

    public void setData(HomeAdapter adapter, Fragment fragment, String title, boolean headerTitleRed, int position) {
        mAdapter = adapter;
        mFragment = fragment;
        mPosition = position;

        mHeaderTitle.setText(title);
        if(headerTitleRed)
            mHeaderTitle.setTextColor(ContextCompat.getColor(mFragment.getActivity(), R.color.header_title_red));
        else
            mHeaderTitle.setTextColor(ContextCompat.getColor(mFragment.getActivity(), R.color.primary));
    }
}
