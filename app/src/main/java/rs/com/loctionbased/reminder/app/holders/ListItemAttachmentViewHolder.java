package rs.com.loctionbased.reminder.app.holders;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.adapters.ListItemAttachmentAdapter;
import rs.com.loctionbased.reminder.app.dialogs.EditListItemAttachmentDialogFragment;
import rs.com.loctionbased.reminder.model.attachment.ListItemAttachment;
import rs.com.loctionbased.reminder.util.ClipboardUtil;

public class ListItemAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, EditListItemAttachmentDialogFragment.EditListItemAttachmentDialogDismissListener {

    private ListItemAttachmentAdapter mAdapter;
    private Activity mActivity;

    //UI
    private LinearLayout mContainer;
    private CheckBox mCheckBox;
    private ImageView mTapToAdd;
    private TextView mText;

    //DATA
    private ListItemAttachment mCurrent;
    private int mPosition;
    private boolean mRealTimeDataPersistence;

    public ListItemAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_list_item_container);
        mCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_attachment_list_item_checkbox);
        mTapToAdd = (ImageView) itemView.findViewById(R.id.list_item_attachment_list_item_tap_to_add);
        mText = (TextView) itemView.findViewById(R.id.list_item_attachment_list_item_text);
    }


    public void setData(ListItemAttachmentAdapter adapter, Activity activity, ListItemAttachment current, int position, boolean realTimeDataPersistence) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;
        mRealTimeDataPersistence = realTimeDataPersistence;

        setupViewHolder();
    }

    private void setupViewHolder() {
        mContainer.setOnClickListener(null);
        mContainer.setOnLongClickListener(null);

        if(mCurrent.getText() == null || mCurrent.getText().isEmpty()) {
            mCheckBox.setVisibility(View.GONE);
            mTapToAdd.setVisibility(View.VISIBLE);

            mContainer.setOnClickListener(this);

            mText.setText("");
            mCheckBox.setChecked(false);
        } else {
            mCheckBox.setVisibility(View.VISIBLE);
            mTapToAdd.setVisibility(View.GONE);

            mContainer.setOnLongClickListener(this);


            mText.setText(mCurrent.getText());
            mCheckBox.setChecked(mCurrent.isChecked());

            mCheckBox.setOnClickListener(this);
        }
    }


    public void setListeners() { /*Done in setData*/ }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_list_item_container:
                CharSequence items[] = new CharSequence[]{
                        mActivity.getResources().getString(R.string.dialog_list_item_attachment_options_copy),
                        mActivity.getResources().getString(R.string.dialog_list_item_attachment_options_edit),
                        mActivity.getResources().getString(R.string.dialog_list_item_attachment_options_delete)};

                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        ClipboardUtil.copyToClipboard(mActivity, mCurrent.getText());
                                        break;

                                    case 1:
                                        handleListItemEdit(false);
                                        break;

                                    case 2:
                                        mAdapter.deleteItem(mPosition);
                                        if (mRealTimeDataPersistence)
                                            mAdapter.triggerAttachmentDataUpdatedListener();
                                        break;
                                }

                            }
                        })
                        .create();
                dialog.show();
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.list_item_attachment_list_item_checkbox:
                mCurrent.setChecked(!mCurrent.isChecked());
                if(mRealTimeDataPersistence)
                    mAdapter.triggerAttachmentDataUpdatedListener();
                break;

            case R.id.item_attachment_list_item_container:
                handleListItemEdit(true);
                break;
        }
    }


    private void handleListItemEdit(boolean isANewItem){
        FragmentManager fm = ((AppCompatActivity)mActivity).getSupportFragmentManager();

        EditListItemAttachmentDialogFragment dialog = EditListItemAttachmentDialogFragment.newInstance(isANewItem, mText.getText().toString());
        dialog.setListener(this);
        dialog.show(fm, "EditLinkAttachmentDialogFragment");
    }


    @Override
    public void onFinishEditListItemAttachmentDialog(String text, boolean isANewItem) {
        mCurrent.setText(text);
        mText.setText(text);
        setupViewHolder();
        if(isANewItem)
            mAdapter.insertNewBlankItem();

        if(mRealTimeDataPersistence)
            mAdapter.triggerAttachmentDataUpdatedListener();
    }


}
