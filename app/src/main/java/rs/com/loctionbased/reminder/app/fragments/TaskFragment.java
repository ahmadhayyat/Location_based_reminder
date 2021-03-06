package rs.com.loctionbased.reminder.app.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;

import java.util.List;

import rs.com.loctionbased.reminder.Customization.FontsOverride;
import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.adapters.AttachmentAdapter;
import rs.com.loctionbased.reminder.app.interfaces.TaskDataInterface;
import rs.com.loctionbased.reminder.enums.TaskCategory;
import rs.com.loctionbased.reminder.model.Task;
import rs.com.loctionbased.reminder.model.attachment.Attachment;
import rs.com.loctionbased.reminder.model.attachment.AudioAttachment;
import rs.com.loctionbased.reminder.model.attachment.ImageAttachment;
import rs.com.loctionbased.reminder.model.attachment.LinkAttachment;
import rs.com.loctionbased.reminder.model.attachment.ListAttachment;
import rs.com.loctionbased.reminder.model.attachment.TextAttachment;
import rs.com.loctionbased.reminder.util.ConversionUtil;
import rs.com.loctionbased.reminder.util.SnackbarUtil;

import static androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED;

public class TaskFragment extends Fragment implements View.OnClickListener, TaskDataInterface {

    //TYPE FACE
    Typeface ProximaNovaReg, ProximaNovaBold;

    //CONST
    private static final String TAG = TaskFragment.class.getSimpleName();
    public static final String TASK_ARGUMENT = "TASK_ARGUMENT";

    //DATA
    private List<String> mTaskCategories;
    private boolean mAddAttachmentHintVisible;
    private boolean mHeadersVisible;
    private boolean mAttachmentLongClickOptionsDialogHintShown;
    private Task mTask = new Task();
    private AttachmentAdapter mAdapter;

    //UI
    private RelativeLayout mHeaderBasicInfo;
    private RelativeLayout mHeaderAttachments;
    private RelativeLayout mContainer;
    private LinearLayout mContainerBasicInfo;
    private TextView mTaskTitle;
    private TextView mTaskDescription;
    private Spinner mTaskCategory;
    private TextView mAttachmentsFabHint;
    private FloatingActionMenu mAttachmentsFabMenu;
    private FloatingActionButton mAttachmentsFabList;
    private FloatingActionButton mAttachmentsFabText;
    private FloatingActionButton mAttachmentsFabLink;
    private FloatingActionButton mAttachmentsFabImage;
    private FloatingActionButton mAttachmentsFabAudio;

    public RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mNoItemsContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);

        FontsOverride.setDefaultFont(getActivity(), "DEFAULT", "fonts/ProximaNovaReg.ttf");

        if (getArguments().containsKey(TASK_ARGUMENT)) {
            mTask = (Task) getArguments().get(TASK_ARGUMENT);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    getActivity().setResult(RESULT_CANCELED);
                    getActivity().finish();
                }
            };
            Log.e(TAG, "Missing TASK_ARGUMENT argument in TaskFragment.");
            SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.ERROR, R.string.error_unexpected, SnackbarUtil.SnackbarDuration.LONG, callback);
        }

        ProximaNovaReg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNovaReg.ttf");
        ProximaNovaBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNovaBold.ttf");

        mHeaderBasicInfo = (RelativeLayout) rootView.findViewById(R.id.fragment_task_header_basic_info);
        ((TextView) mHeaderBasicInfo.findViewById(R.id.item_task_header_title)).setText(R.string.fragment_task_header_basic_info);

        mHeaderAttachments = (RelativeLayout) rootView.findViewById(R.id.fragment_task_header_attachments);
        ((TextView) mHeaderAttachments.findViewById(R.id.item_task_header_title)).setText(R.string.fragment_task_header_attachments);

        mContainer = (RelativeLayout) rootView.findViewById(R.id.fragment_task_container);
        mContainerBasicInfo = (LinearLayout) rootView.findViewById(R.id.fragment_task_basic_info_container);
        mTaskTitle = (TextView) rootView.findViewById(R.id.fragment_task_title);
        mTaskTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAttachmentsFabMenu.close(true);
                }
            }
        });
        mTaskDescription = (TextView) rootView.findViewById(R.id.fragment_task_description);
        mTaskDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAttachmentsFabMenu.close(true);
                }
            }
        });
        mTaskCategory = (Spinner) rootView.findViewById(R.id.fragment_task_category);
        mAttachmentsFabHint = (TextView) rootView.findViewById(R.id.fragment_task_add_attachment_hint);
        if (mTask.getAttachments().size() == 0) {
            mAttachmentsFabHint.setVisibility(View.VISIBLE);
            mAddAttachmentHintVisible = true;
        } else {
            fadeInHeaders();
            mHeadersVisible = true;
        }

        mAttachmentsFabMenu = (FloatingActionMenu) rootView.findViewById(R.id.fragment_task_add_attachment);
        mAttachmentsFabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {

                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mContainer.getWindowToken(), 0);

                if (mAddAttachmentHintVisible) {
                    TransitionManager.beginDelayedTransition(mContainer, new Slide(Gravity.START));
                    mAttachmentsFabHint.setVisibility(View.INVISIBLE);
                }
            }
        });
        mAttachmentsFabList = (FloatingActionButton) rootView.findViewById(R.id.fragment_task_add_list_attachment);
        mAttachmentsFabText = (FloatingActionButton) rootView.findViewById(R.id.fragment_task_add_text_attachment);
        mAttachmentsFabLink = (FloatingActionButton) rootView.findViewById(R.id.fragment_task_add_link_attachment);
        mAttachmentsFabImage = (FloatingActionButton) rootView.findViewById(R.id.fragment_task_add_image_attachment);
        mAttachmentsFabAudio = (FloatingActionButton) rootView.findViewById(R.id.fragment_task_add_audio_attachment);

        mAttachmentsFabList.setOnClickListener(this);
        mAttachmentsFabText.setOnClickListener(this);
        mAttachmentsFabLink.setOnClickListener(this);
        mAttachmentsFabImage.setOnClickListener(this);
        mAttachmentsFabAudio.setOnClickListener(this);

        //Hide keyboard
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mContainer.getWindowToken(), 0);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_task_recycler);
        mNoItemsContainer = (RelativeLayout) rootView.findViewById(R.id.fragment_task_no_items_container);

        setUpRecyclerView();
        setupSpinners();
        setTaskValues();

        return rootView;
    }

    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mAdapter = new AttachmentAdapter(getActivity(), mTask.getAttachments(), false);
        mAdapter.setShowAttachmentHintListener(new AttachmentAdapter.ShowAttachmentHintListener() {
            @Override
            public void onShowAttachmentHint() {
                if (!mAttachmentLongClickOptionsDialogHintShown) {
                    mAttachmentLongClickOptionsDialogHintShown = true;
                    SnackbarUtil.showSnackbar(mContainer, SnackbarUtil.SnackbarType.NOTICE, R.string.fragment_task_snackbar_notice_attachments_options_hint, SnackbarUtil.SnackbarDuration.LONG, null);
                }
            }
        });
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.item_decoration_half_line));

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupSpinners() {
        mTaskCategories = TaskCategory.getFriendlyValues(getActivity());
        ArrayAdapter reminderCategoryAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, mTaskCategories);
        reminderCategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mTaskCategory.setAdapter(reminderCategoryAdapter);
    }

    private void setTaskValues() {
        mTaskTitle.setText(mTask.getTitle());
        mTaskDescription.setText(mTask.getDescription());
        if (mTask.getCategory() != null)
            mTaskCategory.setSelection(mTask.getCategory().ordinal());
    }


    private void addAttachment(Attachment attachment) {
        mTask.addAttachment(attachment);
        if (mAdapter.getItemCount() == 1)
            mAdapter.notifyDataSetChanged();
        else
            mAdapter.notifyItemInserted(mAdapter.getItemCount());
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        mAttachmentsFabMenu.close(true);

        if (!mHeadersVisible) {
            fadeInHeaders();
            mHeadersVisible = true;
        }

        switch (id) {
            case R.id.fragment_task_add_list_attachment:
                addAttachment(new ListAttachment());
                break;

            case R.id.fragment_task_add_text_attachment:
                addAttachment(new TextAttachment(""));
                break;

            case R.id.fragment_task_add_link_attachment:
                addAttachment(new LinkAttachment(""));
                break;

            case R.id.fragment_task_add_image_attachment:
                addAttachment(new ImageAttachment());
                break;

            case R.id.fragment_task_add_audio_attachment:
                addAttachment(new AudioAttachment());
                break;
        }
        //Scroll to added item
        if (mAdapter.getItemCount() > 0)
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }


    private void fadeInHeaders() {
        //Fade in headers
        TransitionManager.beginDelayedTransition(mContainer);
        mHeaderBasicInfo.setVisibility(View.VISIBLE);
        mHeaderAttachments.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mContainerBasicInfo.getLayoutParams();
        lp.setMargins(ConversionUtil.dpToPx(16, getResources()), 0, 0, 0);
        mContainerBasicInfo.setLayoutParams(lp);
    }


    @Override
    public void updateData() {
        TaskCategory category = TaskCategory.values()[mTaskCategory.getSelectedItemPosition()];
        mTask.setCategory(category);
        mTask.setTitle(mTaskTitle.getText().toString().trim());
        mTask.setDescription(mTaskDescription.getText().toString().trim());
    }

}
