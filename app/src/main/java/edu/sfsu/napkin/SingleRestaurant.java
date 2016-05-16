package edu.sfsu.napkin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.sfsu.napkin.activity.About;
import edu.sfsu.napkin.activity.Home;
import edu.sfsu.napkin.activity.IngredientInput;
import edu.sfsu.napkin.activity.Settings;
import edu.sfsu.napkin.activity.Tutorialclass;

/**
 * SingleRestaurant class displays all the information that the user clicked on in the listview of RageQuitMain.
 * @author Christopher Dea (wrote line 27 - 234 )
 * @since 11/14/2015.
 */
public class SingleRestaurant extends AppCompatActivity {
    public String details = "";
    public String singleRestaurantName, singleRestaurantAddress, singleRestaurantCity, singleRestaurantState,
            singleRestaurantPostalCode, singleRestaurantPhone, singleRestaurantImageUrl, singleRestaurantRatingImageUrl,
            singleRestaurantDistance;

    /**
     * Gets created when the class it's called. It displays the restaurants'
     * name, address, phone number, distance from the user and a rating from Yelp
     * @author Christopher Dea (wrote line 40-77)
     * @since 11/14/2015.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        retrieveExtras();

        TextView restaurantNameTV = (TextView) findViewById(R.id.restaurant_name_text_view);
        restaurantNameTV.setText(getRestaurantName());

        TextView restaurantAddressTV = (TextView) findViewById(R.id.restaurant_address_text_view);
        restaurantAddressTV.setText(getRestaurantAddress());
        Linkify.addLinks(restaurantAddressTV, Linkify.MAP_ADDRESSES);


        TextView restaurantPhoneTV = (TextView) findViewById(R.id.restaurant_phone_text_view);
        restaurantPhoneTV.setText(getRestaurantPhone());
        Linkify.addLinks(restaurantPhoneTV, Linkify.PHONE_NUMBERS);

        ImageView iv = (ImageView) findViewById(R.id.restaurant_image);
        Picasso.with(this)
                .load(singleRestaurantImageUrl)
                .placeholder(R.mipmap.ic_launcher) // optional
                .error(R.mipmap.ic_launcher)         // optional
                .into(iv);

        ImageView ratingView = (ImageView) findViewById(R.id.rating);
        Picasso.with(this)
                .load(singleRestaurantRatingImageUrl)
                .placeholder(R.mipmap.ic_launcher) // optional
                .error(R.mipmap.ic_launcher)         // optional
                .into(ratingView);

        TextView restaurantDistanceTV = (TextView) findViewById(R.id.restaurant_distance_text_view);
        restaurantDistanceTV.setText(getRestaurantDistance());

    }

    /**
     * Gets all the extras from RageQuitMain and stores them in public variables in the class.
     * Used if someone wanted to get the same information multiple times.
     * @author Christopher Dea (wrote line 85-95)
     * @since 11/2/2015.
     */
    private void retrieveExtras() {

        singleRestaurantName = getIntent().getExtras().getString("exName");
        singleRestaurantAddress = getIntent().getExtras().getString("exAddress");
        singleRestaurantCity = getIntent().getExtras().getString("exCity");
        singleRestaurantState = getIntent().getExtras().getString("exState");
        singleRestaurantPostalCode = getIntent().getExtras().getString("exPostalCode");
        singleRestaurantPhone = getIntent().getExtras().getString("exPhone");
        singleRestaurantImageUrl = getIntent().getExtras().getString("exImageUrl");
        singleRestaurantRatingImageUrl = getIntent().getExtras().getString("exRatingImageUrl");
        singleRestaurantDistance = getIntent().getExtras().getString("exDistance");
    }

    /**
     * Displays the restaurants name
     * @author Christopher Dea (wrote line 103-109)
     * @since 11/14/2015.
     */
    private String getRestaurantName() {

        String name;
        name = String.format("Name: %s", singleRestaurantName);

        return name;
    }

    /**
     * Displays the restaurants address
     * @author Christopher Dea (wrote line 116-122)
     * @since 11/14/2015.
     */
    private String getRestaurantAddress(){

        String address;
        address = String.format("Address: %s %s, %s %s",
                singleRestaurantAddress, singleRestaurantCity, singleRestaurantState, singleRestaurantPostalCode);
        return address;
    }

    /**
     * Displays the restaurants phone
     * @author Christopher Dea (wrote line 129-134)
     * @since 11/14/2015.
     */
    private String getRestaurantPhone(){

        String phone;
        phone = String.format("Phone: %s", singleRestaurantPhone);
        return phone;
    }

    /**
     * Displays the restaurants distance in miles
     * @author Christopher Dea (wrote line 141-146)
     * @since 11/14/2015.
     */
    private String getRestaurantDistance(){

        String distance;
        distance = String.format("Distane %s", singleRestaurantDistance);
        return distance;
    }

    /**
     * Code for the Nav bar. Keeping it consistent with the other activity
     * @author Christopher Dea (wrote line 154-160)
     * @since 11/2/2015.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_navigation_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *Switch statement that calls and starts the activity of another class.
     * @author Christopher Dea (wrote line 168-189)
     * @since 11/2/2015.
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
     */
    public void home() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    /**
     * Method to go to Ingredient screen
     */
    public void ingredient() {
        Intent intent = new Intent(this, IngredientInput.class);
        startActivity(intent);
    }

    /**
     * Method to go to Setting screen
     */
    public void settings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    /**
     * Method to go to Tutorial screen
     */
    public void tutorial() {
//        tutorialClass.setRunStatus(this, true);

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