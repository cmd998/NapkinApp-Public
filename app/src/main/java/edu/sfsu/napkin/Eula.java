package edu.sfsu.napkin;

/** Eula class used to create a simple End User License Agreement. The EULA checks to see if a the unique key is matched
 * if the key is matched, the user has seen and accepted the EULA and nothing will happen. If it is not matched then a
 * dialog will be displayed with the EULA in it. If the user declines the EULA, the app will close.
 * Referenced code from (http://www.donnfelker.com/android-a-simple-eula-for-your-android-apps/)
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class Eula {
    //Creates a unique key for the version
    private String EULA_PREFIX = "eulaKey_";
    private Activity activityInfo;

    public Eula(Activity context) {
        activityInfo = context;
    }

    //Finds the package info of the app
    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = activityInfo.getPackageManager().getPackageInfo(activityInfo.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException exception) {
            exception.printStackTrace();
        }
        return pi;
    }

    //Creates a dialog that will show the user the EULA
    public void show() {
        PackageInfo versionInfo = getPackageInfo();
        // Checks shared preference to see if the key has been set
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activityInfo);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);

        if(hasBeenShown == false){

            // Show the title of the EULA
            String title = activityInfo.getString(R.string.app_name) + " v" + versionInfo.versionName;

            //Includes the updates as well so users know what changed.
            String message = activityInfo.getString(R.string.updates) +
                    "Please read the following terms and conditions: " +
                    "\n\n\t\tThis application finds recipes based off the user's inputted ingredients.\n\n" +
                    "\t\tPlease use this application AT YOUR OWN RISK. We are not liable for any harm done to yourself " +
                    "done from your own cooking, etc. We are only providing services to helping the user find recipes based on the ingredients inputted." +
                    " More details can be found in the 'About' section in the settings bar."
                    + activityInfo.getString(R.string.eula);

            AlertDialog.Builder builder = new AlertDialog.Builder(activityInfo);

            builder.setTitle(title);
            builder.setMessage(message);

            builder.setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Mark this version as read.
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(eulaKey, true);
                    editor.commit();
                    dialogInterface.dismiss();
                }
            });

            builder.setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Close the activity as they have declined the EULA
                    activityInfo.finish();
                }
            });

            builder.create().show();
        }
    }
}