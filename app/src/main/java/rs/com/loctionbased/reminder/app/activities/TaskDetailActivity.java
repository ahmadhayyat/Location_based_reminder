package rs.com.loctionbased.reminder.app.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.transitionseverywhere.TransitionManager;

import java.util.Calendar;
import java.util.Locale;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.adapters.AttachmentAdapter;
import rs.com.loctionbased.reminder.app.fragments.LocationBasedReminderDetailFragment;
import rs.com.loctionbased.reminder.app.holders.ImageAttachmentViewHolder;
import rs.com.loctionbased.reminder.database.RemindyDAO;
import rs.com.loctionbased.reminder.enums.DateFormat;
import rs.com.loctionbased.reminder.enums.ReminderType;
import rs.com.loctionbased.reminder.enums.TaskStatus;
import rs.com.loctionbased.reminder.exception.CouldNotDeleteDataException;
import rs.com.loctionbased.reminder.exception.CouldNotGetDataException;
import rs.com.loctionbased.reminder.exception.CouldNotUpdateDataException;
import rs.com.loctionbased.reminder.model.Task;
import rs.com.loctionbased.reminder.model.Time;
import rs.com.loctionbased.reminder.model.attachment.Attachment;
import rs.com.loctionbased.reminder.model.attachment.AudioAttachment;
import rs.com.loctionbased.reminder.model.attachment.ImageAttachment;
import rs.com.loctionbased.reminder.model.attachment.LinkAttachment;
import rs.com.loctionbased.reminder.model.attachment.ListAttachment;
import rs.com.loctionbased.reminder.model.attachment.TextAttachment;
import rs.com.loctionbased.reminder.util.AlarmManagerUtil;
import rs.com.loctionbased.reminder.util.AttachmentUtil;
import rs.com.loctionbased.reminder.util.CalendarUtil;
import rs.com.loctionbased.reminder.util.FileUtil;
import rs.com.loctionbased.reminder.util.GeofenceUtil;
import rs.com.loctionbased.reminder.util.SharedPreferenceUtil;
import rs.com.loctionbased.reminder.util.SnackbarUtil;
import rs.com.loctionbased.reminder.util.TaskUtil;

public class TaskDetailActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    //CONST
    public static final String TASK_ID_TO_DISPLAY = "TASK_ID_TO_DISPLAY";
    public static final String TASK_POSITION = "TASK_POSITION";

    public static final int TASK_DETAIL_REQUEST_CODE = 491;
    public static final String TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX = "TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX";
    public static final String TASK_DETAIL_RETURN_TASK_POSITION = "TASK_DETAIL_RETURN_TASK_POSITION";
    public static final String TASK_DETAIL_RETURN_TASK_TASK = "TASK_DETAIL_RETURN_TASK_TASK";
    public static final String TASK_DETAIL_RETURN_ACTION_TYPE = "TASK_DETAIL_RETURN_ACTION_TYPE";
    public static final int TASK_DETAIL_RETURN_ACTION_DELETED = 920;
    public static final int TASK_DETAIL_RETURN_ACTION_EDITED = 921;
    public static final int TASK_DETAIL_RETURN_ACTION_EDITED_REMINDER = 922;

    //DATA
    private Task mTask;
    private String mOldReminderJson;
    private int mPosition;
    private DateFormat mDateFormat;
    private boolean mTaskDataUpdated = false;
    private boolean mUpdateGeofences = false;
    private GoogleApiClient mGoogleApiClient;


    //UI
    private Toolbar mToolbar;
    private LinearLayout mContainer;
    private ImageView mCategory;
    private TextView mTitle;
    private TextView mDescription;
    private TextView mReminderSubtitle;
    private TextView mAttachmentsSubtitle;
    private LinearLayout mDoneContainer;
    private TextView mDone;
    private LinearLayout mOverdueContainer;
    private TextView mOverdue;
    private ImageButton mDoneButton;
    private FloatingActionMenu mAttachmentsFabMenu;
    private FloatingActionButton mAttachmentsFabList;
    private FloatingActionButton mAttachmentsFabText;
    private FloatingActionButton mAttachmentsFabLink;
    private FloatingActionButton mAttachmentsFabImage;
    private FloatingActionButton mAttachmentsFabAudio;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AttachmentAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        mContainer = (LinearLayout) findViewById(R.id.activity_task_detail_container);


        if(getIntent().hasExtra(TASK_ID_TO_DISPLAY)) {
            int taskId = getIntent().getIntExtra(TASK_ID_TO_DISPLAY, -1);
            try {
                mTask = new RemindyDAO(getApplicationContext()).getTask(taskId);
            } catch (CouldNotGetDataException e) {
                BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        finish();
                    }
                };
                SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_snackbar_error_no_task, SnackbarUtil.SnackbarDuration.LONG, callback);
                finish();
                return;
            }
            mOldReminderJson = new Gson().toJson(mTask.getReminder());
            mPosition = getIntent().getIntExtra(TASK_POSITION, -1);     //If intent came from a notification, expect position to be -1. This is normal.
            mUpdateGeofences = mTask.getReminderType().equals(ReminderType.LOCATION_BASED);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            };
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_snackbar_error_no_task, SnackbarUtil.SnackbarDuration.LONG, callback);
            finish();
            return;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mDateFormat = SharedPreferenceUtil.getDateFormat(getApplicationContext());

        mCategory = (ImageView) findViewById(R.id.activity_task_detail_category);
        mTitle = (TextView) findViewById(R.id.activity_task_detail_title);
        mDescription = (TextView) findViewById(R.id.activity_task_detail_description);
        mReminderSubtitle = (TextView) findViewById(R.id.activity_task_detail_reminder_subtitle);
        mAttachmentsSubtitle = (TextView) findViewById(R.id.activity_task_detail_attachments_subtitle);
        mDoneContainer = (LinearLayout) findViewById(R.id.activity_task_detail_done_container);
        mDone = (TextView) findViewById(R.id.activity_task_detail_done);
        mOverdueContainer = (LinearLayout) findViewById(R.id.activity_task_detail_overdue_container);
        mOverdue = (TextView) findViewById(R.id.activity_task_detail_overdue);
        mDoneButton = (ImageButton) findViewById(R.id.activity_task_detail_done_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_task_detail_recycler);

        mAttachmentsFabMenu = (FloatingActionMenu) findViewById(R.id.activity_task_detail_add_attachment);
        mAttachmentsFabList = (FloatingActionButton) findViewById(R.id.activity_task_detail_add_list_attachment);
        mAttachmentsFabText = (FloatingActionButton) findViewById(R.id.activity_task_detail_add_text_attachment);
        mAttachmentsFabLink = (FloatingActionButton) findViewById(R.id.activity_task_detail_add_link_attachment);
        mAttachmentsFabImage = (FloatingActionButton) findViewById(R.id.activity_task_detail_add_image_attachment);
        mAttachmentsFabAudio = (FloatingActionButton) findViewById(R.id.activity_task_detail_add_audio_attachment);

        mAttachmentsFabList.setOnClickListener(this);
        mAttachmentsFabText.setOnClickListener(this);
        mAttachmentsFabLink.setOnClickListener(this);
        mAttachmentsFabImage.setOnClickListener(this);
        mAttachmentsFabAudio.setOnClickListener(this);
        mDoneButton.setOnClickListener(this);

        setUpViews();
        setUpDoneOrOverdue();
        setUpToolbar();
        setUpReminderViews();
        setUpRecyclerView();
    }

    private void setUpViews() {
        mCategory.setImageResource(mTask.getCategory().getIconRes());
        mTitle.setText(mTask.getTitle());
        mDescription.setText(mTask.getDescription());
    }

    private void setUpDoneOrOverdue() {
        TransitionManager.beginDelayedTransition(mContainer);
        mDoneContainer.setVisibility(View.GONE);
        mOverdueContainer.setVisibility(View.GONE);
        mDoneButton.setColorFilter(ContextCompat.getColor(this, R.color.gray_500));

        if(mTask.getStatus().equals(TaskStatus.DONE)) {
            mDone.setText(mDateFormat.formatCalendar(mTask.getDoneDate()));
            mDoneContainer.setVisibility(View.VISIBLE);
            mDoneButton.setColorFilter(ContextCompat.getColor(this, R.color.fab_accept_green));
        } else if(TaskUtil.checkIfOverdue(mTask.getReminder())) {
            Calendar endCal = TaskUtil.getReminderEndCalendar(mTask.getReminder());
            mOverdue.setText(String.format(
                    Locale.getDefault(),
                    getResources().getString(R.string.activity_task_overdue_since),
                    mDateFormat.formatCalendar(endCal),
                    new Time( endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), SharedPreferenceUtil.getTimeFormat(this)).toString()
            ));
            mOverdueContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_task_detail_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.activity_task_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    private void setUpReminderViews() {

        Bundle bundle = new Bundle();
        Fragment fragment = null;

        switch (mTask.getReminderType()) {

            case LOCATION_BASED:
                fragment = new LocationBasedReminderDetailFragment();
                bundle.putSerializable(LocationBasedReminderDetailFragment.REMINDER_TO_DISPLAY, mTask.getReminder());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_task_detail_reminder_placeholder, fragment).commit();
                break;
        }

        if(fragment != null) {
            mReminderSubtitle.setText(getResources().getString(mTask.getReminderType().getFriendlyNameRes()));
            mReminderSubtitle.setVisibility(View.VISIBLE);
        }
    }

    private void setUpRecyclerView() {

        if(mTask.getAttachments().size() > 0) {
            mAttachmentsSubtitle.setVisibility(View.VISIBLE);

            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mAdapter = new AttachmentAdapter(this, mTask.getAttachments(), true);
            mAdapter.setAttachmentDataUpdatedListener(new AttachmentAdapter.AttachmentDataUpdatedListener() {
                @Override
                public void onAttachmentDataUpdated() {
                    mTaskDataUpdated = true;
                }
            });
            DividerItemDecoration itemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());
            itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_decoration_half_line));
            mRecyclerView.addItemDecoration(itemDecoration);
            mRecyclerView.setNestedScrollingEnabled(false);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mTaskDataUpdated) {
            try {

                //Clean attachments
                AttachmentUtil.cleanInvalidAttachments(mTask.getAttachments());

                //Save changes
                new RemindyDAO(this).updateTask(mTask);

                //Update geofences
                if(mUpdateGeofences || mTask.getReminderType().equals(ReminderType.LOCATION_BASED))
                    GeofenceUtil.updateGeofences(getApplicationContext(), mGoogleApiClient);

                //Update alarms
                if(mTask.getReminderType().equals(ReminderType.ONE_TIME) || mTask.getReminderType().equals(ReminderType.REPEATING)) {

                    //Remove task from triggeredTasks list
                    SharedPreferenceUtil.removeIdFromTriggeredTasks(getApplicationContext(), mTask.getId());

                    //Update alarms
                    AlarmManagerUtil.updateAlarms(getApplicationContext());
                }

                String newReminderJson = new Gson().toJson(mTask.getReminder());
                int taskDetailReturnActionType = (mOldReminderJson.equals(newReminderJson) ?
                        TASK_DETAIL_RETURN_ACTION_EDITED : TASK_DETAIL_RETURN_ACTION_EDITED_REMINDER);

                Intent returnIntent = new Intent();
                returnIntent.putExtra(TASK_DETAIL_RETURN_ACTION_TYPE, taskDetailReturnActionType);
                returnIntent.putExtra(TASK_DETAIL_RETURN_TASK_POSITION, mPosition);
                returnIntent.putExtra(TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX, getViewPagerIndexFromTask(mTask));
                setResult(RESULT_OK, returnIntent);
                supportFinishAfterTransition();

            }catch (CouldNotUpdateDataException e) {
                SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_snackbar_error_updating_task, SnackbarUtil.SnackbarDuration.LONG, null);
            }
        }
    }

    private void addAttachment(Attachment attachment) {
        mTaskDataUpdated = true;
        mTask.addAttachment(attachment);

        if(mAdapter == null)
            setUpRecyclerView();

        if(mAdapter.getItemCount() == 1)
            mAdapter.notifyDataSetChanged();
        else
            mAdapter.notifyItemInserted(mAdapter.getItemCount());

        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        mAttachmentsFabMenu.close(true);

        switch (id) {
            case R.id.activity_task_detail_add_list_attachment:
                addAttachment(new ListAttachment());
                break;

            case R.id.activity_task_detail_add_text_attachment:
                addAttachment(new TextAttachment(""));
                break;

            case R.id.activity_task_detail_add_link_attachment:
                addAttachment(new LinkAttachment(""));
                break;

            case R.id.activity_task_detail_add_image_attachment:
                addAttachment(new ImageAttachment());
                break;

            case R.id.activity_task_detail_add_audio_attachment:
                addAttachment(new AudioAttachment());

                break;

            case R.id.activity_task_detail_done_button:
                if(mTask.getDoneDate() == null) {
                    mTask.setDoneDate(CalendarUtil.getNewInstanceZeroedCalendar());
                    mTask.setStatus(TaskStatus.DONE);
                } else {
                    mTask.setDoneDate(null);
                    mTask.setStatus( (mTask.getReminderType() == ReminderType.NONE ? TaskStatus.UNPROGRAMMED : TaskStatus.PROGRAMMED) );

                }

                mTaskDataUpdated = true;
                mOldReminderJson = "!";
                setUpDoneOrOverdue();
                break;
        }

        if(mAdapter != null && mAdapter.getItemCount() > 0)
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_task_edit:
                Intent goToTaskActivityIntent = new Intent(getApplicationContext(), TaskActivity.class);
                goToTaskActivityIntent.putExtra(TaskActivity.TASK_TO_EDIT, mTask);
                startActivityForResult(goToTaskActivityIntent, TaskActivity.TASK_ACTIVITY_REQUEST_CODE);
                break;

            case R.id.menu_task_delete:
                handleDeleteTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EditImageAttachmentActivity.EDIT_IMAGE_ATTACHMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra(EditImageAttachmentActivity.HOLDER_POSITION_EXTRA, -1);
            ImageAttachment imageAttachment = (ImageAttachment) data.getSerializableExtra(EditImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA);
            if(position != -1) {
                ImageAttachmentViewHolder holder = (ImageAttachmentViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                if(holder != null)
                    holder.updateImageAttachment(imageAttachment);
            }
        }

        if(requestCode == ViewImageAttachmentActivity.VIEW_IMAGE_ATTACHMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra(ViewImageAttachmentActivity.HOLDER_POSITION_EXTRA, -1);
            ImageAttachment imageAttachment = (ImageAttachment) data.getSerializableExtra(ViewImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA);
            if(position != -1) {
                ImageAttachmentViewHolder holder = (ImageAttachmentViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                if(holder != null)
                    holder.updateImageAttachment(imageAttachment);
            }
        }

        if(requestCode == TaskActivity.TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            mTaskDataUpdated = true;
            mTask = (Task) data.getSerializableExtra(TaskActivity.TASK_TO_EDIT);

            setUpViews();
            setUpDoneOrOverdue();
            setUpToolbar();
            setUpReminderViews();
            setUpRecyclerView();
        }
    }


    public void handleDeleteTask() {
        androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_task_dialog_delete_title))
                .setMessage(getResources().getString(R.string.activity_task_dialog_delete_message))
                .setPositiveButton(getResources().getString(R.string.activity_task_dialog_delete_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            FileUtil.deleteAttachmentFiles(TaskDetailActivity.this, mTask.getAttachments());
                            new RemindyDAO(TaskDetailActivity.this).deleteTask(mTask.getId());

                            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);

                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra(TASK_DETAIL_RETURN_ACTION_TYPE, TASK_DETAIL_RETURN_ACTION_DELETED);
                                    returnIntent.putExtra(TASK_DETAIL_RETURN_TASK_POSITION, mPosition);
                                    returnIntent.putExtra(TASK_DETAIL_RETURN_TASK_VIEWPAGER_INDEX, getViewPagerIndexFromTask(mTask));
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }
                            };
                            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.SUCCESS, R.string.activity_task_snackbar_task_deleted_successfully, SnackbarUtil.SnackbarDuration.SHORT, callback);
                        }catch (CouldNotDeleteDataException e) {
                            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.activity_task_snackbar_error_deleting_task, SnackbarUtil.SnackbarDuration.LONG, null);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_task_dialog_delete_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private int getViewPagerIndexFromTask(Task task) {
        if(task.getStatus().equals(TaskStatus.UNPROGRAMMED))
            return 0;

        if(task.getStatus().equals(TaskStatus.DONE))
            return 2;

        return 1;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
