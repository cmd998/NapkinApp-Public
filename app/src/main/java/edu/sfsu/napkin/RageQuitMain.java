package edu.sfsu.napkin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.sfsu.napkin.api.APIRequestQueue;
import edu.sfsu.napkin.api.YelpRequestBuilder;


/**
 * This class is used for the Google Maps activity. It creates a Google Maps fragment and
 * displays the user's location and nearby restaurant with markers.
 * @author Christopher Dea
 * @since 11/2/2015.
 */
public class RageQuitMain extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    private MarkerOptions marker = new MarkerOptions();
    public ListView restaurantListView;
    private RestaurantListAdapter restaurantListAdapter;
    private final YelpRequestBuilder mYelpReqBuilder = YelpRequestBuilder.getInstance();

//    // Alert Dialog Manager
//    AlertDialogManager alert = new AlertDialogManager();

    // GPS Location
    GPSTracker gps;

    /**
     * Is created when the RageQuitMain class is called. It checks to see if Google Play Services are
     * available or not. It also sets up the UI for the Google Maps fragment.
     * @author Christopher Dea
     * @since 11/2/2015.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Get any saved data
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rage_quit_main);

        // creating GPS Class object
        gps = new GPSTracker(this);

        // Point to the name for the layout xml file used


        //Creating the list view with an Array list
        restaurantListView = (ListView) findViewById(R.id.restaurantListView);

        // check if GPS location can get
        if(gps.canGetLocation()) {

            Log.i("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        }

//        else {
//
//            // Can't get user's current location
//            alert.showAlertDialog(RageQuitMain.this, "GPS Status",
//                    "Couldn't get location information. Please enable GPS",
//                    false);
//            // stop executing code by return
//            return;
//        }

        // Google Map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!= ConnectionResult.SUCCESS) {

            // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }

        else {

            // Google Play Services are available
            // Getting reference to the SupportMapFragment of activity_rage_quit_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            gMap = fm.getMap();

            //setting the map type to hybrid
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //zoom control layer
            gMap.getUiSettings().setZoomControlsEnabled(true);

            //compass layer
            gMap.getUiSettings().setCompassEnabled(true);
        }


    }

    /**
     * Creates the Google Maps, displays the user's current location and
     * calls the restaurantListRequest
     * @author Christopher Dea wrote line 155-176 and 183-185)
     * @author Lee Thomas (wrote line 176-182)
     * @since 11/2/2015.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // gets the latlng of the users location
        LatLng latLng = new LatLng(gps.getLatitude(), gps.getLongitude());

        Log.i("Here", String.valueOf(latLng));

        // create marker
        marker.position(latLng)
                .title("You are here!");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red));

        // adding marker
        gMap.addMarker(marker);

        // Showing the current location in Google Map
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        gMap.animateCamera(cameraUpdate);

        String[] searchKeywords = getIntent().getStringArrayExtra("com.edu.sfsu.napkin.ChosenIngredients");
        String url = "";
        try {
            url = mYelpReqBuilder.findRestaurantsByLocation(gps.getLatitude(), gps.getLongitude(), searchKeywords);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("Yelp Query: ", url);
        restaurantListRequest(this, url, mYelpReqBuilder);

    }

    /**
     * Display restaurants with markers
     * @author Christopher Dea (wrote lines 195-206)
     * @since 11/2/2015.
     */
    public void populateMap(ArrayList<Restaurant> restaurants){

        Log.i("populateMaps", "IM HERE");
        //loop that takes the latlng and restaurant's name and create markers for them.
        for (Restaurant restaurant: restaurants) {
            gMap.addMarker(new MarkerOptions()
                .position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()))
                    .title(restaurant.getName())
                    .snippet(restaurant.getDisplayAddressPretty())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_blue)));
        }
    }
    /**
     * Passes the restaurants info as an extra when the user clicks on the restaurant in list view.
     * @author Christopher Dea (wrote lines 212-239)
     * @since 11/2/2015.
     */
    public void buildRestaurantView() {
        restaurantListAdapter = new RestaurantListAdapter(this, R.layout.restaurant_list_item, restaurants);
        restaurantListView.setAdapter(restaurantListAdapter);
        restaurantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Activates SingleRestaurant class on click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Starting new intent
                Restaurant tempRestaurant = (Restaurant) restaurantListView.getItemAtPosition(position);
                Intent detailsIntent = new Intent(getApplicationContext(), SingleRestaurant.class);
                detailsIntent.putExtra("exName", tempRestaurant.getName());
                detailsIntent.putExtra("exAddress",  tempRestaurant.getDisplayAddressPretty());
                detailsIntent.putExtra("exCity",  tempRestaurant.getCity());
                detailsIntent.putExtra("exState",  tempRestaurant.getStateCode());
                detailsIntent.putExtra("exPostalCode",  tempRestaurant.getPostalCode());
                detailsIntent.putExtra("exPhone",  tempRestaurant.getPhone());
                detailsIntent.putExtra("exImageUrl",  tempRestaurant.getImageUrl());
                detailsIntent.putExtra("exRatingImageUrl",  tempRestaurant.getRatingImgUrlLarge());
//                detailsIntent.putExtra("exDistance",  tempRestaurant.getDistance());
                detailsIntent.putExtra("exDistance",  tempRestaurant.getDistanceMiles());

                // Sending place reference id to single place activity
                // place reference id used to get "Place full details"
                startActivity(detailsIntent);
            }
        });
    }


    /**
     * Gathers the restaurants info and parse them in an ArrayList
     * @author Lee Thomas (wrote lines 247 - 280)
     * @since 11/2/2015.
     */
    private void restaurantListRequest(final Context context, final String url, final YelpRequestBuilder reqBuilder){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("YELP RESPONSE: " + response);
                        restaurants = reqBuilder.parseRestaurants(response);
                        populateMap(restaurants);
                        buildRestaurantView();
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
                try {
                    headers.put("Authorization", mYelpReqBuilder.buildOauthHeader("GET", url));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                return headers;
            }
        };

        APIRequestQueue.getInstance(this.getCacheDir()).getRequestQueue().add(request);
    }

    /**
     * Array list adapter for mRestaurantArray
     * Reference: http://www.youtube.com/watch?v=ZEEYYvVwJGY
     * @author Christopher Dea (wrote lines 289-355)
     * @since 11/2/2015.
     */
    private class RestaurantListAdapter extends ArrayAdapter<Restaurant> {
        private int layout;
        public RestaurantListAdapter(Context context, int resource,  ArrayList<Restaurant> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        //setting up items in list
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //instead of creating a new recipe_list_item view, check if it exists,
            //if exits: reuse the view with new data - using .from(getContext()), context returned by super call
            //if does not exist: use inflater to create it (inflater is very taxing)
            RestaurantViewHolder mainViewHolder = null;
            Restaurant restaurant = getItem(position);

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                RestaurantViewHolder viewHolder = new RestaurantViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.restaurant_list_item_image);
                viewHolder.name = (TextView) convertView.findViewById(R.id.restaurant_list_item_name);
                viewHolder.rating = (ImageView) convertView.findViewById(R.id.restaurant_list_item_rating);
//                viewHolder.distance = (TextView) convertView.findViewById(R.id.restaurant_list_item_distance);

                //setting information
                viewHolder.name.setText(restaurant.getName());
//                viewHolder.distance.setText(restaurant.getDistance());
////                viewHolder.distance.setText((restaurant.getDistanceMiles());


                Picasso.with(getContext())
                    .load(restaurant.getImageUrl())
                    .placeholder(R.drawable.napkin_orange)   // optional
                    .error(R.drawable.napkin_orange)         // optional
                    .into(viewHolder.image);

                Picasso.with(getContext())
                        .load(restaurant.getRatingImgUrlLarge())
                        .placeholder(R.mipmap.ic_launcher)   // optional
                        .error(R.mipmap.ic_launcher)         // optional
                        .into(viewHolder.rating);


                /*
                add a reference of this object into convertView, so we convertView != null
                    we can retrieve the object and directly set the new data to the RecipeViewHolder items
                     (recyle this object)
                 */
                convertView.setTag(viewHolder);

            }
            else{
                //convertView != null --> retrieve viewHolder (using tag), assign it to externally declared RecipeViewHolder (mainViewHolder)
                mainViewHolder = (RestaurantViewHolder) convertView.getTag();
                //manually set the data of list view items here//
                mainViewHolder.name.setText(restaurant.getName());
                Picasso.with(getContext())
                    .load(restaurant.getImageUrl())
                    .placeholder(R.drawable.napkin_orange)   // optional
                    .error(R.drawable.napkin_orange)         // optional
                    .into(mainViewHolder.image);
            }

            return convertView;
        }
    }

    /**
     * A viewHolder class used to hold the references of data in List<Restaurant>
     * array so we do not have to call the array
     * @author Christopher Dea (wrote lines 363-368)
     * @since 11/2/2015.
     */
    public class RestaurantViewHolder {
        ImageView image;
        ImageView rating;
        TextView name;
        TextView distance;
    }

}