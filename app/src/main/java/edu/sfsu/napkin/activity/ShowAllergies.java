package edu.sfsu.napkin.activity;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;

import java.util.Set;

public class ShowAllergies extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Create adapters to read the array containing the allergy selections
         * @author Sang Saephan
         */
        ArrayAdapter allergyAdapter;
        ArrayAdapter noAllergyAdapter;

        /**
         * Retrieve data from the allergy list via SharedPreferences
         * @author Sang Saephan
         */
        SharedPreferences allergyList = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> allergies = allergyList.getStringSet("allergy_key", null);
        String[] selectedAllergies =  allergies.toArray(new String[]{});

        String[] noAllergy = {"No allergies selected"};

        /**
         * Inputs the values of the arrays into the adapters
         * @author Sang Saephan
         */
        allergyAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, selectedAllergies);
        noAllergyAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, noAllergy);

        /**
         * Displays the list
         * @author Sang Saephan
         */
        if(!allergies.isEmpty()) {
            setListAdapter(allergyAdapter);
        }else{
            setListAdapter(noAllergyAdapter);
        }
    }
}
