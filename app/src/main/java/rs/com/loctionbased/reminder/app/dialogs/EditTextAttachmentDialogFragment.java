package rs.com.loctionbased.reminder.app.dialogs;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import rs.com.loctionbased.reminder.R;

public class EditTextAttachmentDialogFragment extends DialogFragment implements View.OnClickListener {

    //UI
    private EditTextAttachmentDialogDismissListener mListener;
    private EditText mText;
    private Button mCancel;
    private Button mOk;

    public EditTextAttachmentDialogFragment() {
    }

    public static EditTextAttachmentDialogFragment newInstance(String text) {
        EditTextAttachmentDialogFragment frag = new EditTextAttachmentDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final String text = getArguments().getString("text");

        View dialogView =  inflater.inflate(R.layout.dialog_edit_attachment_text, container);

        mText = (EditText) dialogView.findViewById(R.id.dialog_edit_text_attachment_text);
        mText.setText(text);
        mText.setSelection(mText.getText().length());

        mOk = (Button) dialogView.findViewById(R.id.dialog_edit_text_attachment_ok);
        mOk.setOnClickListener(this);
        mCancel = (Button) dialogView.findViewById(R.id.dialog_edit_text_attachment_cancel);
        mCancel.setOnClickListener(this);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialogView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_edit_text_attachment_cancel:
                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mText.getWindowToken(), 0);

                dismiss();
                break;

            case R.id.dialog_edit_text_attachment_ok:
                if(!mText.getText().toString().trim().isEmpty()) {

                    mListener.onFinishEditTextAttachmentDialog(mText.getText().toString().trim());

                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mText.getWindowToken(), 0);

                    dismiss();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.dialog_edit_text_error_empty_text), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void setListener(EditTextAttachmentDialogDismissListener listener) {
        mListener = listener;
    }


    public interface EditTextAttachmentDialogDismissListener {
        void onFinishEditTextAttachmentDialog(String text);
    }
}
