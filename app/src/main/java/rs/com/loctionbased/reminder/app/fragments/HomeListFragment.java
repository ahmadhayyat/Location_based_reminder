package rs.com.loctionbased.reminder.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.activities.TaskDetailActivity;
import rs.com.loctionbased.reminder.app.adapters.HomeAdapter;
import rs.com.loctionbased.reminder.app.interfaces.ViewHolderClickListener;
import rs.com.loctionbased.reminder.database.RemindyDAO;
import rs.com.loctionbased.reminder.enums.ReminderType;
import rs.com.loctionbased.reminder.enums.TaskSortType;
import rs.com.loctionbased.reminder.enums.TaskStatus;
import rs.com.loctionbased.reminder.enums.ViewPagerTaskDisplayType;
import rs.com.loctionbased.reminder.exception.CouldNotDeleteDataException;
import rs.com.loctionbased.reminder.exception.CouldNotGetDataException;
import rs.com.loctionbased.reminder.exception.CouldNotUpdateDataException;
import rs.com.loctionbased.reminder.model.Task;
import rs.com.loctionbased.reminder.util.CalendarUtil;
import rs.com.loctionbased.reminder.util.ConversionUtil;
import rs.com.loctionbased.reminder.util.SnackbarUtil;
import rs.com.loctionbased.reminder.viewmodel.TaskViewModel;

public class HomeListFragment extends Fragment implements ViewHolderClickListener {

    public static final String ARGUMENT_TASK_TYPE_TO_DISPLAY = "ARGUMENT_TASK_TYPE_TO_DISPLAY";
    public static final String TAG = HomeListFragment.class.getSimpleName();

    //DATA
    private List<TaskViewModel> mTasks = new ArrayList<>();
    private ViewPagerTaskDisplayType mReminderTypeToDisplay;
    private RemindyDAO mDao;
    private TaskSortType mTaskSortType = TaskSortType.DATE;

    //UI
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HomeAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private RelativeLayout mNoItemsContainer;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    public ActionMode mActionMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            mReminderTypeToDisplay = (ViewPagerTaskDisplayType) getArguments().getSerializable(ARGUMENT_TASK_TYPE_TO_DISPLAY);
        } catch (NullPointerException e) {
            Log.d(TAG, "Error! mReminderTypeToDisplay == null" + e.getMessage());
            SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, null);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_home_list_recycler);
        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_home_list_swipe_refresh);
        mNoItemsContainer = (RelativeLayout) rootView.findViewById(R.id.fragment_home_list_no_items_container);

        setUpRecyclerView();
        setUpSwipeRefresh();

        refreshRecyclerView();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        switch (mReminderTypeToDisplay) {
            case UNPROGRAMMED:
                inflater.inflate(R.menu.menu_home_no_sort, menu);
                break;
            case PROGRAMMED:
                inflater.inflate(R.menu.menu_home_sort, menu);
                break;
            case DONE:
                inflater.inflate(R.menu.menu_home_sort, menu);
                break;
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new HomeAdapter(this, mTasks);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setUpSwipeRefresh() {
        mSwipeRefresh.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                               @Override
                                               public void onRefresh() {
                                                   refreshRecyclerView();
                                                   mSwipeRefresh.setRefreshing(false);
                                               }
                                           }
        );
    }

    public void setSortTypeAndRefresh(TaskSortType taskSortType) {
        mTaskSortType = taskSortType;
        refreshRecyclerView();
    }

    public void refreshRecyclerView() {

        if (mDao == null)
            mDao = new RemindyDAO(getActivity().getApplicationContext());

        mTasks.clear();

        try {
            switch (mReminderTypeToDisplay) {
                case UNPROGRAMMED:
                    mTasks.addAll(mDao.getUnprogrammedTasks());
                    break;

                case PROGRAMMED:
                    mTasks.addAll(mDao.getProgrammedTasks(mTaskSortType, true, getResources()));
                    break;

                case DONE:
                    mTasks.addAll(mDao.getDoneTasks(mTaskSortType, getResources()));
            }
        } catch (CouldNotGetDataException | InvalidClassException e) {
            Log.d(TAG, "Error fetching data from db for recyclerView: " + e.getMessage());
            SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_getting_tasks_from_database, SnackbarUtil.SnackbarDuration.LONG, null);
        }

        mAdapter.notifyDataSetChanged();

        toggleNoItemsContainer();
    }

    private void toggleNoItemsContainer() {
        if (mTasks.size() == 0) {
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoItemsContainer.setVisibility(View.GONE);
        }
    }

    public void updateViewholderItem(int position) {
        try {
            Task task = mDao.getTask(mTasks.get(position).getTask().getId());
            TaskViewModel taskViewModel = new TaskViewModel(task, ConversionUtil.taskReminderTypeToTaskViewmodelType(task.getReminderType()));
            mTasks.set(position, taskViewModel);
            mAdapter.notifyItemChanged(position);
        } catch (CouldNotGetDataException e) {
            SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_updating_task_from_database, SnackbarUtil.SnackbarDuration.LONG, null);
        }
    }

    public void removeViewHolderItem(int position) {
        mTasks.remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    @Override
    public void onItemClicked(int position, @Nullable Intent optionalIntent, @Nullable Bundle optionalBundle) {
        if (mActionMode != null) {
            toggleSelection(position);
        } else {
            getActivity().startActivityForResult(optionalIntent, TaskDetailActivity.TASK_DETAIL_REQUEST_CODE, optionalBundle);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        }

        toggleSelection(position);

        return true;
    }


    private class ActionModeCallback implements ActionMode.Callback {

        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_home_contextual, menu);
            menu.findItem(R.id.home_contextual_done).setVisible((mReminderTypeToDisplay == ViewPagerTaskDisplayType.PROGRAMMED));
            menu.findItem(R.id.home_contextual_not_done).setVisible((mReminderTypeToDisplay == ViewPagerTaskDisplayType.DONE));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home_contextual_delete:
                    try {
                        for (int i : mAdapter.getSelectedItems())
                            mDao.deleteTask(mTasks.get(i).getTask().getId());

                        mAdapter.removeItems(mAdapter.getSelectedItems());
                        mode.finish();
                        toggleNoItemsContainer();
                    } catch (CouldNotDeleteDataException e) {
                        SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_deleting_tasks_from_database, SnackbarUtil.SnackbarDuration.LONG, null);
                    }
                    return true;

                case R.id.home_contextual_done:
                    try {
                        for (int i : mAdapter.getSelectedItems()) {
                            Task taskToUpdate = mTasks.get(i).getTask();
                            taskToUpdate.setStatus(TaskStatus.DONE);
                            taskToUpdate.setDoneDate(CalendarUtil.getNewInstanceZeroedCalendar());
                            mDao.updateTask(taskToUpdate);
                        }

                        mAdapter.removeItems(mAdapter.getSelectedItems());
                        mode.finish();
                        toggleNoItemsContainer();
                    } catch (CouldNotUpdateDataException e) {
                        SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_deleting_tasks_from_database, SnackbarUtil.SnackbarDuration.LONG, null);
                    }
                    return true;

                case R.id.home_contextual_not_done:
                    try {
                        for (int i : mAdapter.getSelectedItems()) {
                            Task taskToUpdate = mTasks.get(i).getTask();
                            taskToUpdate.setStatus((taskToUpdate.getReminderType() == ReminderType.NONE ? TaskStatus.UNPROGRAMMED : TaskStatus.PROGRAMMED));
                            taskToUpdate.setDoneDate(null);
                            mDao.updateTask(taskToUpdate);
                        }

                        mAdapter.removeItems(mAdapter.getSelectedItems());
                        mode.finish();
                        toggleNoItemsContainer();
                    } catch (CouldNotUpdateDataException e) {
                        SnackbarUtil.showSnackbar(mRecyclerView, SnackbarUtil.SnackbarType.ERROR, R.string.error_problem_deleting_tasks_from_database, SnackbarUtil.SnackbarDuration.LONG, null);
                    }
                    return true;

                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            mActionMode = null;
        }
    }
}
