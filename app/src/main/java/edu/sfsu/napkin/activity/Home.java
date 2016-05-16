package edu.sfsu.napkin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import edu.sfsu.napkin.Eula;
import edu.sfsu.napkin.R;
import edu.sfsu.napkin.Tutorial;
import tourguide.tourguide.TourGuide;


/**
 *Napkin is an app designed to allow users to pick from a list of ingredients from on-hand ingredients.
 * @version 1.0
 * @since 2015-09-25
 *
 * Home.java is used to handle all functionality on the initial view after the Splash screen.
 * Functionality Includes:
 * @see Animation
 * @see Tutorial
 * @see Eula
 *
 */

//SharePreferences file:  load_prev_search
//                        - field: load_ingr_list

    /*External files used to carry out animations, trasitions, Eula overlay, and return to previous recipes. */

public class Home extends AppCompatActivity {
    public TourGuide mTourGuideHandler;
    public Activity mAct;
    public TextView last_recipe_TV;
    public TextView timeTV;

    public static final String PREVIOUS_SEARCH_FILE = "previous_search";
    public static final String LOAD_PREVIOUS = "load_ingr_list";
    private String lastRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView myImageView= (ImageView)findViewById(R.id.instrImgView);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.rotatetooriginal);
        myImageView.startAnimation(myFadeInAnimation); //Set animation to your ImageView

        //set last search time
        timeTV = (TextView) findViewById(R.id.prevSearchTime_tv);
        setPreviousSearchTime();

        //TODO add load last searched recipe display
        //last_recipe_TV = (TextView) findViewById(R.id.prev_recipe_TV);
        //setLastSearchedRecipe();

        //Shows the tutorial notification
        new Tutorial(this).show();

        //Shows the EULA
        new Eula(this).show();
    }

    /** REDEFINING onResume to update last search time
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        setPreviousSearchTime();
        //setLastSearchedRecipe();
    }

    /** Update last search time tv
     *@author Andrey Barsukov
     */
    private void setPreviousSearchTime() {
        timeTV.setText(String.format("Last Search: %s", getLastSearchTime()));
    }

    /**Sets the last searched recipe
     * @author Andrey Barsukov
     */
    private void setLastSearchedRecipe() {
        last_recipe_TV.setText(getLastSearchedRecipe());
    }

    /**get last searched recipe from SharedPrefs
     * @author Andrey Barsukov
     * @return recipe details
     */
    private String getLastSearchedRecipe() {
        String lastRecipe = "";
        SharedPreferences spLast = getSharedPreferences(getString(R.string.saved_recipe_file), MODE_PRIVATE);
        lastRecipe = ("Name: " + spLast.getString(getString(R.string.recipe_name), "no searches found") +
                "\nPrep TIme: " +  spLast.getString(getString(R.string.recipe_time), "n/a"));
        return lastRecipe;
    }

    /**On click method -> go to ingredient input activity
     * @author Andrey Barsukov
     */
    public void onClickLetsCook (View view){
        setLoadPrevSearch(false);

        Intent goToIngInput = new Intent(this, IngredientInput.class);

        startActivity(goToIngInput);
    }

    /**Retrieve previously search ingredients list
     * @author Andrey Barsukov
     * @return String set of ingredients
     */
    private Set<String> getPrevIngrList(){
        SharedPreferences sharedPrefs = getSharedPreferences(PREVIOUS_SEARCH_FILE, MODE_PRIVATE);
        return sharedPrefs.getStringSet("saved_ingr_list", null);
    }

    /**Loads the most recently searched ingredient list
     * @author Andrey Barsukov
     * @param view
     */
    public void onClickLoadPrev (View view){
        if(getPrevIngrList() == null){
            Toast prevSearchToast = Toast.makeText(getApplicationContext(), "No previous searches found.", Toast.LENGTH_SHORT);
            prevSearchToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
            prevSearchToast.show();
        }
        else {
            setLoadPrevSearch(true);
            Intent goToIngInputLoadPrev = new Intent(this, IngredientInput.class);

            startActivity(goToIngInputLoadPrev);
        }
    }

    /**used to save if user wants to load prev search list from file
     * @author Andrey Barsukov
     * @param load  -  boolean, determined by which button was clicked
     */
    private void setLoadPrevSearch(boolean load) {
        SharedPreferences sharePrefs = getSharedPreferences(PREVIOUS_SEARCH_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePrefs.edit();
        editor.putBoolean(LOAD_PREVIOUS, load);
        editor.commit();

    }

    /**Gets timestap for most recent ingredient search
     * @author Andrey Barsukov
     * @return timestap string
     */
    private String getLastSearchTime(){
        SharedPreferences sharePrefs = getSharedPreferences(PREVIOUS_SEARCH_FILE, MODE_PRIVATE);
        return sharePrefs.getString("last_search_time", "not found");
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