package rs.com.loctionbased.reminder.util;

import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.List;

import rs.com.loctionbased.reminder.R;
import rs.com.loctionbased.reminder.enums.TapTargetSequenceType;


public class TapTargetSequenceUtil {

    private static final int DELAY = 100;

    public static void showTapTargetSequenceFor(@NonNull final AppCompatActivity activity, @NonNull TapTargetSequenceType type) {
        List<TapTarget> targets = new ArrayList<>();

        if(!SharedPreferenceUtil.doShowTapTargetSequenceFor(activity, type))
            return;

        switch (type) {
            case EDIT_IMAGE_ATTACHMENT_ACTIVITY:
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_edit_image_attachment_crop), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_crop_title), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_crop_description)).transparentTarget(true).cancelable(false));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_edit_image_attachment_rotate), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_rotate_title), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_rotate_description)).transparentTarget(true).cancelable(false));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_edit_image_attachment_camera), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_camera_title), activity.getResources().getString(R.string.ttsu_activity_edit_image_attachment_camera_description)).transparentTarget(true).cancelable(false));
                doShowTapTargetSequenceFor(activity, targets, null);
                break;

            case PLACE_LIST_ACTIVITY:
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_place_list_no_items_container), activity.getResources().getString(R.string.ttsu_activity_place_list_no_items_container_title), activity.getResources().getString(R.string.ttsu_activity_place_list_no_items_container_description)).transparentTarget(true).cancelable(false).targetRadius(100));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_place_list_fab), activity.getResources().getString(R.string.ttsu_activity_place_list_fab_title), activity.getResources().getString(R.string.ttsu_activity_place_list_fab_description)).transparentTarget(true).cancelable(false));
                doShowTapTargetSequenceFor(activity, targets, null);
                break;

            case PLACE_ACTIVITY:
                targets.add(TapTarget.forView(activity.findViewById(R.id.place_autocomplete_search_button), activity.getResources().getString(R.string.ttsu_activity_place_autocomplete_search_button_title), activity.getResources().getString(R.string.ttsu_activity_place_autocomplete_search_button_description)).transparentTarget(true).cancelable(false));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_place_map), activity.getResources().getString(R.string.ttsu_activity_place_map_container_title), activity.getResources().getString(R.string.ttsu_activity_place_map_container_description)).transparentTarget(true).cancelable(false).targetRadius(100));
                targets.add(TapTarget.forView(activity.findViewById(R.id.activity_place_radius_icon), activity.getResources().getString(R.string.ttsu_activity_place_radius_icon_title), activity.getResources().getString(R.string.ttsu_activity_place_radius_icon_description)).transparentTarget(true).cancelable(false));
                doShowTapTargetSequenceFor(activity, targets, null);
                break;

            default:
                Toast.makeText(activity, "TapTargetSequenceUtil(): Invalid TapTargetSequenceType", Toast.LENGTH_SHORT).show();
        }
    }

    private static void doShowTapTargetSequenceFor(@NonNull final AppCompatActivity activity, @NonNull final List<TapTarget> targets, @Nullable final TapTargetSequence.Listener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new TapTargetSequence(activity)
                        .targets(targets)
                        .listener(listener)
                        .start();
            }
        }, DELAY);
    }
}
