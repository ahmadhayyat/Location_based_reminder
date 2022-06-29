package rs.com.loctionbased.reminder.util;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;


public class PermissionUtil {
    public static String[] checkIfPermissionsAreGranted(Context context, String ... permissions) {

        List<String> nonGrantedPermissions = new ArrayList<>();
        for (String permission: permissions) {
            int rc = ActivityCompat.checkSelfPermission(context, permission);
            if(rc != PackageManager.PERMISSION_GRANTED) {
                nonGrantedPermissions.add(permission);
            }
        }

        if(nonGrantedPermissions.size() == 0)
            return null;
        else
            return nonGrantedPermissions.toArray(new String[nonGrantedPermissions.size()]);
    }
}
