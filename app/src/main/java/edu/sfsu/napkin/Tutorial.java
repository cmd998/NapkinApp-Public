package edu.sfsu.napkin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** Tutorial class used to create a simple dialog box that informs the user about an optional tutorial
 * that is located in the settings bar. This dialog box only shows when the user opens the app for the first
 * time. The class checks to see if a the unique key is matched. If the key is matched, the user has seen and
 * accepted the Tutorial notification and nothing will happen.
 */

public class Tutorial {
    //Creates a unique key for the version
    private String TUTORIAL_PREFIX = "tutorialKey_";
    private Activity activityInfo;

    public Tutorial(Activity context) {
        activityInfo = context;
    }

    //Creates a dialog that will show the user the Tutorial Dialog
    public void show() {
        // Checks shared preference to see if the key has been set
        final String tutorialKey = TUTORIAL_PREFIX;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activityInfo);
        boolean hasBeenShown = prefs.getBoolean(tutorialKey, false);

        if (hasBeenShown == false) {

            String title = "Need help?";
            String message = "Go to the settings bar and click on 'Need help?' to begin a tutorial.";

            AlertDialog.Builder builder = new AlertDialog.Builder(activityInfo);

            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor tutorialEditor = prefs.edit();
                    tutorialEditor.putBoolean(tutorialKey, true);
                    tutorialEditor.commit();
                    dialogInterface.dismiss();
                }
            });

            builder.create().show();
        }
    }
}