package edu.sfsu.napkin.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.sfsu.napkin.R;
import edu.sfsu.napkin.RageQuitMain;
import edu.sfsu.napkin.Recipe;
import edu.sfsu.napkin.Wine;
import edu.sfsu.napkin.api.APIRequestQueue;
import edu.sfsu.napkin.api.SnoothRequestBuilder;


public class RecipeDetails extends AppCompatActivity implements LocationListener {
    public String rName, rOriginAPI, rID, rURL, rIngredients, rImageURL;
    public int rPrepTime;
    final public int toSeconds = 1000;
    final public int toMinutes = 60*toSeconds;
    final public int toHours = 60*toMinutes;
    final public int timeMod = 60;

    private SnoothRequestBuilder snooth;
    private ArrayList<Wine> wines;

    public static final String PREVIOUS_SEARCH_FILE = "previous_search";
    private Tutorialclass tutorialClass = new Tutorialclass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //store all extras from previous activity in global variables
        retrieveExtras();

        snooth = SnoothRequestBuilder.getInstance();

        setContentView(R.layout.activity_recipe_details);

        //WEB VIEW
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //disable links on webView
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        webView.loadUrl(rURL);

        ImageView wineIcon = (ImageView) findViewById(R.id.wine_icon);
        wineIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] ingredients = rIngredients.split(",");
                ArrayList<String> ingr = new ArrayList<String>();
                for (String i : ingredients) {
                    ingr.add(i);
                }
                wineRequest(snooth.findByRecipe(
                        new Recipe(rOriginAPI, rID, rName, "", "", rPrepTime, ingr, rURL, rImageURL)), snooth);
            }
        });

        final TextView timerTV = (TextView) findViewById(R.id.details_time_tv);
        //TIMER
        int hours, minutes, seconds;

        //            setting timer to prep time     1000milisec conversion
        new CountDownTimer(rPrepTime * toMinutes, toSeconds) {

            public void onTick(long millisUntilFinished) {
                String showTime = String.format("%02d:%02d:%02d", (millisUntilFinished / toHours)%timeMod, (millisUntilFinished / toMinutes)%timeMod , (millisUntilFinished / toSeconds)%timeMod);
                //timerTV.setText((millisUntilFinished / toHours)%timeMod + ":" + (millisUntilFinished / toMinutes)%timeMod + ":" + (millisUntilFinished / toSeconds)%timeMod);
                timerTV.setText(showTime);
            }

            public void onFinish() {
                timerTV.setText("Done!");
            }
        }.start();

        Toast.makeText(getApplicationContext(), "Loading... please wait.",
                Toast.LENGTH_LONG).show();
    }

    private void wineRequest(String url, final SnoothRequestBuilder reqBuilder) {
        System.out.print(url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        wines = new ArrayList<Wine>();
                        wines.addAll(reqBuilder.parseDrinks(response));

                        try {
                            System.out.print(response);
                            wineDetailRequest(snooth.findWineById(wines.get(0)), reqBuilder, wines.get(0));
                        }
                        catch (Exception e) {
                            e.printStackTrace();

                            final Dialog drinkDialog = new Dialog(RecipeDetails.this);
                            drinkDialog.setContentView(R.layout.drink_dialog);
                            drinkDialog.setTitle("Sorry!");

                            // set the custom dialog components - text, image and button
                            TextView drinkVintage = (TextView) drinkDialog.findViewById(R.id.drink_vintage_tv);
                            drinkVintage.setVisibility(View.GONE);

                            TextView drinkName = (TextView) drinkDialog.findViewById(R.id.drink_name_tv);
                            drinkName.setText("We couldn't find any wines for this recipe.");

                            TextView drinkWinery = (TextView) drinkDialog.findViewById(R.id.drink_winery_tv);
                            drinkWinery.setVisibility(View.GONE);

                            TextView drinkVarietal = (TextView) drinkDialog.findViewById(R.id.drink_varietal_tv);
                            drinkVarietal.setVisibility(View.GONE);

                            TextView drinkType = (TextView) drinkDialog.findViewById(R.id.drink_type);
                            drinkType.setVisibility(View.GONE);

                            TextView drinkDescription = (TextView) drinkDialog.findViewById(R.id.drink_description_scrollView);
                            drinkDescription.setVisibility(View.GONE);

                            Button dialogButton = (Button) drinkDialog.findViewById(R.id.dismiss_drinkdialog);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    drinkDialog.dismiss();
                                }
                            });

                            drinkDialog.show();
                        }
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

    private void wineDetailRequest(String url, final SnoothRequestBuilder reqBuilder, final Wine wine) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RESPONSE");
                        wines.set(0, reqBuilder.parseWine(wine, response));

                        Wine w = wines.get(0);
                        int vintage = w.getVintage();
                        String name = w.getName();
                        String winery = w.getWinery();
                        String varietal = w.getVarietal();
                        String type = w.getType();
                        String description = w.getDescription();
                        String imageULR = w.getImageUrl();
                        imageULR = imageULR.replace("https://", "");
                        System.out.println("WINE URL: " + imageULR);


                        String tempType = type.toLowerCase();
                        if (tempType.contains(" wine") && (tempType.contains("white") || tempType.contains("red"))) {
                            type = type.substring(0, type.indexOf(" Wine"));
                        }

                        final Dialog drinkDialog = new Dialog(RecipeDetails.this);
                        drinkDialog.setContentView(R.layout.drink_dialog);
                        drinkDialog.setTitle("We recommend");

                        // set the custom dialog components - text, image and button
                        TextView drinkVintage = (TextView) drinkDialog.findViewById(R.id.drink_vintage_tv);
                        if (vintage > 0) {
                            drinkVintage.setText(Integer.toString(vintage));
                        }
                        else {
                            drinkVintage.setVisibility(View.GONE);
                        }

                        TextView drinkName = (TextView) drinkDialog.findViewById(R.id.drink_name_tv);
                        drinkName.setText(name);

                        TextView drinkWinery = (TextView) drinkDialog.findViewById(R.id.drink_winery_tv);
                        if (!winery.equals("Unknown")) {
                            drinkWinery.setText(winery);
                        }
                        else {
                            drinkWinery.setVisibility(View.GONE);
                        }

                        TextView drinkVarietal = (TextView) drinkDialog.findViewById(R.id.drink_varietal_tv);
                        if (!varietal.equals("Unknown")) {
                            drinkVarietal.setText(varietal);
                        }
                        else {
                            drinkVarietal.setVisibility(View.GONE);
                        }

                        TextView drinkType = (TextView) drinkDialog.findViewById(R.id.drink_type);
                        if (!drinkType.equals("Unknown")) {
                            drinkType.setText(type);
                        }
                        else {
                            drinkType.setVisibility(View.GONE);
                        }

                        TextView drinkDescription = (TextView) drinkDialog.findViewById(R.id.drink_description_scrollView);
                        if (!description.equals("No description provided.")) {
                            drinkDescription.setText(description);
                        }
                        else {
                            drinkDescription.setText("It's a mystery...");
                        }

                        ImageView drinkImage = (ImageView) drinkDialog.findViewById(R.id.drink_image_iv);
                        Picasso.with(drinkDialog.getContext())
                                .load(imageULR)
                                .placeholder(R.drawable.napkin_orange)   // optional
                                .error(R.drawable.napkin_orange)         // optional
                                .into(drinkImage);

                        Button dialogButton = (Button) drinkDialog.findViewById(R.id.dismiss_drinkdialog);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drinkDialog.dismiss();
                            }
                        });

                        drinkDialog.show();
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


    /** retrieve the values of recipe details passed through .putExtras()
     *@author Andrey Barsukov
     */
    private void retrieveExtras() {
        rName = getIntent().getExtras().getString("exName");
        rOriginAPI = getIntent().getExtras().getString("exAPI");
        rPrepTime = getIntent().getExtras().getInt("exTime");
        rIngredients = getIntent().getExtras().getString("exIngredients");
        rID = getIntent().getExtras().getString("exID");
        rURL = getIntent().getExtras().getString("exURL");
        rImageURL = getIntent().getExtras().getString("exImgURL");

        saveDetailsSharedPrefs();
    }

    /** save details of clicked recipe to display on Home page
     *@author Andrey Barsukov
     * after user selects a recipe, it will be saved a displayed next time home screen is shown
     */
    private void saveDetailsSharedPrefs() {
        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.saved_recipe_file), MODE_PRIVATE);
        SharedPreferences.Editor detailEditor = sharedPrefs.edit();

        detailEditor.putString(getString(R.string.recipe_name), rName);
        detailEditor.putString(getString(R.string.recipe_API), rOriginAPI);
        detailEditor.putInt(getString(R.string.recipe_time), rPrepTime);

        detailEditor.putString(getString(R.string.recipe_ingr), rIngredients);

        detailEditor.putString(getString(R.string.recipe_ID), rID);
        detailEditor.putString(getString(R.string.recipe_URL), rURL);
        detailEditor.commit();
    }

    /** builds and returns the details of a recipe
     *@author Andrey Barsukov
     * @return String details
     */
    private String getRecipeDetails() {
        String details;
        details = String.format("Name: %s\nOrigin API: %s\nIngredients: \n%s\nRecipe ID: %s\nInstructions URL: %s\n\nImage URL: %s", rName, rOriginAPI, rIngredients, rID, rURL, rImageURL);

        return details;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_details, menu);
        return true;
    }

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

    //Method to go to Home screen
    public void home() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    //Method to go to Ingredient screen
    public void ingredient() {
        Intent intent = new Intent(this, IngredientInput.class);
        startActivity(intent);
    }

    //Method to go to Settings screen
    public void settings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    //Method to go to Tutorial screen
    public void tutorial() {
        Intent goToTutorial = new Intent(this, Tutorialclass.class);
        startActivity(goToTutorial);
    }

    //Method to go to About screen
    public void about() {
        Intent goToAbout = new Intent(this, About.class);
        startActivity(goToAbout);
    }

    /**return most recently searched ingredients as a Set<String>
     * @author Andrey Barsukov
     * @return String set ingredients
     */
    private Set<String> getPrevIngrList(){
        SharedPreferences sharedPrefs = getSharedPreferences(PREVIOUS_SEARCH_FILE, MODE_PRIVATE);
        return sharedPrefs.getStringSet("saved_ingr_list", null);
    }

    /**
     * Checks if the phones GPS is on or off
     * @author Christopher Dea (wrote line 434 - 462)
     * @since 10/15/15
     */
    public void checkGPS(View view){

        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            String[] searchParams = getPrevIngrList().toArray(new String[getPrevIngrList().size()]);

//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//            if(location.getLatitude() == 0.0 && location.getLongitude() ==  0.0){
//                public void onLocationChanged(Location location) {
//                    if (location != null) {
//                        Log.i("Location Changed", location.getLatitude() + " and " + location.getLongitude());
//                        locationManager.removeUpdates(this);
//                    }
//                }
//            }

                Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, RageQuitMain.class);
                intent.putExtra("com.edu.sfsu.napkin.ChosenIngredients", searchParams);
                startActivity(intent);
            }

        else{

            showGPSDisabledAlertToUser();
        }

    }

    /**
     * Displays an alert dialog that prompts the user that his or her GPS is disabled.
     * If the user clicks on "Go To Settings", it will take them to the phones location services settings.
     * If the user clicks on "Cancel" it does nothing and does not go to RageQuitMain.
     * @author Christopher Dea (wrote line 467 -510)
     * @since 10/15/15
     */
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?" +
                "  \n\nNOTE: After enabling GPS Services, please allow a few seconds for your GPS coordinates to be updated")
                .setCancelable(false)
                .setPositiveButton("Go to Settings",
                        //have the phone to the devices location settings
                        new DialogInterface.OnClickListener() {
                            //Goes to the phone location settings
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            //Does nothing until user turns on GPS for Google Maps
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}