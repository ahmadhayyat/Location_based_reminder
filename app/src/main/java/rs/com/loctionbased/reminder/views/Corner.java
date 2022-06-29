package rs.com.loctionbased.reminder.views;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    Corner.TOP_LEFT, rs.com.loctionbased.reminder.views.Corner.TOP_RIGHT,
    Corner.BOTTOM_LEFT, rs.com.loctionbased.reminder.views.Corner.BOTTOM_RIGHT
})
public @interface Corner {
  int TOP_LEFT = 0;
  int TOP_RIGHT = 1;
  int BOTTOM_RIGHT = 2;
  int BOTTOM_LEFT = 3;
}
