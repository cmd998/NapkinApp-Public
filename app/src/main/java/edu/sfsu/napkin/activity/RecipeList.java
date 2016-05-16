package edu.sfsu.napkin.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.napkin.api.APIRequestQueue;
import edu.sfsu.napkin.api.BigOvenRequestBuilder;
import edu.sfsu.napkin.api.RecipeRequestBuilder;
import edu.sfsu.napkin.api.YummlyRequestBuilder;
import edu.sfsu.napkin.R;

import edu.sfsu.napkin.Recipe;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * file name INGR_LIST_FILE = "ingrListFile"
 */

public class RecipeList extends AppCompatActivity {
    public static final String INGR_LIST_FILE = "ingrListFile";  //name of file
    public String sample = "sample";
    public ArrayList<Recipe> mRecipeArray;
    public ListView recipeListView;
    private RecipeListAdapter RLAdapter;

    //Tutorial
    public Activity activity;
    private TextView recipeOutputText, prepTimeTV, ingrMatch, ingrMatchedTextView, prepTimeTextView;
    private TextView sortByTV;
    private Button sampleRecipe;
    private Animation enterAnimation, exitAnimation;
    private Tutorialclass tutorialClass = new Tutorialclass();
    private ListView recipeList;

    private final YummlyRequestBuilder mYumReqBuilder = YummlyRequestBuilder.getInstance();
    private final BigOvenRequestBuilder mBigOvenReqBuilder = BigOvenRequestBuilder.getInstance();

    public Dialog reviewDialog;
    TextView review_nameTV;
    TextView review_youHaveTV;
    TextView review_youNeedTV;
    TextView review_timeTV;
    TextView review_ingredientsTV;
    ImageView review_image;
    Button dismissButton;
    Button loadRecipeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        TextView tv = (TextView) findViewById(R.id.recipeOutputText);
        ingrMatchedTextView = (TextView) findViewById(R.id.ingrMatchedTV);
        prepTimeTextView = (TextView) findViewById(R.id.prepTimeTV);
        sortByTV = (TextView) findViewById(R.id.sortBy_text_view);

        mRecipeArray = new ArrayList<>();
        recipeListView = (ListView) findViewById(R.id.recipe_list_view);
        sample = "";

        reviewDialog = new Dialog(RecipeList.this);

        ImageView stepTwo = (ImageView) findViewById(R.id.steptwo_iv);
        stepTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Try Adding/Removing ingredients to get different results.",
                        Toast.LENGTH_LONG).show();
            }
        });

        //make API call with current ingr list
        recipeListRequest(mYumReqBuilder.findRecipesByIngredients(getSearchParameters()), mYumReqBuilder);
        //recipeListRequest(mBigOvenReqBuilder.findRecipesByIngredients(getSearchParameters()), mBigOvenReqBuilder);

        //Tutorial Continues
        if(tutorialClass.hasBeenClicked == true) {
            tutorialContinued();
        }

        //Sort by percent matched
        ingrMatchedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SORTING BY INGREDIENTS MATCHED

                if(mRecipeArray.size() != 0 && mRecipeArray != null){
                    Collections.sort(mRecipeArray, Recipe.COMPARE_BY_PERCENT_MATCHED);
                    RLAdapter.notifyDataSetChanged();
                    sortByTV.setText("Sorting by: % match");
                }
            }
        });

        //sort by prep timne
        prepTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SORTING LIST BY PREP TIME
                if(mRecipeArray.size() != 0 && mRecipeArray != null){
                    Collections.sort(mRecipeArray, Recipe.COMPARE_BY_PREPTIME);
                    RLAdapter.notifyDataSetChanged();
                    sortByTV.setText("Sorting by: prep time");
                }
            }
        });

        //SET UP REVIEW DIALOG
        reviewDialog.setContentView(R.layout.review_recipe_dialog);
        reviewDialog.setTitle("Preview the recipe");

        review_nameTV =(TextView) reviewDialog.findViewById(R.id.review_name_tv);
        review_image = (ImageView) reviewDialog.findViewById(R.id.review_image_iv);
        dismissButton = (Button) reviewDialog.findViewById(R.id.dismisDialog);
        loadRecipeButton = (Button) reviewDialog.findViewById(R.id.loadRecipe_reivew);
        review_timeTV = (TextView) reviewDialog.findViewById(R.id.review_time_tv);
        review_ingredientsTV = (TextView) reviewDialog.findViewById(R.id.review_ingredients_tv);
        review_youHaveTV = (TextView) reviewDialog.findViewById(R.id.review_youHave_tv);
        review_youNeedTV = (TextView) reviewDialog.findViewById(R.id.review_youNeed_tv);

        Toast.makeText(getApplicationContext(), "Loading... please wait.",
                Toast.LENGTH_SHORT).show();

    }

    /** tutorialContinued continues the tutorial if hasBeenClicked is true. This method creates new
     * overlays that will be placed on top of the activity.
     */
    private void tutorialContinued() {
        activity = this;

        recipeOutputText = (TextView)findViewById(R.id.recipeOutputText);
        prepTimeTV = (TextView)findViewById(R.id.prepTimeTV);
        ingrMatch = (TextView)findViewById(R.id.ingrMatchedTV);
        recipeList = (ListView) findViewById(R.id.recipe_list_view);

        enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);

        TourGuide recipeOutput = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Your top choices")
                                .setDescription("This side will show you the best related recipes based off your ingredients.")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.BOTTOM)
                )
                .playLater(recipeOutputText);

        TourGuide matchIngr = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Ingredient Match")
                                .setDescription("Clicking this will sort recipes by ingredients matched")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .playLater(ingrMatch);

        TourGuide prepTime = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Preparation Time")
                                .setDescription("Clicking this will sort recipes by preparation time")
                                .setTextColor(Color.parseColor("#bdc3c7"))
                                .setBackgroundColor(Color.parseColor("#e74c3c"))
                                .setShadow(true)
                                .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .playLater(prepTimeTV);

        TourGuide recipeListView = TourGuide.init(this)
                .setToolTip(new ToolTip()
                                .setTitle("Recipe list")
                                .setDescription("Here is where all the recipes will load. Swipe to finish tutorial and select a recipe.")
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
                .playLater(recipeList);

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(recipeOutput, matchIngr, prepTime, recipeListView)
                .setDefaultOverlay(new Overlay()
                                .setEnterAnimation(enterAnimation)
                                .setExitAnimation(exitAnimation)
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        TourGuide.init(this).playInSequence(sequence);
    }

    @Override
    public void onBackPressed() {
        tutorialClass.hasBeenClicked = false;
        Intent goIngredientInput = new Intent(this, IngredientInput.class);
        startActivity(goIngredientInput);
        super.onBackPressed();
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


    /**Populate recipe results list
     * @author Andrey Barsukov
     * Create an instance of a custom List Adapter and connect it to an array of Recipe Objects
     *  - RecipeListAdapter: custom Array List Adapter, more info below
     * Create on item list click listener
     *  - on recipe Item click:
     *      - create a recipe review dialog activity
     *      - populate review dialog
     *      - calculate missing ingredients
     *      - load image using Picasso
     *      - set .putExtras to pass to next activity
     *      - set onClick listeners for "LOAD INSTRUCTIIONS" button
     *
     */
    public void displayRecipeList() {

        //SORTING LIST BY PREPTIME
        //Collections.sort(mRecipeArray, Recipe.COMPARE_BY_PREPTIME);
        //RLAdapter.notifyDataSetChanged();

        //Populate the list
        if (RLAdapter == null) {
            RLAdapter = new RecipeListAdapter(this, R.layout.recipe_list_item, mRecipeArray);
            recipeListView.setAdapter(RLAdapter);
        } else {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            RLAdapter.notifyDataSetChanged();
                        }
                    });
            return;
        }

        //DeFAULT SORTING
        //RLAdapter.sort(Recipe.COMPARE_BY_INGR_MATCHED);

        //recipeListView.setAdapter(new RecipeListAdapter(this, R.layout.recipe_list_item, mRecipeArray));
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //Save last recipe clicked on
                final Recipe tempRecipe = mRecipeArray.get(position);
//                if (tempRecipe.getCookTime() < 0 || tempRecipe.getIngredients().size() == 0) {
//                    recipeRequest(mYumReqBuilder.findRecipeById(mRecipeArray.get(position)), mYumReqBuilder,
//                            mRecipeArray.get(position));
//                }
                //DIALOG:
                review_nameTV.setText(mRecipeArray.get(position).getName());
                //review_ingredientsTV.setText(mRecipeArray.get(position).getIngredients().toString());
                int have, need, time;
                boolean flag; //used to test is there is an ingr match
                have = mRecipeArray.get(position).getIngrMatched();
                need =  mRecipeArray.get(position).getIngredients().size() - mRecipeArray.get(position).getIngrMatched();
                review_youHaveTV.setText("You have: " + have);
                review_youNeedTV.setText("You need: " + need);
                review_timeTV.setText("Prep Time: ");
                time = mRecipeArray.get(position).getCookTime();
                if(time > 60){
                    review_timeTV.append(time/60 + " hour(s) ");
                }
                review_timeTV.append(time%60 + " minutes");

                for (String  r_ingr : mRecipeArray.get(position).getIngredients()) {
                    flag = false;
                    for(String s_ingr : getSearchParameters()){
                        if(r_ingr.contains(s_ingr)){
                            flag = true;
                        }
                    }
                    if(flag){
                        review_youHaveTV.append("\n+ " + r_ingr);
                    }
                    else{
                        review_youNeedTV.append("\n- " + r_ingr);
                    }
                }


                if(mRecipeArray.get(position).getAPI().equalsIgnoreCase("Yummly")) {
                    String unscaledURL = mRecipeArray.get(position).getImageUrl().toString();
                    unscaledURL = unscaledURL.replace("=s90-c", "");
                    mRecipeArray.get(position).setRecipeImageURL(unscaledURL);
                }

                //load image
                Picasso.with(reviewDialog.getContext())
                        .load(mRecipeArray.get(position).getImageUrl().toString())
                        .placeholder(R.drawable.napkin_orange)   // optional
                        .error(R.drawable.napkin_orange)         // optional
                        .into(review_image);

                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reviewDialog.dismiss();
                    }
                });

                loadRecipeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        System.out.println("ANDREY: " + tempRecipe);

                        Intent detailsIntent = new Intent(RecipeList.this, RecipeDetails.class);

                        //save data for next activity
                        detailsIntent.putExtra("exName", mRecipeArray.get(position).getName());
                        detailsIntent.putExtra("exAPI", mRecipeArray.get(position).getAPI());
                        detailsIntent.putExtra("exCuisine", mRecipeArray.get(position).getCuisine());
                        detailsIntent.putExtra("exType", mRecipeArray.get(position).getDishType());
                        detailsIntent.putExtra("exTime", mRecipeArray.get(position).getCookTime());
                        detailsIntent.putExtra("exIngredients", mRecipeArray.get(position).getIngredients().toString());
                        detailsIntent.putExtra("exID", mRecipeArray.get(position).getRecipeID());
                        detailsIntent.putExtra("exURL", mRecipeArray.get(position).getRecipeUrl());
                        detailsIntent.putExtra("exImgURL", mRecipeArray.get(position).getImageUrl());

                        //start next activity
                        startActivity(detailsIntent);
                    }
                });
                //display dialog
                reviewDialog.show();
            }
        });
    }

    private void recipeListRequest(String url, final RecipeRequestBuilder reqBuilder) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RESPONSE: "+ response);
                        mRecipeArray.addAll(reqBuilder.parseRecipes(response));
                        StringBuilder mRecipeArrayString = new StringBuilder();
                        if (mRecipeArray != null && mRecipeArray.size() > 0) {
                            for (int i = 0; i < mRecipeArray.size(); i++) {
                                mRecipeArrayString.append(mRecipeArray.get(i).toString());

                                if (mRecipeArray.get(i).getAPI().equalsIgnoreCase("Yummly")) {
                                    recipeRequest(mYumReqBuilder.findRecipeById(mRecipeArray.get(i)), mYumReqBuilder,
                                            mRecipeArray.get(i), i);
                                } else {
                                    recipeRequest(mBigOvenReqBuilder.findRecipeById(mRecipeArray.get(i)), mBigOvenReqBuilder,
                                            mRecipeArray.get(i), i);
                                }

                                if (i < mRecipeArray.size() - 1) {
                                    mRecipeArrayString.append(",");
                                }
                            }
                        }

                        sample = mRecipeArrayString.toString();
                        System.out.println(sample);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Oh no, volley error: "+ error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        APIRequestQueue.getInstance(this.getCacheDir()).getRequestQueue().add(request);
    }

    private void recipeRequest(String url, final RecipeRequestBuilder reqBuilder, final Recipe recipe, final int arrayIndex) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RESPONSE");
                        mRecipeArray.set(arrayIndex, reqBuilder.parseRecipe(recipe, response));
                        if (RLAdapter == null) {
                            displayRecipeList();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RLAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        System.out.println("Recipe Detail: " + recipe.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("LEE: Oh no, volley error: "+ error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };
        APIRequestQueue.getInstance(this.getCacheDir()).getRequestQueue().add(request);
    }


    /**Custom List Array Adapter
     * @author Andrey Barsukov
     * This is a custom adapter that uses a system of recycling a single view
     * to fill up a scrollListView.
     *
     * This class uses a custom view holder class that stores only the nessesary values of
     * a recipe that are required for a recipe list view item.
     *
     * Layout Recycling system:
     *  - create a single instance of a ListView item layout RecipeListItem.xml.
     *  if(ListView is empty){
     *      create a new instance of all views inside RecipeListItem.xml
     *      create an instance of a RecipeViewHolder class
     *          -set values of RecipeViewHolder with the first recipe is RecipeList
     *          -attach each element of RecipeViewHolder to its corresponding element in RecipeListItem.xml
     *          -send the current instance of RecipeViewHolder to the ListView Adapter
     *  }
     *  else{
     *      -set values of RecipeViewHolder with the (position)th recipe is RecipeList
     *       -attach each element of RecipeViewHolder to its corresponding element in RecipeListItem.xml
     *       -send the current instance of RecipeViewHolder to the ListView Adapter
     *
     *       return the current instance of RecipeViewHolder
     *  }
     *
     *  RESULT: Only one instance of RecipeListItem.xml and each of its view is created
     *      -this since instance of it gets recycles for each aditional item in the ListView
     *
     *
     */
    private class RecipeListAdapter extends ArrayAdapter<Recipe>{
        private int layout;      //stores references to recipe_list_item layouts
        public RecipeListAdapter(Context context, int resource, ArrayList<Recipe> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        //setting up items in list
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //instead of creating a new recipe_list_item view, check if it exists,
            //if exits: reuse the view with new data - using .from(getContext()), context returned by super call
            //if does not exist: use inflater to create it (inflater is very taxing)
            int numRecipeIngr = 0, matched = 0;  //used to compute number of ingr matched
            String matchPercent = "NA";
            double p_match = 0;  //Percent of ingr matched
            RecipeViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                RecipeViewHolder viewHolder = new RecipeViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.recipe_list_item_image);
//                viewHolder.clock = (ImageView) convertView.findViewById(R.id.recipe_list_item_clock);
                viewHolder.name = (TextView) convertView.findViewById(R.id.recipe_list_item_name);
                viewHolder.matched = (TextView) convertView.findViewById(R.id.matchedIngrTV);
                //setting text
                viewHolder.name.setText(getItem(position).getName().toString());

                //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-//
                //Picasso
                System.out.println("item: "+ getItem(position));
                System.out.println("url: " + getItem(position).getImageUrl());
                if (!getItem(position).getImageUrl().isEmpty()) {
                    Picasso.with(getContext())
                            .load(getItem(position).getImageUrl().toString())
                            .placeholder(R.drawable.napkin_orange)   // optional
                            .error(R.drawable.napkin_orange)         // optional
                            .into(viewHolder.image);
                }
//                //compute matched ingridents
                for (String  r_ingr : getItem(position).getIngredients()) {
                    for(String s_ingr : getSearchParameters()){
                        if(r_ingr.contains(s_ingr)){
                            matched++;
                        }
                    }
                }

                //set matched ingredients in Recipe object
                //TODO reset ingrMatched somewhere?

                //displayed number of matched ingr
                getItem(position).setIngrMatched(matched);
                p_match = 100 * ((double) matched / (double) getItem(position).getIngredients().size());
                //set % match
                getItem(position).setPercentMatch(p_match);
                matchPercent = String.format("%.2f", p_match);
                viewHolder.matched.setText("Matched:\n" + matchPercent + "%");

                viewHolder.time = (TextView) convertView.findViewById(R.id.recipe_list_item_time);
                //set time
                viewHolder.time.setText(Integer.toString(getItem(position).getCookTime()));


                /*
                add a reference of this object into convertView, so we convertView != null
                    we can retrieve the object and directly set the new data to the RecipeViewHolder items
                     (recyle this object)
                 */
                convertView.setTag(viewHolder);
            }
            else{  //converView != null --> retrieve viewHolder (using tag), assign it to externally declared RecipeViewHolder (mainViewHolder)
                Recipe thisRecipe = getItem(position);
                mainViewHolder = (RecipeViewHolder) convertView.getTag();
                //manually set the data of list view items here//
                mainViewHolder.name.setText(thisRecipe.getName());
                //convert int to string
                mainViewHolder.time.setText(Integer.toString(thisRecipe.getCookTime()));

                //compute matched ingridents
                for (String  r_ingr : getItem(position).getIngredients()) {
                    for(String s_ingr : getSearchParameters()){
                        if(r_ingr.contains(s_ingr)){
                            matched++;
                        }
                    }
                }

                //record ingr matched in currect version of recipe class.
                getItem(position).setIngrMatched(matched);

                //record % matched
                p_match = 100 * ((double) matched / (double) thisRecipe.getIngredients().size());
                thisRecipe.setPercentMatch(p_match);

                matchPercent = String.format("%.2f", p_match);
                mainViewHolder.matched.setText("Matched:\n" + matchPercent + "%");
                if (!thisRecipe.getImageUrl().isEmpty()) {
                    Picasso.with(getContext())
                            .load(thisRecipe.getImageUrl())
                            .placeholder(R.drawable.napkin_orange)   // optional
                            .error(R.drawable.napkin_orange)         // optional
                            .into(mainViewHolder.image);
                } else {
                    mainViewHolder.image.setImageResource(R.drawable.napkin_orange);
                }
            }

            return convertView;
            //return super.getView(position, convertView, parent);
        }
    }

    /**Holds values for ListView adapter
     * @author Andrey Barsukov
     * viewHolder class
     * used to hold the references of data in List<Recipe> array so we dont have to call the array
     * items by id everytime.
     */

    public class RecipeViewHolder {
        ImageView image;
        TextView name;
        TextView time;
        TextView matched;
    }


    public String[] getSearchParameters() {
        return getIntent().getStringArrayExtra("edu.sfsu.napkin.ChosenIngredients");
    }

    /**
     * read data from file: ingrListFile
     *@author Andrey Barsukov
     * @return (String) ingr list
     */
    private String readFromFile() {
        String ret = "";
        try {
            InputStream inputStream = openFileInput(INGR_LIST_FILE);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        //reformat string without []
        return ret;
    }
}