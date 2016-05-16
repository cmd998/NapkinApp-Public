package edu.sfsu.napkin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.sfsu.napkin.R;
import edu.sfsu.napkin.StringAdapter;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;
import edu.sfsu.napkin.activity.Tutorialclass;

/**
 * ingr stored in FILENAME "ingrListFile"
 */

public class IngredientInput extends AppCompatActivity {

    public static final String PREVIOUS_SEARCH_FILE = "previous_search";
    //create an autocomplete object
    AutoCompleteTextView autoCompleteTextView;
    public Activity mActivity;

    //Tourguide variables
    private Animation enterAnimation, exitAnimation;
    private AutoCompleteTextView clickToAddIngr;
    private ListView ingrList;
    private Button useIngr;


    private Tutorialclass tutorialClass = new Tutorialclass();

    //create an autocomplete object
    String[] ing_array;

    private String FILENAME = "ingrListFile";  //file to store ingr list on phone mem

    //////////////////////////////////////////////////////////////////////////
    public ListView ingrListView;
    private ArrayList ingrSearchArray;
    //Swiping
    private boolean mSwiping = false; // detects if user is swiping on ACTION_UP
    private boolean mItemPressed = false; // Detects if user is currently holding down a view
    private static final int SWIPE_DURATION = 250; // needed for velocity implementation
    private static final int MOVE_DURATION = 150;  //duration of move animation
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_input);

        ImageView stepOne = (ImageView) findViewById(R.id.stepone_iv);
        stepOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Select any combination of ingredients. \nSlide LEFT to Remove.",
                        Toast.LENGTH_LONG).show();
            }
        });

        //Tutorial continues
        if(tutorialClass.hasBeenClicked == true) {
            tutorialContinued();
        }

        //ingrSearchArray stores ingr user selectes from autocomplete list and creates a text view
        ingrSearchArray = new ArrayList<>();


        //if user wanted to load prev, load the last ingr list from file
        if(load_prev() && getPrevIngrList().size() != 0 && getPrevIngrList() != null){
            //read last searched ingr list from file and split into ingrSearchArray
            for(String ingr : getPrevIngrList()){
                ingrSearchArray.add(ingr);
            }
        }

        //instantiate ListView
        ingrListView = (ListView) findViewById(R.id.list_view);
        //assign a string adapter that dynamically builds list view
        final StringAdapter adapter = new StringAdapter(IngredientInput.this, ingrSearchArray, mTouchListener);
        ingrListView.setAdapter(adapter);

        //creating autoComplete view on this activity
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.ingrAutoComplete);
        ing_array = getResources().getStringArray(R.array.ingArray);
        ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ing_array);
        autoCompleteTextView.setAdapter(autocompleteAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ingr = autoCompleteTextView.getText().toString();
                adapter.add(ingr);  //add the selected ingr to ingrListView as a
                ingr += " has been added";
                //make a toast message to confirm add
                Toast itemAddedToast = Toast.makeText(getApplicationContext(), ingr, Toast.LENGTH_SHORT);
                itemAddedToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
                itemAddedToast.show();
                //Toast.makeText(getApplicationContextya(), ingr, Toast.LENGTH_SHORT).show();
                autoCompleteTextView.setText("");

            }
        });

    }

    /** tutorialContinued continues the tutorial if hasBeenClicked is true. This method creates new
     * overlays that will be placed on top of the activity.
     */
    private void tutorialContinued(){
        clickToAddIngr = (AutoCompleteTextView) findViewById(R.id.ingrAutoComplete);
        ingrList = (ListView) findViewById(R.id.list_view);
        useIngr = (Button) findViewById(R.id.ingrDoneButton);

        enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);

        TourGuide enterRecipe = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Enter Ingredients here!")
                                .setDescription("This is where you enter ingredients. Type 'chicken' and click to add. Try adding 'pepper' too! Click on shaded area to continue.")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.BOTTOM)
                )
                .playLater(clickToAddIngr);

        TourGuide ingredientsList = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Here is where your ingredients will be.")
                                .setDescription("Swipe left to remove your ingredient(s), swipe right to favorite ingredient.")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.TOP)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                                .setStyle(Overlay.Style.Rectangle)
                )
                .playLater(ingrList);

        TourGuide done = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Finished adding recipes?")
                                .setDescription("Click here to proceed!")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.TOP)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .playLater(useIngr);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(enterRecipe, ingredientsList, done)
                .setDefaultOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        TourGuide.init(this).playInSequence(sequence);

    }

    //Override the back button
    @Override
    public void onBackPressed() {
        tutorialClass.hasBeenClicked = false;
        Intent goHome = new Intent(this, Home.class);
        startActivity(goHome);
        super.onBackPressed();
    }

    /**On click start recipe search -> to go recipe list activity
     * @author Andrey Barsukov
     * @param view
     */
    public void ingrInputDone(View view) {
        //writing current search list to SharedPreferences
        updateIngrList();
        Intent goToOutputIntent = new Intent(this, RecipeList.class);
        goToOutputIntent.putExtra("edu.sfsu.napkin.ChosenIngredients", ingrSearchArray.toArray(new String[ingrSearchArray.size()]));
        startActivity(goToOutputIntent);
    }

    /** reformat ingr list file, remove brackets, trim
     * @author Andrey Barsukov
     * @param fileInput
     * @return String
     */
    public String buildIngrList(String fileInput){
        String ret = fileInput;
        ret = ret.replace("[", "");
        ret = ret.replace("]", "");
        ret = ret.trim();
        return ret;
    }

    //VOLLY


    /** SHARED PREFERENCES saving ingr to SharedPreferences
     *@author Andrey Barsukov
     * @param
     */
    private void updateIngrList() {
        // TODO: 10/13/2015 - DONE: 10/31/2015 (andrey)
        SharedPreferences sharedPrefs = getSharedPreferences(PREVIOUS_SEARCH_FILE, MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPrefs.edit();

        //Converting (HashSet) ArrayList to Set<String>
        Set<String> ingrSet = new HashSet<String>(ingrSearchArray);
        spEditor.putStringSet("saved_ingr_list", ingrSet);

        //save date/time for last search
        Date currentTime = new Date();
        spEditor.putString("last_search_time", currentTime.toString());

        //committing changes
        spEditor.commit();
    }

    /** SHARED PREFERENCES reload previously searched ingr list
     *@author Andrey Barsukov
     * @return
     */
    private Set<String> getPrevIngrList(){
        SharedPreferences sharedPrefs = getSharedPreferences(PREVIOUS_SEARCH_FILE, MODE_PRIVATE);
        return sharedPrefs.getStringSet("saved_ingr_list", null);
    }

    /**SHARED PREFERENCE HANDLING
     * @author Andrey Barsukov
     * @return returns true is user selected "Load Previous Search" button on last screen
     */
    private boolean load_prev() {
        SharedPreferences sharePrefs = getSharedPreferences(PREVIOUS_SEARCH_FILE, MODE_PRIVATE);
        return sharePrefs.getBoolean("load_ingr_list", false);
    }

    /** Addition and removal of ListView items
     * @author Andrey Barsukov
     *  This class handles the creation deleting of ingr ListView items,
     *      several adapters used, one for data storage of the list views
     *      one for the listView items themselves to keep track which view is removed
     *      and notify the animation adapter.
     *
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){
        float mDownX;
        private int mSwipeSlop = -1;
        boolean swiped;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(IngredientInput.this).getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed) {
                        // Doesn't allow swiping two items at same time
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    swiped = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE: {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);

                    if (!mSwiping){
                        if (deltaXAbs > mSwipeSlop){ // tells if user is actually swiping or just touching in sloppy manner
                            mSwiping = true;
                            ingrListView.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping && !swiped){ // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)

                        v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped

                        if (deltaX > v.getWidth() / 3){ // swipe to right
                            mDownX = x;
                            swiped = true;
                            mSwiping = false;
                            mItemPressed = false;

                            //animate small pause to allow user to retrieve a leaving swipe
                            v.animate().setDuration(300).translationX(v.getWidth() / 3); // could pause here if you want, same way as delete
                            TextView tv = (TextView) v.findViewById(R.id.list_tv);

                            return true;
                        }
                        else if (deltaX < -1 * (v.getWidth() / 3)){ // swipe to left
                            v.setEnabled(false); // need to disable the view for the animation to run

                            //set a toast message that ingr was removed
                            Toast removedIngrToast = Toast.makeText(getApplicationContext(), "Item removed", Toast.LENGTH_SHORT);
                            removedIngrToast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            removedIngrToast.show();

                            // stacked the animations to have the pause before the views flings off screen
                            v.animate().setDuration(300).translationX(-v.getWidth()/3).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    v.animate().setDuration(300).alpha(0).translationX(-v.getWidth()).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSwiping = false;
                                            mItemPressed = false;
                                            animateRemoval(ingrListView, v);
                                        }
                                    });
                                }
                            });
                            mDownX = x;
                            swiped = true;
                            return true;
                        }
                    }

                }
                break;
                case MotionEvent.ACTION_UP:{
                    if (mSwiping){ // if the user was swiping, don't go to the and just animate the view back into position
                        v.animate().setDuration(300).translationX(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mSwiping = false;
                                mItemPressed = false;
                                ingrListView.setEnabled(true);
                            }
                        });
                    }
                    else{ // user was not swiping; registers as a click
                        //TODO On click pop up list with more specific versions of ingr
                        mItemPressed = false;
                        ingrListView.setEnabled(true);
                        int i = ingrListView.getPositionForView(v);
                        Toast.makeText(IngredientInput.this, ingrSearchArray.get(i).toString(), Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                default:
                    return false;
            }
            return true;
        }
    };

    /**Animate item removal of a recipe
     * @author Andrey Barsukov
     * @param listView
     * @param viewToRemove
     */
    // animates the removal of the view, also animates the rest of the view into position
    private void animateRemoval(final ListView listView, View viewToRemove) {
        //TODO find a better place to refresh ingr search list
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        final ArrayAdapter adapter = (ArrayAdapter) ingrListView.getAdapter();
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View child = listView.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = listView.getAdapter().getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }

        //remove view from adapter
        adapter.remove(adapter.getItem(listView.getPositionForView(viewToRemove)));

        final ViewTreeObserver observer = listView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                for (int i = 0; i < listView.getChildCount(); ++i) {
                    final View child = listView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable() {
                                    public void run() {
                                        mSwiping = false;
                                        ingrListView.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listView.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable() {
                                public void run() {
                                    mSwiping = false;
                                    ingrListView.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }

    /** Writing to file
     * Open a output stream and write data para into it
     *@author Andrey Barsukov
     * @param String data
     *
    */
    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILENAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /** Read from file
     *  create and open an inputStreamBuffer and build a string from it to return
     *  @author Andrey Barsukov
     * @return String ret
     */
    private String readFromFile() {
        String ret = "";
        try {
            InputStream inputStream = openFileInput(FILENAME);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
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
