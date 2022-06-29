package rs.com.loctionbased.reminder.util;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public final class ColorUtil {

    public static int get(@NonNull Context context, int res) {
        try {
            return ContextCompat.getColor(context, res);
        } catch (Resources.NotFoundException e) {
            return res;
        }
    }
}
