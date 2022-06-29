package rs.com.loctionbased.reminder.app.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;
import java.util.List;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.app.holders.AudioAttachmentViewHolder;
import rs.com.loctionbased.reminder.app.holders.ImageAttachmentViewHolder;
import rs.com.loctionbased.reminder.app.holders.LinkAttachmentViewHolder;
import rs.com.loctionbased.reminder.app.holders.ListAttachmentViewHolder;
import rs.com.loctionbased.reminder.app.holders.TextAttachmentViewHolder;
import rs.com.loctionbased.reminder.enums.AttachmentType;
import rs.com.loctionbased.reminder.model.attachment.Attachment;
import rs.com.loctionbased.reminder.model.attachment.AudioAttachment;
import rs.com.loctionbased.reminder.model.attachment.ImageAttachment;
import rs.com.loctionbased.reminder.model.attachment.LinkAttachment;
import rs.com.loctionbased.reminder.model.attachment.ListAttachment;
import rs.com.loctionbased.reminder.model.attachment.TextAttachment;
import rs.com.loctionbased.reminder.util.FileUtil;

public class AttachmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //DATA
    private List<Attachment> mAttachments;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private ShowAttachmentHintListener showAttachmentHintListener;
    private AttachmentDataUpdatedListener attachmentDataUpdatedListener;
    private boolean mRealTimeDataPersistence;

    public AttachmentAdapter(Activity activity, List<Attachment> attachments, boolean realTimeDataPersistence) {
        mActivity = activity;
        mAttachments = attachments;
        mRealTimeDataPersistence = realTimeDataPersistence;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        AttachmentType attachmentType = AttachmentType.values()[viewType];
        switch (attachmentType) {
            case TEXT:
                return new TextAttachmentViewHolder(mInflater.inflate(R.layout.list_item_attachment_text, parent, false));
            case LIST:
                return new ListAttachmentViewHolder(mInflater.inflate(R.layout.list_item_attachment_list, parent, false));
            case LINK:
                return new LinkAttachmentViewHolder(mInflater.inflate(R.layout.list_item_attachment_link, parent, false));
            case AUDIO:
                return new AudioAttachmentViewHolder(mInflater.inflate(R.layout.list_item_attachment_audio, parent, false));
            case IMAGE:
                return new ImageAttachmentViewHolder(mInflater.inflate(R.layout.list_item_attachment_image, parent, false));
            default:
                throw new InvalidParameterException("Wrong viewType passed to onCreateViewHolder in AttachmentAdapter");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mAttachments.get(position).getType().ordinal();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Attachment current = mAttachments.get(position);

        switch (current.getType()) {
            case TEXT:
                TextAttachmentViewHolder tvh = (TextAttachmentViewHolder) holder;
                tvh.setData(this, mActivity, (TextAttachment)current, position, mRealTimeDataPersistence);
                tvh.setListeners();
                break;
            case LIST:
                ListAttachmentViewHolder listvh = (ListAttachmentViewHolder) holder;
                listvh.setData(this, mActivity, (ListAttachment) current, position, mRealTimeDataPersistence);
                listvh.setListeners();
                break;

            case LINK:
                LinkAttachmentViewHolder lvh = (LinkAttachmentViewHolder) holder;
                lvh.setData(this, mActivity, (LinkAttachment)current, position, mRealTimeDataPersistence);
                lvh.setListeners();
                break;

            case AUDIO:
                AudioAttachmentViewHolder avh = (AudioAttachmentViewHolder) holder;
                avh.setData(this, mActivity, (AudioAttachment) current, position, mRealTimeDataPersistence);
                break;

            case IMAGE:
                ImageAttachmentViewHolder ivh = (ImageAttachmentViewHolder) holder;
                ivh.setData(this, mActivity, (ImageAttachment) current, position, mRealTimeDataPersistence);
                ivh.setListeners();
                break;

            default:
                throw new InvalidParameterException("Wrong viewType passed to onCreateViewHolder in AttachmentAdapter");
        }
    }

    @Override
    public int getItemCount() {
        return mAttachments.size();
    }


    public void deleteAttachment(int position) {

        Attachment attachment = mAttachments.get(position);
        switch (attachment.getType()) {
            case AUDIO:
                String audioFilename = ((AudioAttachment)attachment).getAudioFilename();
                FileUtil.deleteAudioAttachment(mActivity, audioFilename);
                break;
            case IMAGE:
                String imageFilename = ((ImageAttachment)attachment).getImageFilename();
                FileUtil.deleteImageAttachment(mActivity, imageFilename);
        }

        mAttachments.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        if(mRealTimeDataPersistence) {
            triggerAttachmentDataUpdatedListener();
        }

    }



    public void setAttachmentDataUpdatedListener(AttachmentDataUpdatedListener listener) {
        this.attachmentDataUpdatedListener = listener;
    }
    public void triggerAttachmentDataUpdatedListener() {
        if(attachmentDataUpdatedListener != null)
            attachmentDataUpdatedListener.onAttachmentDataUpdated();
    }
    public interface AttachmentDataUpdatedListener {
        void onAttachmentDataUpdated();
    }


    public void setShowAttachmentHintListener(ShowAttachmentHintListener listener) {
        this.showAttachmentHintListener = listener;
    }
    public void triggerShowAttachmentHintListener() {
        if(showAttachmentHintListener != null)
            showAttachmentHintListener.onShowAttachmentHint();
    }
    public interface ShowAttachmentHintListener {
        void onShowAttachmentHint();
    }
}
