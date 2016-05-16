package edu.sfsu.napkin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import edu.sfsu.napkin.R;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/** Tutorialclass used to begin the tutorial. Creates an overlay on top of the activities that allows
 * the user to interact with the app and continue the tutorial. The tutorial can be quit at any time by
 * hitting the back button.
 */
public class Tutorialclass extends AppCompatActivity {
    public Activity activity;
    public static boolean hasBeenClicked;

    public static final String LOAD_PREV_SEARCH_FILE = "load_prev_search";
    public static final String SHARED_PREF_ITEM_LOAD_INGR = "load_ingr_list";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tutorialBegin();
    }

    public void tutorialBegin() {
        activity = this;

        Animation enterAnimation, exitAnimation;

        Button button = (Button) findViewById(R.id.button);
        Button loadPrevIngr = (Button) findViewById(R.id.loadPrevIngr);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);

        //Creates the first overlay of the tutorial by setting its overlay and the tool tip.
        TourGuide beginTutorial = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Welcome to our app!")
                                .setDescription("To continue through the tutorials, click on the shaded areas unless told otherwise! To quit the tutorial at any time, hit the back button.")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.TOP)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .playLater(imageView);

        TourGuide loadPrevSearch = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Want to see your previous searches?")
                                .setDescription("Here is where you'll load your previous searches.")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.TOP)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .playLater(loadPrevIngr);

        TourGuide letsCook = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Lets get started!")
                                .setDescription("Click on 'Lets cook' to continue.")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.TOP)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .playLater(button);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(beginTutorial, loadPrevSearch, letsCook)
                .setDefaultOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        TourGuide.init(this).playInSequence(sequence);

        //Set a variable that will be passed to each activity. If this variable is true then the tutorial will continue.
        hasBeenClicked = true;
    }

    @Override
    public void onBackPressed() {
        hasBeenClicked = false;
        Intent goHome = new Intent(this, Home.class);
        startActivity(goHome);
        super.onBackPressed();
    }

    public void onClickLoadPrev(View view) {
        setLoadPrevSearch(true);

        Intent goToIngInputLoadPrev = new Intent(this, IngredientInput.class);
        startActivity(goToIngInputLoadPrev);
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

    public void onClickLetsCook(View view) {
        setLoadPrevSearch(false);
        Intent goToIngInput = new Intent(this, IngredientInput.class);
        startActivity(goToIngInput);
    }

    private void setLoadPrevSearch(boolean load) {
        SharedPreferences sharePrefs = getSharedPreferences(LOAD_PREV_SEARCH_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePrefs.edit();
        editor.putBoolean(SHARED_PREF_ITEM_LOAD_INGR, load);
        editor.commit();
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
        switch (item.getItemId()) {
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