package rs.com.loctionbased.reminder.model.attachment;

import java.io.Serializable;


public class ListItemAttachment implements Serializable {

    private String text;
    private boolean checked;

    public ListItemAttachment() {
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
