package edu.sfsu.napkin.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
import java.util.Set;
import edu.sfsu.napkin.R;


public class Settings extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        /**
         * This activity allows the user to select their allergies, it then stores the selections in SharedPreferences
         * @author Sang Saephan
         */
        SharedPreferences allergyList = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> allergies = allergyList.getStringSet("allergy_key", null);
        String[] selectedAllergies =  allergies.toArray(new String[]{});


    }




}
