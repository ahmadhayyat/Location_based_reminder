package rs.com.loctionbased.reminder.model.attachment;

import rs.com.loctionbased.reminder.enums.AttachmentType;

public class ImageAttachment extends Attachment {

    private byte[] thumbnail;
    private String imageFilename;

    public ImageAttachment() { }
    public ImageAttachment(byte[] thumbnail, String imageFilename) {
        this.thumbnail = thumbnail;
        this.imageFilename = imageFilename;
    }
    public ImageAttachment(int id, int reminderId, byte[] thumbnail, String fullImagePath) {
        super(id, reminderId);
        this.thumbnail = thumbnail;
        this.imageFilename = fullImagePath;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.IMAGE;
    }


    public byte[] getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImageFilename() {
        return imageFilename;
    }
    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
}
