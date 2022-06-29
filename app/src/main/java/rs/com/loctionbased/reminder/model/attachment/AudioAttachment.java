package rs.com.loctionbased.reminder.model.attachment;

import rs.com.loctionbased.reminder.enums.AttachmentType;


public class AudioAttachment extends Attachment {

    private String audioFilename;

    public AudioAttachment() { }
    public AudioAttachment(int id, int reminderId, String audioFilename) {
        super(id, reminderId);
        this.audioFilename = audioFilename;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.AUDIO;
    }

    public String getAudioFilename() {
        return audioFilename;
    }
    public void setAudioFilename(String audioFilename) {
        this.audioFilename = audioFilename;
    }
}
