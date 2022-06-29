package rs.com.loctionbased.reminder.act;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.helper.SampleHelper;

public class ActivitySample extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_view);

        SampleHelper.with(this).init().loadAbout();
    }

}
