package rs.com.loctionbased.reminder.util;

import java.util.Iterator;
import java.util.List;

import rs.com.loctionbased.reminder.model.attachment.Attachment;
import rs.com.loctionbased.reminder.model.attachment.AudioAttachment;
import rs.com.loctionbased.reminder.model.attachment.ImageAttachment;
import rs.com.loctionbased.reminder.model.attachment.LinkAttachment;
import rs.com.loctionbased.reminder.model.attachment.ListAttachment;
import rs.com.loctionbased.reminder.model.attachment.ListItemAttachment;
import rs.com.loctionbased.reminder.model.attachment.TextAttachment;

public class AttachmentUtil {

    public static void cleanInvalidAttachments(List<Attachment> attachments) {

        Iterator<Attachment> attachmentIterator = attachments.iterator();
        while(attachmentIterator.hasNext()) {

            Attachment attachment = attachmentIterator.next();
            switch (attachment.getType()) {
                case LINK:
                    if (((LinkAttachment) attachment).getLink() == null || ((LinkAttachment) attachment).getLink().isEmpty())
                        attachmentIterator.remove();
                    break;

                case TEXT:
                    if (((TextAttachment) attachment).getText() == null || ((TextAttachment) attachment).getText().isEmpty())
                        attachmentIterator.remove();
                    break;

                case AUDIO:
                    if (((AudioAttachment) attachment).getAudioFilename() == null || ((AudioAttachment) attachment).getAudioFilename().isEmpty())
                        attachmentIterator.remove();
                    break;

                case IMAGE:
                    if (((ImageAttachment) attachment).getImageFilename() == null || ((ImageAttachment) attachment).getImageFilename().isEmpty())
                        attachmentIterator.remove();
                    break;

                case LIST:
                    Iterator<ListItemAttachment> i = ((ListAttachment) attachment).getItems().iterator();
                    while (i.hasNext()) {
                        ListItemAttachment item = i.next();
                        if (item.getText() == null || item.getText().isEmpty())
                            i.remove();
                    }

                    if (((ListAttachment) attachment).getItems().size() == 0) {
                        attachmentIterator.remove();
                    }
                    break;
            }
        }
    }

}
