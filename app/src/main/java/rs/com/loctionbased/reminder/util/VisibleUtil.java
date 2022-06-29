package rs.com.loctionbased.reminder.util;

import androidx.annotation.Nullable;
import android.view.View;

public final class VisibleUtil {

    public static void handle(View v, @Nullable String s) {
        v.setVisibility(s == null || s.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
