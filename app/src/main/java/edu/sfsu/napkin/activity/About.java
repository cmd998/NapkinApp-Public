package edu.sfsu.napkin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.sfsu.napkin.R;

/** About class used to explain the terms and condition of the app. It also references the APIs and
 * libraries that have been used.
 */

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * This method inflates the menu item onto the activity
     * @author Sang Saephan
     * @param menu The menu to be inflated onto the activity
     * @return true if the menu is valid
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_navigation_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method calls another method to start an intent to a different activity
     * @author Sang Saephan
     * @param item The item selected from the navigation menu
     * @return true based on the id of the selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        switch(item.getItemId()){
            case R.id.home_action:
                home();
                return true;
            case R.id.ingredient_action:
                ingredient();
                return true;
            case R.id.settings_action:
                settings();
                return true;
            case R.id.tutorial_action:
                tutorial();
                return true;
            case R.id.about_action:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to go to Home screen
     * @author Sang Saephan
     */
    public void home() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    /**
     * Method to go to Ingredient screen
     * @author Sang Saephan
     */
    public void ingredient() {
        Intent intent = new Intent(this, IngredientInput.class);
        startActivity(intent);
    }

    /**
     * Method to go to Settings screen
     * @author Sang Saephan
     */
    public void settings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    /**
     * Method to go to Tutorial screen
     */
    public void tutorial() {
        Intent goToTutorial = new Intent(this, Tutorialclass.class);
        startActivity(goToTutorial);
    }

    /**
     * Method to go to About screen
     */
    public void about() {
        Intent goToAbout = new Intent(this, About.class);
        startActivity(goToAbout);
    }
}
