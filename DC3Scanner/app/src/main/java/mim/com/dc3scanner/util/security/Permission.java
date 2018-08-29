package mim.com.dc3scanner.util.security;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by marcoisaac on 12/1/2016.
 */

public class Permission {

    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        //int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //if (permission != PackageManager.PERMISSION_GRANTED) {
        // We don't have permission so prompt the user

        ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
        );
        //}
    }

    public static boolean verifyStorage(Activity activity, String permiso) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,permiso);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }
}