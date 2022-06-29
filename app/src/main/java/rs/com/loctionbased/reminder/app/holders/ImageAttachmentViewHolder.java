package rs.com.loctionbased.reminder.app.holders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.SharedClass;
import rs.com.loctionbased.reminder.app.activities.EditImageAttachmentActivity;
import rs.com.loctionbased.reminder.app.activities.ViewImageAttachmentActivity;
import rs.com.loctionbased.reminder.app.adapters.AttachmentAdapter;
import rs.com.loctionbased.reminder.model.attachment.ImageAttachment;

public class ImageAttachmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    //CONSTS
    private static final String TAG = ImageAttachmentViewHolder.class.getSimpleName();

    //UI
    private LinearLayout mContainer;
    private ImageView mImage;
    private LinearLayout mTapToAddContainer;
    private AttachmentAdapter mAdapter;
    private Activity mActivity;

    //DATA
    private ImageAttachment mCurrent;
    private int mPosition;
    private boolean mRealTimeDataPersistence;

    public ImageAttachmentViewHolder(View itemView) {
        super(itemView);

        mContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_image_container);
        mImage = (ImageView) itemView.findViewById(R.id.item_attachment_image_content);
        mTapToAddContainer = (LinearLayout) itemView.findViewById(R.id.item_attachment_image_tap_to_add_container);
    }

    public void setData(AttachmentAdapter adapter, Activity activity, ImageAttachment current, int position, boolean realTimeDataPersistence) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;
        mRealTimeDataPersistence = realTimeDataPersistence;

        mContainer.setOnLongClickListener(this);
        mTapToAddContainer.setOnClickListener(this);
        mImage.setOnClickListener(this);

        setupViewHolder();

        if(mCurrent.getImageFilename() == null)
            launchImageEditAttachmentActivity();
    }

    private void setupViewHolder() {
        if(mCurrent.getImageFilename() == null) {
            mTapToAddContainer.setVisibility(View.VISIBLE);
            mImage.setVisibility(View.GONE);
        } else {
            mTapToAddContainer.setVisibility(View.GONE);
            mImage.setVisibility(View.VISIBLE);
            mImage.setImageBitmap(BitmapFactory.decodeFile(SharedClass.picturePath));
        }
    }

    public void setListeners() {
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_image_tap_to_add_container:
                launchImageEditAttachmentActivity();
                break;

            case R.id.item_attachment_image_content:
                launchImageViewAttachmentActivity();
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.item_attachment_image_container:
                CharSequence items[] = new CharSequence[] {
                        mActivity.getResources().getString(R.string.dialog_image_attachment_options_edit),
                        mActivity.getResources().getString(R.string.dialog_image_attachment_options_delete)};


                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0)
                                    launchImageEditAttachmentActivity();
                                else if(which == 1)
                                    mAdapter.deleteAttachment(mPosition);
                            }
                        })
                        .create();
                dialog.show();
                return true;
        }
        return false;
    }


    public void updateImageAttachment(ImageAttachment imageAttachment){
        mCurrent.setThumbnail(imageAttachment.getThumbnail());
        mCurrent.setImageFilename(imageAttachment.getImageFilename());
        setupViewHolder();
        mAdapter.triggerShowAttachmentHintListener();

        if(mRealTimeDataPersistence)
            mAdapter.triggerAttachmentDataUpdatedListener();
    }

    private void launchImageEditAttachmentActivity() {
        Intent goToEditImageAttachmentActivity = new Intent(mActivity, EditImageAttachmentActivity.class);
        goToEditImageAttachmentActivity.putExtra(EditImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA, mCurrent);
        goToEditImageAttachmentActivity.putExtra(EditImageAttachmentActivity.HOLDER_POSITION_EXTRA, mPosition);
        mActivity.startActivityForResult(goToEditImageAttachmentActivity, EditImageAttachmentActivity.EDIT_IMAGE_ATTACHMENT_REQUEST_CODE);
    }

    private void launchImageViewAttachmentActivity() {

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(mImage, mActivity.getResources().getString(R.string.transition_image_attachment_image));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, pairs);

        Intent goToViewImageAttachmentActivity = new Intent(mActivity, ViewImageAttachmentActivity.class);
        goToViewImageAttachmentActivity.putExtra(ViewImageAttachmentActivity.IMAGE_ATTACHMENT_EXTRA, mCurrent);
        goToViewImageAttachmentActivity.putExtra(ViewImageAttachmentActivity.HOLDER_POSITION_EXTRA, mPosition);
        mActivity.startActivityForResult(goToViewImageAttachmentActivity, ViewImageAttachmentActivity.VIEW_IMAGE_ATTACHMENT_REQUEST_CODE, options.toBundle());
    }

}
