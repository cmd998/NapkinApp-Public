package edu.sfsu.napkin.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import edu.sfsu.napkin.R;

public class Headers extends PreferenceActivity {

    @Override

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_headers);
    }
}
