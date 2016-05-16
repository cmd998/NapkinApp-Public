package edu.sfsu.napkin.api;

import com.google.api.client.auth.oauth.OAuthHmacSigner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import edu.sfsu.napkin.Restaurant;

/**
 * Created by leethomas on 10/9/15.
 * Used for generating urls for and parsing API responses from Yelp
 */
public class YelpRequestBuilder extends RequestBuilder {
    private final String HOST                    = "https://api.yelp.com/v2";
    private final String EP_SEARCH               = "/search";
    private final String OAUTH_CONSUMER_SECRET   = "IDlnKy2QD5FDSsBUiAY_LwL0RMQ";
    private final String OAUTH_CONSUMER_KEY      = "AGUxnJORZvjSN5Va0N0DSQ";
    private final String OAUTH_TOKEN             = "FOxJo6mKQGLGgT19xDEo6FfI_1mQY0eG";
    private final String OAUTH_TOKEN_SECRET      = "FYyXx4s5sPzlpH0UScQD9caZTfQ";
    private final String OAUTH_SIGNATURE_METHOD  = "HMAC-SHA1";
    private final String OAUTH_VERSION           = "1.0";

    private static YelpRequestBuilder instance = null;

    public YelpRequestBuilder() throws UnsupportedEncodingException {
        oauthRequired = true;
        baseUrl = HOST;
        oauthParams = new TreeMap<String, String>();
        oauthParams.put("oauth_token", OAUTH_TOKEN);
        oauthParams.put("oauth_consumer_key", OAUTH_CONSUMER_KEY);
        oauthParams.put("oauth_signature_method", OAUTH_SIGNATURE_METHOD);
        oauthParams.put("oauth_version", OAUTH_VERSION);

        signingKey = new OAuthHmacSigner();
        ((OAuthHmacSigner) signingKey).tokenSharedSecret = OAUTH_TOKEN_SECRET;
        ((OAuthHmacSigner) signingKey).clientSharedSecret = OAUTH_CONSUMER_SECRET;
    }

    /**
     * Return a singleton instance of this class.
     * @return A singleton instance of this class.
     */
    public static YelpRequestBuilder getInstance() {
        if (instance == null) {
            try {
                instance = new YelpRequestBuilder();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Parses a Yelp JSON restaurant search response into Restaurant objects
     * @param json JSON Yelp restaurant search response
     * @return A list of Restaurant objects
     */
    public ArrayList<Restaurant> parseRestaurants(JSONObject json) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        JSONArray businesses;

        try {
            businesses = json.getJSONArray("businesses");
            for (int i = 0; i < businesses.length(); i++) {
                JSONObject business = businesses.getJSONObject(i);
                JSONObject location = business.getJSONObject("location");
                JSONObject coordinates = business.getJSONObject("location").getJSONObject("coordinate");
                restaurants.add(new Restaurant(
                        business.getString("name"),
                        location.getJSONArray("address"),
                        location.getString("city"),
                        location.getString("state_code"),
                        location.getString("postal_code"),
                        coordinates.getDouble("latitude"),
                        coordinates.getDouble("longitude"),
                        business.getString("phone"),
                        business.getString("image_url"),
                        business.getString("rating_img_url_large"),
                        business.getDouble("distance")
                ));
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return restaurants;
    }

    /**
     * Creates a URL for searching for nearby restaurants using Yelp
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param keywords An array of keywords to be used to search for restaurants
     * @return A Yelp URL with the parameters encoded
     * @throws UnsupportedEncodingException
     */
    public String findRestaurantsByLocation(double latitude, double longitude, String... keywords) throws UnsupportedEncodingException {
        HashMap<String, String> params = new HashMap<>();

        params.put("ll", new StringBuilder().append(latitude).append(",").append(longitude).toString());
        StringBuilder keywordList = new StringBuilder();
        for (String keyword: keywords) {
            keywordList.append(keyword).append(",");
        }
        keywordList.append("restaurants,");
        keywordList.deleteCharAt(keywordList.length() - 1);
        params.put("term", keywordList.toString());
        //again, bad idea to hardcode but...eh
        params.put("limit", "10");
        params.put("radius_filter", "40000");// max amount, 40,000m == 25 miles
        return buildQueryString(EP_SEARCH, params);
    }
}
